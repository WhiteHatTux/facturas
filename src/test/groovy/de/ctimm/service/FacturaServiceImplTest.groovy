package de.ctimm.service

import de.ctimm.TestDataCreator
import de.ctimm.dao.BillDao
import de.ctimm.dao.OwnerRepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import org.junit.Before

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
class FacturaServiceImplTest extends GroovyTestCase {
    ResponseParser responseParser
    FacturaService facturaService
    OwnerService ownerService
    OwnerRepository ownerRepository
    BillDao billDao
    Integer testAccount = 194799

    TestDataCreator testDataCreator = new TestDataCreator()
    String testResponse = testDataCreator.testResponse
    String testBill = testDataCreator.testBill
    String testBill1 = testDataCreator.testBill1
    String testNotificationData = testDataCreator.testNotificationData

    @Before
    void setUp() {
        billDao = mock(BillDao.class)
        when(billDao.getBillXml(eq(6069973))).thenReturn(testBill)
        when(billDao.getBillXml(eq(5810666))).thenReturn(testBill1)
        when(billDao.getBillHtml(anyInt())).thenReturn(testResponse)
        when(billDao.getOwnerHtml(anyInt())).thenReturn(testNotificationData)


        responseParser = new ResponseParser(billDao)
        ownerRepository = new OwnerRepository()
        ownerService = new OwnerServiceImpl(responseParser, ownerRepository, billDao)
        facturaService = new FacturaServiceImpl(responseParser, billDao, ownerService)
    }

    void testGetTotalAmount() {
        Double actualResult = facturaService.getTotalAmount(testAccount, 0)
        Double expectedResult = 27.51
        assertEquals(expectedResult, actualResult)
    }

    void testGetOwnerName() {
        String actualResult = facturaService.getOwnerName(testAccount, 0)
        String expectedResult = 'LOPEZ ESCOBAR ROBERTO PABLO '

        assertEquals(expectedResult, actualResult)
    }

    void testGetIdentification() {
        String actualResult = facturaService.getIdentification(testAccount, 0)
        String expectedReult = '0200989077'

        assertEquals(expectedReult, actualResult)
    }

    void testGetOwner() {
        Owner actualResultOwner = facturaService.getOwner(testAccount)
        Owner expectedResultOwner = testDataCreator.createTestOwner()

        compareOwners(expectedResultOwner, actualResultOwner)
    }

    static void compareOwners(Owner expected, Owner actual) {
        actual.properties.each { def key, def value ->
            if (key != "collectionTimestamp" && key != "billsList") {
                assertEquals(expected.(key.toString()), value)
            }
        }
    }

    void testGetSummary() {
        Owner expectedResultOwner = testDataCreator.createTestOwner()
        Map<String, Object> expectedResult = new HashMap<>()
        expectedResult.put("Total", "27.51")
        expectedResult.put("Identification", "0200989077")
        expectedResult.put("Discounts", "0.0")
        expectedResult.put("Owner", expectedResultOwner)
        expectedResult.put("Issued", "2016-10-18 00:00:00.0")

        def actualResult = facturaService.getSummary(testAccount, false)
        compareOwners(expectedResultOwner, (Owner) actualResult.get("Owner"))
        assertEquals(expectedResult, actualResult)

        verify(billDao, times(1)).getBillHtml(eq(194799))

        def actualResult2 = facturaService.getSummary(testAccount, true)
        compareOwners(expectedResultOwner, (Owner) actualResult2.get("Owner"))
        assertEquals(expectedResult, actualResult2)

        verify(billDao, times(2)).getBillHtml(eq(194799))
    }

    void testGetSummaryForOldBill() {
        Owner expectedResultOwner = testDataCreator.createTestOwner()
        Map<String, Object> expectedResult = new HashMap<>()
        expectedResult.put("Total", "16.64")
        expectedResult.put("Identification", "0200989077")
        expectedResult.put("Discounts", "0.0")
        expectedResult.put("Owner", expectedResultOwner)
        expectedResult.put("Issued", "2016-09-18 00:00:00.0")

        def actualResult = facturaService.getSummaryForBill(testAccount, 1)
        compareOwners(expectedResultOwner, (Owner) actualResult.get("Owner"))
        assertEquals(expectedResult, actualResult)

        def actualResult2 = facturaService.getSummaryForBill(testAccount, 1)
        compareOwners(expectedResultOwner, (Owner) actualResult2.get("Owner"))
        assertEquals(expectedResult, actualResult2)

        verify(billDao, times(1)).getBillHtml(eq(194799))
    }
}
