package de.ctimm

import de.ctimm.domain.Bill
import de.ctimm.domain.Owner

import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class TestDataCreator {
    final String testResponse = this.getClass().getResource("/testResponse.html").text
    final String testBill = this.getClass().getResource("/testBill.xml").text
    final String testBill1 = this.getClass().getResource("/testBill1.xml").text
    final String testComprobante = this.getClass().getResource("/testComprobante.xml").text
    final String testNotificationData = this.getClass().getResource("/testnotificationData.html").text

    public static final Integer testAccount = 194799
    String ownerName = 'LOPEZ ESCOBAR  ROBERTO PABLO '

    public Owner createTestOwner() {
        Owner expectedResultOwner = new Owner(testAccount)
        expectedResultOwner.name = ownerName
        expectedResultOwner.cellphone = '0987614298'
        expectedResultOwner.phone = '2845555'
        expectedResultOwner.direction = 'AVLOS ATISFEBRES CORDERO'
        expectedResultOwner.email = 'robertopablolopez@yahoo.es'

        Bill bill = createTestBill(0)

        Bill bill2 = createTestBill(1)

        expectedResultOwner.addBill(bill)
        expectedResultOwner.addBill(bill2)
        expectedResultOwner
    }

    Bill createTestBill(int age) {
        if (age == 0) {
            Bill expectedResultBill = new Bill(testAccount)
            expectedResultBill.accessKey = "1810201601189000143900120010120043984740439847414"
            expectedResultBill.collectionTimestamp = new Date()
            expectedResultBill.number = "001012-4398474"
            expectedResultBill.dateOfAuthorization = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-10-19 10:47:59")
            expectedResultBill.discounts = 0.0
            expectedResultBill.identification = "0200989077"
            expectedResultBill.issued = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-10-18 00:00:00")
            expectedResultBill.total = 27.51
            expectedResultBill.xmlNumber = 6069973
            expectedResultBill.xml = testComprobante
            return expectedResultBill
        } else if (age == 1) {
            Bill expectedResultBill = new Bill(testAccount)
            expectedResultBill.accessKey = "1809201601189000143900120010120041953810419538112"
            expectedResultBill.collectionTimestamp = new Date()
            expectedResultBill.number = "001012-4195381"
            expectedResultBill.dateOfAuthorization = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-09-20 01:58:35")
            expectedResultBill.discounts = 0.0
            expectedResultBill.identification = "0200989077"
            expectedResultBill.issued = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-09-18 00:00:00")
            expectedResultBill.total = 16.64
            expectedResultBill.xmlNumber = 5810666
            expectedResultBill.xml = testBill
            return expectedResultBill
        } else if(age == 2) {
            Bill expectedResultBill = new Bill(testAccount)
            expectedResultBill.accessKey = "1808201601189000143900120010120039927240399272410"
            expectedResultBill.collectionTimestamp = new Date()
            expectedResultBill.number = "001012-3992724"
            expectedResultBill.dateOfAuthorization = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-08-18 12:29:35")
            expectedResultBill.discounts = 1.38
            expectedResultBill.identification = "0200989077"
            expectedResultBill.issued = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-08-18 00:00:00")
            expectedResultBill.total = 1.91
            expectedResultBill.xmlNumber = 5552016
            expectedResultBill.xml = testBill1
            return expectedResultBill
        }
    }

}
