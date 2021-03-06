package de.ctimm.service

import de.ctimm.domain.Bill
import de.ctimm.domain.Owner

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
interface FacturaService {

    boolean getForceReloadOwner()

    void setForceReloadOwner(boolean forceReloadOwner)

    Bill getBill(Integer account, int age)

    List<Bill> getBills(Integer account)

    Double getDiscounts(Integer account, int age)

    String getIdentification(Integer account, int age)

    Date getDateOfAuthorization(Integer account, int age)

    Date getIssueDate(Integer account, int age)

    Owner getOwner(Integer account)

    Map<String, Object> getSummary(Integer account, boolean forceReload)

    Map<String, Object> getSummaryForBill(Integer account, int age)

    Double getTotalAmount(Integer account, int age)

    void housekeep()

}