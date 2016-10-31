package de.ctimm

import de.ctimm.domain.Bill
import de.ctimm.service.ResponseParser
import groovy.xml.XmlUtil
import org.custommonkey.xmlunit.XMLUnit
import org.mockito.Mockito
import org.springframework.web.client.RestTemplate

import java.sql.Timestamp

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class ResponseParserTest extends GroovyTestCase {

    String testResponseHtml = this.getClass().getResource("/testResponse.html").text
    RestTemplate restTemplate = Mockito.mock(RestTemplate)
    String dummyHost = "dummyhost"
    String dummyPath = "dummyPath"
    ResponseParser responseParser = new ResponseParser(restTemplate, dummyHost, dummyPath);

    void testGetBills() {
        List<Bill> bills = responseParser.getBills(testResponseHtml, 194799);

        def billToCheck = bills[0]
        assertEquals("001012-4398474", billToCheck.number)
        assertEquals(Timestamp.valueOf("2016-10-18 00:00:00"), billToCheck.issued)
        assertEquals("1810201601189000143900120010120043984740439847414", billToCheck.accessKey)
        assertEquals(Timestamp.valueOf("2016-10-19 10:47:59"), billToCheck.dateOfAuthorization)
        assertEquals(Integer.valueOf("6069973"), billToCheck.xmlNumber)
        assertEquals(Integer.valueOf("194799"), billToCheck.account)


    }

    void testGetXml() {
        Integer xmlNumber = 6069973
        def testBill = this.getClass().getResource("/testBill.xml").text
        def testComprobante = this.getClass().getResource("/testComprobante.xml").text
        Mockito.when(restTemplate.getForObject(dummyHost + dummyPath + xmlNumber, String.class))
                .thenReturn(testBill)
        ResponseParser responseParser = new ResponseParser(restTemplate, dummyHost, dummyPath);
        Bill bill = new Bill(6069973)
        bill.xmlNumber = xmlNumber
        bill.accessKey = 1810201601189000143900120010120043984740439847414
        def actualxml = responseParser.getXml(bill)
        def expectedXml = new XmlSlurper().parseText(testComprobante)
        XMLUnit.setIgnoreWhitespace(true)
        XMLUnit.setIgnoreComments(true)
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true)
        XMLUnit.setNormalizeWhitespace(true)

        XMLUnit.compareXML(XmlUtil.serialize(expectedXml), XmlUtil.serialize(actualxml))
    }
}
