package de.ctimm.service

import java.sql.Timestamp

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
interface FacturaService {
    Double getTotalAmount(Integer account)

    String getOwner(Integer account)

    Map<String, String> getSummary(Integer account)

    String getIdentification(Integer account)

    Double getDiscounts(Integer account)

    Timestamp getIssueDate(Integer account)
}