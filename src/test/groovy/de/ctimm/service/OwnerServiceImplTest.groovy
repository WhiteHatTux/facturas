package de.ctimm.service

import de.ctimm.TestDataCreator
import de.ctimm.dao.BillDao
import de.ctimm.dao.OwnerRepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import groovy.time.Duration
import groovy.time.TimeCategory
import org.junit.Before

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyInt
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
class OwnerServiceImplTest extends GroovyTestCase {

    OwnerService ownerService
    BillDao billDao
    OwnerRepository ownerRepository

    TestDataCreator testDataCreator = new TestDataCreator()
    String testResponse = testDataCreator.testResponse
    String testBill = testDataCreator.testBill
    String testNotificationData = testDataCreator.testNotificationData

    @Before
    void setUp() {
        billDao = mock(BillDao.class)
        when(billDao.getBillXml(any(Bill.class))).thenReturn(testBill)
        when(billDao.getBillHtml(anyInt())).thenReturn(testResponse)
        when(billDao.getOwnerHtml(anyInt())).thenReturn(testNotificationData)

        ResponseParser responseParser = new ResponseParser(billDao);
        ownerRepository = new OwnerRepository();
        ownerService = new OwnerServiceImpl(responseParser, ownerRepository, billDao)
    }

    void testUpdateOwner() {
        Owner owner = new Owner(194799)
        owner.name = "something"
        ownerRepository.ownerRepository.put(194799, owner)
        ownerService.updateOwner(194799)
        Owner actualOwner = ownerRepository.ownerRepository.get(194799)
        assertEquals(22, actualOwner.billsList.size())
        assertEquals("LOPEZ ESCOBAR  ROBERTO PABLO ", actualOwner.name)
    }

    void testGetOwner() {
        Owner owner = new Owner(194799)
        owner.name = "Payaso"
        ownerRepository.ownerRepository.put(194799, owner)
        Owner actualOwner = ownerService.getOwner(194799)
        assertEquals(194799, actualOwner.account)
        assertEquals("Payaso", actualOwner.name)
    }

    void testAddOwner() {
        Owner owner = new Owner(194799)
        ownerService.addOwner(owner)
        Owner actualOwner = ownerRepository.ownerRepository.get(194799)
        assertEquals(owner, actualOwner)
    }

    void testDeleteOwner() {
        ownerRepository.ownerRepository.put(194799, new Owner(194799))
        ownerService.deleteOwner(194799)
        assertEquals(0, ownerRepository.ownerRepository.size())
    }

    void testRemoveExpired() {
        Owner owner = new Owner(194799)
        def yesterday = TimeCategory.minus(owner.collectionTimestamp, new Duration(1, 0, 0, 0, 0))
        owner.collectionTimestamp = yesterday
        ownerService.addOwner(owner)
        ownerService.updateExpired()
        assertNotSame(yesterday, ownerRepository.ownerRepository.get(194799).collectionTimestamp)
    }
}
