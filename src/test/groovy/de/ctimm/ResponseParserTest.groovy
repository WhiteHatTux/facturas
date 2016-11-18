package de.ctimm

import de.ctimm.dao.BillDao
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import de.ctimm.service.ResponseParser
import groovy.xml.XmlUtil
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Before

import java.sql.Timestamp

import static org.mockito.Matchers.anyInt
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
class ResponseParserTest extends GroovyTestCase {

    BillDao billDao = mock(BillDao.class)
    ResponseParser responseParser


    TestDataCreator testDataCreator = new TestDataCreator()
    String testResponseHtml = testDataCreator.testResponse
    String testBill = testDataCreator.testBill
    String testComprobante = testDataCreator.testComprobante
    String testNotificationData = testDataCreator.testNotificationData

    @Before
    void setUp() {
        when(billDao.getBillXml(anyInt())).thenReturn(testBill)
        when(billDao.getOwnerHtml(anyInt())).thenReturn(testNotificationData)
        responseParser = new ResponseParser(billDao);
    }

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

    void testGetOwnerInfo() {
        Integer testAccount = 194799
        Owner owner = responseParser.getOwnerInformation(testAccount)

        assertEquals("robertopablolopez@yahoo.es", owner.email)
        assertEquals("LOPEZ ESCOBAR  ROBERTO PABLO ", owner.name)
        assertEquals("0987614298", owner.cellphone)
        assertEquals("2845555", owner.phone)

    }
}
