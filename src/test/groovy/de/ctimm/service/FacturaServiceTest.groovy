package de.ctimm.service

import de.ctimm.domain.BillRepository
import org.junit.Before
import org.mockito.Mockito
import org.springframework.web.client.RestOperations

import static org.mockito.Mockito.anyString
import static org.mockito.Mockito.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
class FacturaServiceTest extends GroovyTestCase {

    RestOperations restTemplateFactura
    RestOperations restTemplateResponseParser
    ResponseParser responseParser
    FacturaService facturaService
    BillRepository billRepository
    Integer testAccount = 194799

    String testResponse = this.getClass().getResource("/testResponse.html").text
    String testBill = this.getClass().getResource("/testBill.xml").text
    String testComprobante = this.getClass().getResource("/testComprobante.xml").text

    @Before
    void setUp() {
        restTemplateFactura = mock(RestOperations.class)
        restTemplateResponseParser = mock(RestOperations.class)
        billRepository = new BillRepository()
        when(restTemplateFactura.postForObject(anyString(), Mockito.any(), eq(String.class))).thenReturn(testResponse)
        when(restTemplateResponseParser.getForObject(anyString(), eq(String.class))).thenReturn(testBill)
        responseParser = new ResponseParser(restTemplateResponseParser, "dummyhost", "dummypath")
        facturaService = new FacturaServiceImpl(responseParser, restTemplateFactura, billRepository)

    }

    void testGetTotalAmount() {
        Double actualResult = facturaService.getTotalAmount(testAccount)
        Double expectedResult = 27.51
        assertEquals(expectedResult, actualResult)
    }

    void testGetOwner() {
        String actualResult = facturaService.getOwner(testAccount)
        String expectedResult = 'LOPEZ ESCOBAR ROBERTO PABLO '

        assertEquals(expectedResult, actualResult)
    }

    void testGetIdentification() {
        String actualResult = facturaService.getIdentification(testAccount)
        String expectedReult = '0200989077'

        assertEquals(expectedReult, actualResult)
    }

    void testGetSummary() {
        def actualresult = facturaService.getSummary(testAccount, false)
        Map<String, String> expectedResult = new HashMap<>()
        expectedResult.put("Total", "27.51")
        expectedResult.put("Identification", "0200989077")
        expectedResult.put("Discounts", "0.0")
        expectedResult.put("Owner", 'LOPEZ ESCOBAR ROBERTO PABLO ')
        expectedResult.put("Issued", "2016-10-18 00:00:00.0")

        assertEquals(expectedResult, actualresult)
    }
}
