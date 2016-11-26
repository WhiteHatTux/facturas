package de.ctimm.domain

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class BillTest extends GroovyTestCase {

    void testBillEquals(){
        Bill bill1 = new Bill(123456)
        bill1.total = 12.12
        Bill bill2 = new Bill(123456)
        bill1.total = 23.23

        assert bill1.equals(bill2)
        assert bill1 == bill2
        assert bill1.hashCode() == bill2.hashCode()
    }
}
