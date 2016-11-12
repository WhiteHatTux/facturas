package de.ctimm

import de.ctimm.domain.Owner

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
        expectedResultOwner
    }

}
