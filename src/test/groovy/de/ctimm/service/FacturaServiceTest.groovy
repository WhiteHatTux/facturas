package de.ctimm.service

import de.ctimm.dao.BillDao
import de.ctimm.dao.BillRepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import org.junit.Before

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyInt
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class FacturaServiceTest extends GroovyTestCase {
    ResponseParser responseParser
    FacturaService facturaService
    BillRepository billRepository
    BillDao billDao
    Integer testAccount = 194799

    String testResponse = this.getClass().getResource("/testResponse.html").text
    String testBill = this.getClass().getResource("/testBill.xml").text
    String testComprobante = this.getClass().getResource("/testComprobante.xml").text
    String testNotificationData = this.getClass().getResource("/testnotificationData.html").text


    String owerName = 'LOPEZ ESCOBAR  ROBERTO PABLO '

    @Before
    void setUp() {
        billDao = mock(BillDao.class)
        when(billDao.getBillXml(any(Bill.class))).thenReturn(testBill)
        when(billDao.getBillHtml(anyInt())).thenReturn(testResponse)
        when(billDao.getOwnerHtml(anyInt())).thenReturn(testNotificationData)

        billRepository = new BillRepository()
        responseParser = new ResponseParser(billDao)
        facturaService = new FacturaServiceImpl(responseParser, billRepository, billDao)

    }

    void testGetTotalAmount() {
        Double actualResult = facturaService.getTotalAmount(testAccount)
        Double expectedResult = 27.51
        assertEquals(expectedResult, actualResult)
    }

    void testGetOwnerName() {
        String actualResult = facturaService.getOwnerName(testAccount)
        String expectedResult = 'LOPEZ ESCOBAR ROBERTO PABLO '

        assertEquals(expectedResult, actualResult)
    }

    void testGetIdentification() {
        String actualResult = facturaService.getIdentification(testAccount)
        String expectedReult = '0200989077'

        assertEquals(expectedReult, actualResult)
    }

    void testGetOwner() {
        Owner actualResultOwner = facturaService.getOwner(testAccount)
        Owner expectedResultOwner = createTestOwner()

        actualResultOwner.properties.each { def key, def value ->
            assertEquals(expectedResultOwner.(key.toString()), value)
        }
    }

    private Owner createTestOwner() {
        Owner expectedResultOwner = new Owner(testAccount)
        expectedResultOwner.name = owerName
        expectedResultOwner.cellphone = '0987614298'
        expectedResultOwner.phone = '2845555'
        expectedResultOwner.direction = 'AVLOS ATISFEBRES CORDERO'
        expectedResultOwner.email = 'robertopablolopez@yahoo.es'
        expectedResultOwner
    }

    void testGetSummary() {
        Owner expectedResultOwner = createTestOwner()
        def actualResult = facturaService.getSummary(testAccount, false)
        Map<String, Object> expectedResult = new HashMap<>()
        expectedResult.put("Total", "27.51")
        expectedResult.put("Identification", "0200989077")
        expectedResult.put("Discounts", "0.0")
        expectedResult.put("Owner", expectedResultOwner)
        expectedResult.put("Issued", "2016-10-18 00:00:00.0")

        actualResult.get("Owner").properties.each { def key, def value ->
            assertEquals(expectedResultOwner.(key.toString()), value)
        }
        assertEquals(expectedResult, actualResult)
    }
}
