package de.ctimm.dao

import de.ctimm.domain.Bill

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
interface BillDao {
    String getBillHtml(int account)

    String getBillXml(Integer xmlNumber)

    String getOwnerHtml(int account)
}
