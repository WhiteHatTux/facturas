package de.ctimm.domain

import groovy.time.BaseDuration
import groovy.time.Duration
import groovy.time.TimeCategory

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
class BillRepositoryTest extends GroovyTestCase {
    BillRepository billRepository = new BillRepository()

    void testGetBillNull() {
        Integer account = 194799
        Bill bill = new Bill(194799)
        Date original = bill.collectionTimestamp
        bill.collectionTimestamp = TimeCategory.minus(bill.collectionTimestamp, new Duration(1,0,0,0,0))
        billRepository.addBill(bill)
        Bill newBill = billRepository.getBill(account)
        assertNull(newBill)
    }

    void testGetBill() {
        Integer account = 194799
        Bill bill = new Bill(194799)
        Date original = bill.collectionTimestamp
        billRepository.addBill(bill)
        Bill newBill = billRepository.getBill(account)
        assertTrue(newBill.collectionTimestamp == original)
    }
}
