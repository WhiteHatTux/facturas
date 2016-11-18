package de.ctimm

import de.ctimm.domain.Bill
import de.ctimm.domain.Owner

import java.sql.Timestamp

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
class TestDataCreator {
    final String testResponse = this.getClass().getResource("/testResponse.html").text
    final String testBill = this.getClass().getResource("/testBill.xml").text
    final String testBill1 = this.getClass().getResource("/testBill1.xml").text
    final String testComprobante = this.getClass().getResource("/testComprobante.xml").text
    final String testNotificationData = this.getClass().getResource("/testnotificationData.html").text

    final Integer testAccount = 194799
    String ownerName = 'LOPEZ ESCOBAR  ROBERTO PABLO '

    public Owner createTestOwner() {
        Owner expectedResultOwner = new Owner(testAccount)
        expectedResultOwner.name = ownerName
        expectedResultOwner.cellphone = '0987614298'
        expectedResultOwner.phone = '2845555'
        expectedResultOwner.direction = 'AVLOS ATISFEBRES CORDERO'
        expectedResultOwner.email = 'robertopablolopez@yahoo.es'

        Bill bill = new Bill(194799)
        bill.xmlNumber = 6069973
        bill.issued = Timestamp.valueOf("2016-10-18 00:00:00.000")

        Bill bill2 = new Bill(194799)
        bill2.xmlNumber = 5810666
        bill2.issued = Timestamp.valueOf("2016-09-18 00:00:00.000")
        expectedResultOwner.addBill(bill)
        expectedResultOwner.addBill(bill2)
        expectedResultOwner
    }

}
