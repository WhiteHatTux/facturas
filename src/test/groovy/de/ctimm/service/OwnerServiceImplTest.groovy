package de.ctimm.service

import de.ctimm.TestDataCreator
import de.ctimm.dao.BillDao
import de.ctimm.dao.BillJPARepository
import de.ctimm.dao.OwnerJPARepository
import de.ctimm.domain.Owner
import groovy.time.Duration
import groovy.time.TimeCategory
import org.junit.Before
import org.mockito.ArgumentCaptor

import static org.mockito.Matchers.anyInt
import static org.mockito.Mockito.*

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class OwnerServiceImplTest extends GroovyTestCase {

    OwnerService ownerService
    BillDao billDao
    OwnerJPARepository ownerRepository
    BillJPARepository billJPARepository

    final ArgumentCaptor<Owner> ownerArgumentCaptor = ArgumentCaptor.forClass(Owner.class)

    TestDataCreator testDataCreator = new TestDataCreator()
    String testResponse = testDataCreator.testResponse
    String testBill = testDataCreator.testBill
    String testNotificationData = testDataCreator.testNotificationData

    @Before
    void setUp() {
        billDao = mock(BillDao.class)
        when(billDao.getBillXml(anyInt())).thenReturn(testBill)
        when(billDao.getBillHtml(anyInt())).thenReturn(testResponse)
        when(billDao.getOwnerHtml(anyInt())).thenReturn(testNotificationData)

        ResponseParser responseParser = new ResponseParser(billDao);
        ownerRepository = mock(OwnerJPARepository.class);
        billJPARepository = mock(BillJPARepository.class)
        ownerService = new OwnerServiceImpl(responseParser, ownerRepository, billDao, billJPARepository)
        ownerArgumentCaptor
    }

    void testUpdateOwner() {
        Owner owner = new Owner(194799)
        owner.name = "something"
        ownerService.updateOwner(194799)
        verify(ownerRepository).save(ownerArgumentCaptor.capture())
        Owner actualOwner = ownerArgumentCaptor.getValue()
        assertEquals(22, actualOwner.billsList.size())
        assertEquals("LOPEZ ESCOBAR  ROBERTO PABLO ", actualOwner.name)
    }

    void testGetOwner() {
        Owner owner = new Owner(194799)
        owner.name = "Payaso"
        when(ownerRepository.findByAccount(194799)).thenReturn(owner)
        Owner actualOwner = ownerService.getOwner(194799)
        verify(billDao, times(0)).getOwnerHtml(anyInt())
        assertEquals(194799, actualOwner.account)
        assertEquals("Payaso", actualOwner.name)
    }

    void testAddOwner() {
        Owner owner = new Owner(194799)
        ownerService.addOwner(owner)
        verify(ownerRepository).save(ownerArgumentCaptor.capture())
        Owner actualOwner = ownerArgumentCaptor.getValue()
        assertEquals(owner, actualOwner)
    }

    void testDeleteOwnerNoExist() {
        ownerService.deleteOwner(194799)
        // If the owner doesn't exist, deletion is not invoked
        verify(ownerRepository, times(0)).save(ownerArgumentCaptor.capture())

    }

    void testDeleteOwnerExist() {
        Owner owner = new Owner(194799)
        owner.name = "Payaso"
        when(ownerRepository.findByAccount(194799)).thenReturn(owner)
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class)
        ownerService.deleteOwner(194799)
        verify(ownerRepository, times(1)).deleteByAccount(integerArgumentCaptor.capture())
        Integer actualDeletedOwner = integerArgumentCaptor.getValue()
        assertEquals(194799, actualDeletedOwner)
    }

    void testRemoveExpired() {
        Owner owner = new Owner(194799)
        def yesterday = TimeCategory.minus(owner.collectionTimestamp, new Duration(1, 0, 0, 0, 0))
        owner.collectionTimestamp = yesterday
        ownerService.addOwner(owner)
        verify(ownerRepository).save(ownerArgumentCaptor.capture())
        when(ownerRepository.findAll()).thenReturn(Collections.singletonList(ownerArgumentCaptor.getValue()))
        ownerService.updateExpired()
        verify(ownerRepository, times(2)).save(ownerArgumentCaptor.capture())
        Owner actualOwner = ownerArgumentCaptor.getValue()
        assertNotSame(yesterday, actualOwner.collectionTimestamp)
    }
}
