package de.ctimm.dao

import de.ctimm.domain.Bill

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
interface BillDao {
    String getBillHtml(int account)

    String getBillXml(Bill bill)

    String getOwnerHtml(int account)
}
