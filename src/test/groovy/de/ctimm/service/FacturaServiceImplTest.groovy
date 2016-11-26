package de.ctimm.service

import de.ctimm.TestDataCreator
import de.ctimm.dao.BillDao
import de.ctimm.dao.BillJPARepository
import de.ctimm.dao.OwnerJPARepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import org.junit.Before
import org.mockito.ArgumentCaptor

import java.text.SimpleDateFormat

import static org.mockito.Matchers.anyInt
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

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
                assertEquals(((String) key + " was different"), expected.(key.toString()), value)
            }
        }
    }

    static void compareBills(Bill expected, Bill actual) {
        actual.properties.each { def key, def value ->
            if (key != "collectionTimestamp") {
                if (key == "xml") {
                    String xmlValue = ((String) value).replaceAll("\\s", "")
                    String xmlExpected = ((String) expected.(key.toString())).replaceAll("\\s", "")
                    assertEquals("XML was different", xmlExpected, xmlValue)
                } else {
                    assertEquals(((String) key + " was different"), expected.(key.toString()), value)
                }
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

    void testGetSummaryForBill() {
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

    void testHousekeepDupliateBill() {
        Owner owner = testDataCreator.createTestOwner()
        Bill bill0 = testDataCreator.createTestBill(0)
        owner.billsList.add(bill0)
        // There should be a duplicate bill in here
        assertEquals(3, owner.billsList.size())
        when(ownerRepository.findAll()).thenReturn(Collections.singletonList(owner))
        List<Owner> owners = ownerService.getAllOwners()

        facturaService.housekeep()
        verify(billJPARepository, times(1)).delete(bill0)
        // The duplicate bill should be deleted by now
        assertEquals(2, owners.get(0).billsList.size())
    }

    void testHouseKeepBillData() {
        Bill bill0 = testDataCreator.createTestBill(0)
        Bill bill1 = testDataCreator.createTestBill(1)
        bill0.total = null
        when(billJPARepository.findAll()).thenReturn([bill0, bill1])
        final ArgumentCaptor<Bill> billArgumentCaptor = ArgumentCaptor.forClass(Bill.class)
        facturaService.housekeep()
        verify(billJPARepository).save(billArgumentCaptor.capture())
        assertEquals(27.51, billArgumentCaptor.getValue().total)
    }


    void testGetBill() {
        Bill expectedbill = testDataCreator.createTestBill(0)
        Owner owner = testDataCreator.createTestOwner();
        owner.billsList.each { it.xml = null }
        when(ownerRepository.findByAccount(testAccount)).thenReturn(owner)
        Bill actualBill = facturaService.getBill(testAccount, 0)
        compareBills(expectedbill, actualBill)
    }

    void testGetBills() {
        List<Bill> bills = facturaService.getBills(testAccount)
        assertEquals(2, bills.size())

    }

    void testGetDiscounts() {
        Double discounts = facturaService.getDiscounts(testAccount, 0)
        assertEquals(0.0, discounts)
    }

    void testGetIssueDate() {
        Date issueDate = facturaService.getIssueDate(testAccount, 0)
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-10-18 00:00:00"), issueDate)
    }

    void testGetDateOfAuthorization() {
        Date dateOfAuthorization = facturaService.getDateOfAuthorization(testAccount, 0)
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-10-19 10:47:59"), dateOfAuthorization)
    }

    void testGetNonExistingBill(){
        Map<String, Object> result = facturaService.getSummaryForBill(testAccount, 4)

        assertEquals(result.get("message"), "The requested bill does not exist, Returning the oldest bill")
    }


    void testforceUpdateOwner(){
        // test rest api is manipulated to return a bogus name
        testNotificationData = testNotificationData.replace('LOPEZ ESCOBAR  ROBERTO PABLO ', 'This is the name, returned from the mocked REST API')
        when(billDao.getOwnerHtml(anyInt())).thenReturn(testNotificationData)


        // Currently the owner is set to one value
        FacturaServiceImpl facturaService1 = (FacturaServiceImpl) facturaService
        Owner owner = facturaService1.getOwner(testAccount)
        assertEquals(owner.name, 'LOPEZ ESCOBAR  ROBERTO PABLO ')

        // so when we do a force refresh the local name will be replaced by the bogus one.
        facturaService1.forceReloadOwner = true
        Owner actualOwner = facturaService1.getOwner(testAccount)
        assertEquals('This is the name, returned from the mocked REST API', actualOwner.name)
    }

}
