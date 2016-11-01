package de.ctimm.service

import de.ctimm.domain.Owner

import java.sql.Timestamp

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
interface FacturaService {
    Double getTotalAmount(Integer account)

    String getOwnerName(Integer account)

    Map<String, String> getSummary(Integer account, boolean forceReload)

    String getIdentification(Integer account)

    Double getDiscounts(Integer account)

    Timestamp getIssueDate(Integer account)

    Owner getOwner(Integer account)
}