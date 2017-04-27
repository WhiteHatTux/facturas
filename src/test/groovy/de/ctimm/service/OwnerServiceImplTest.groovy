package de.ctimm.service

import de.ctimm.TestDataCreator
import de.ctimm.dao.BillDao
import de.ctimm.dao.BillJPARepository
import de.ctimm.dao.OwnerJPARepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import groovy.time.Duration
import groovy.time.TimeCategory
import org.junit.Before
import org.mockito.ArgumentCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class OwnerServiceImplTest extends GroovyTestCase {

    private static final Integer testAccount = TestDataCreator.testAccount
    OwnerService ownerService
    BillDao billDao
    OwnerJPARepository ownerRepository
    BillJPARepository billJPARepository

    final ArgumentCaptor<Owner> ownerArgumentCaptor = ArgumentCaptor.forClass(Owner.class)

    TestDataCreator testDataCreator = new TestDataCreator()
    String testResponse = testDataCreator.testResponse
    String testBill = testDataCreator.testBill
    String testNotificationData = testDataCreator.testNotificationData

    int billReturnCounter = 1

    @Before
    void setUp() {
        billDao = mock(BillDao.class)
        when(billDao.getBillXml(anyInt())).thenReturn(testBill)
        when(billDao.getBillHtml(anyInt())).thenReturn(testResponse)
        when(billDao.getOwnerHtml(anyInt())).thenReturn(testNotificationData)

        ResponseParser responseParser = new ResponseParser(billDao)

        ownerRepository = mock(OwnerJPARepository.class)
        when(ownerRepository.save(any(Owner.class))).thenAnswer(new Answer<Owner>() {
            @Override
            Owner answer(InvocationOnMock invocationOnMock) throws Throwable {
                Owner savedOwner = (Owner) invocationOnMock.getArgumentAt(0, Owner.class)
                savedOwner.id = 1
                return savedOwner
            }
        })

        billJPARepository = mock(BillJPARepository.class)
        when(billJPARepository.save(any(Bill.class))).thenAnswer(new Answer<Bill>() {
            @Override
            Bill answer(InvocationOnMock invocationOnMock) throws Throwable {
                Bill bill = invocationOnMock.getArgumentAt(0, Bill.class)
                bill.id = billReturnCounter
                billReturnCounter++
                return bill
            }
        })

        ownerService = new OwnerServiceImpl(responseParser, ownerRepository, billDao, billJPARepository)
        ownerArgumentCaptor
    }

    void testUpdateOwner() {
        Owner owner = new Owner(testAccount)
        owner.name = "something"
        ownerService.updateOwner(testAccount)
        // The owner does not yet exist, so it is saved before adding the bills and after (2 times in total)
        verify(ownerRepository, times(2)).save(ownerArgumentCaptor.capture())
        Owner actualOwner = ownerArgumentCaptor.getValue()
        assertEquals(22, actualOwner.billsList.size())
        assertEquals("LOPEZ ESCOBAR  ROBERTO PABLO ", actualOwner.name)
    }


    void testUpdateExistingOwner() {
        Owner owner = new Owner(testAccount)
        owner.name = "something"
        when(ownerRepository.findByAccount(testAccount)).thenReturn(testDataCreator.createTestOwner())
        ownerService.updateOwner(testAccount)
        // The owner already exists, so it is saved only once after saving the bills
        verify(ownerRepository).save(ownerArgumentCaptor.capture())
        Owner actualOwner = ownerArgumentCaptor.getValue()
        assertEquals(22, actualOwner.billsList.size())
        assertEquals("LOPEZ ESCOBAR  ROBERTO PABLO ", actualOwner.name)
    }

    void testGetOwner() {
        Owner owner = new Owner(testAccount)
        owner.name = "Payaso"
        when(ownerRepository.findByAccount(testAccount)).thenReturn(owner)
        Owner actualOwner = ownerService.getOwner(testAccount)
        verify(billDao, times(0)).getOwnerHtml(anyInt())
        assertEquals(testAccount, actualOwner.account)
        assertEquals("Payaso", actualOwner.name)
    }

    void testAddOwner() {
        Owner owner = new Owner(testAccount)
        ownerService.addOwner(owner)
        verify(ownerRepository).save(ownerArgumentCaptor.capture())
        Owner actualOwner = ownerArgumentCaptor.getValue()
        assertEquals(owner, actualOwner)
    }

    void testDeleteOwnerNoExist() {
        ownerService.deleteOwner(testAccount)
        // If the owner doesn't exist, deletion is not invoked
        verify(ownerRepository, times(0)).save(ownerArgumentCaptor.capture())

    }

    void testDeleteOwnerExist() {
        Owner owner = new Owner(testAccount)
        owner.name = "Payaso"
        when(ownerRepository.findByAccount(testAccount)).thenReturn(owner)
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class)
        ownerService.deleteOwner(testAccount)
        verify(ownerRepository, times(1)).deleteByAccount(integerArgumentCaptor.capture())
        Integer actualDeletedOwner = integerArgumentCaptor.getValue()
        assertEquals(testAccount, actualDeletedOwner)
    }

    void testRemoveExpired() {
        Owner owner = new Owner(testAccount)
        def yesterday = TimeCategory.minus(owner.collectionTimestamp, new Duration(1, 0, 0, 0, 0))
        owner.collectionTimestamp = yesterday
        ownerService.addOwner(owner)
        verify(ownerRepository).save(ownerArgumentCaptor.capture())
        when(ownerRepository.findAll()).thenReturn(Collections.singletonList(ownerArgumentCaptor.getValue()))
        when(ownerRepository.findByAccount(eq(testAccount))).thenReturn(ownerArgumentCaptor.getValue())
        ownerService.updateExpired()
        verify(ownerRepository, times(2)).save(ownerArgumentCaptor.capture())
        Owner actualOwner = ownerArgumentCaptor.getValue()
        assertNotSame(yesterday, actualOwner.collectionTimestamp)
    }


    void testGetAllAccounts() {
        Owner actualOwner = new Owner(testAccount)
        when(ownerRepository.findAll()).thenReturn(Collections.singletonList(actualOwner))
        assertEquals(Collections.singletonList(testAccount), ownerService.getAccountList())
    }
}
