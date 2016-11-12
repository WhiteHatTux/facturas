package de.ctimm.service

import de.ctimm.domain.Owner

import java.sql.Timestamp

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
interface FacturaService {

    Double getDiscounts(Integer account, int age)

    String getIdentification(Integer account, int age)

    Timestamp getIssueDate(Integer account, int age)

    Owner getOwner(Integer account)

    String getOwnerName(Integer account, int age)

    Map<String, Object> getSummary(Integer account, boolean forceReload)

    Map<String, Object> getSummaryForBill(Integer account, int age)

    Double getTotalAmount(Integer account, int age)

}