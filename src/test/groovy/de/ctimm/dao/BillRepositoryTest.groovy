package de.ctimm.dao

import de.ctimm.dao.BillRepository
import de.ctimm.domain.Bill
import groovy.time.Duration
import groovy.time.TimeCategory

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class BillRepositoryTest extends GroovyTestCase {
    BillRepository billRepository = new BillRepository()

    void testGetBillNull() {
        Integer account = 194799
        Bill bill = new Bill(194799)
        bill.collectionTimestamp = TimeCategory.minus(bill.collectionTimestamp, new Duration(1, 0, 0, 0, 0))
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

    void testExpire() {
        Bill bill = new Bill(194799)
        bill.collectionTimestamp = TimeCategory.minus(bill.collectionTimestamp, new Duration(1, 0, 0, 0, 0))
        billRepository.addBill(bill)
        Bill bill2 = new Bill(194778)
        bill2.collectionTimestamp = TimeCategory.minus(bill2.collectionTimestamp, new Duration(1, 0, 0, 0, 0))
        billRepository.addBill(bill2)
        billRepository.removeExpired()
        assertEquals(Collections.emptyMap(), billRepository.billRepository)
    }
}
