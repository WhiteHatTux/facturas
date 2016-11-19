package de.ctimm.service

import de.ctimm.TestDataCreator
import de.ctimm.dao.BillDao
import de.ctimm.dao.BillJPARepository
import de.ctimm.dao.OwnerJPARepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import org.junit.Before

import java.text.SimpleDateFormat

import static org.mockito.Matchers.anyInt
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class FacturaServiceImplTest extends GroovyTestCase {
    ResponseParser responseParser
    FacturaService facturaService
    OwnerService ownerService
    OwnerJPARepository ownerRepository
    BillDao billDao
    BillJPARepository billJPARepository

    TestDataCreator testDataCreator = new TestDataCreator()
    Integer testAccount = testDataCreator.testAccount
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
        ownerRepository = mock(OwnerJPARepository.class)
        billJPARepository = mock(BillJPARepository.class)
        when(ownerRepository.findByAccount(testAccount)).thenReturn(testDataCreator.createTestOwner())
        ownerService = new OwnerServiceImpl(responseParser, ownerRepository, billDao, billJPARepository)
        facturaService = new FacturaServiceImpl(responseParser, billDao, ownerService, billJPARepository)
    }

    void testGetTotalAmount() {
        Double actualResult = facturaService.getTotalAmount(testAccount, 0)
        Double expectedResult = 27.51
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
                assertEquals(((String)key + " was different"), expected.(key.toString()), value)
            }
        }
    }

    static void compareBills(Bill expected, Bill actual){
        actual.properties.each { def key, def value ->
            if (key != "collectionTimestamp") {
                assertEquals(((String)key + " was different"), expected.(key.toString()), value)
            }
        }
    }

    void testGetSummary() {
        Owner expectedResultOwner = testDataCreator.createTestOwner()
        Bill expectedBill = testDataCreator.createTestBill(0)
        Map<String, Object> expectedResult = new HashMap<>()
        expectedResult.put("account", testAccount)
        expectedResult.put("Owner", expectedResultOwner)
        expectedResult.put("bill", expectedBill)

        def actualResult = facturaService.getSummary(testAccount, false)
        compareOwners(expectedResultOwner, (Owner) actualResult.get("Owner"))
        compareBills(expectedBill, (Bill) actualResult.get("bill"))
        expectedResult.each { def key, def value ->
            assertEquals(value, actualResult.get(key))
        }
    }

    void testGetSummaryForOldBill() {
        Owner expectedResultOwner = testDataCreator.createTestOwner()
        Bill expectedBill = testDataCreator.createTestBill(1)
        Map<String, Object> expectedResult = new HashMap<>()
        expectedResult.put("Owner", expectedResultOwner)
        expectedResult.put("account", testAccount)
        expectedResult.put("bill", expectedBill)


        def actualResult = facturaService.getSummaryForBill(testAccount, 1)
        compareOwners(expectedResultOwner, (Owner) actualResult.get("Owner"))
        compareBills(expectedBill, (Bill) actualResult.get("bill"))
        expectedResult.each { def key, def value ->
            assertEquals(value, actualResult.get(key))
        }
    }
}
