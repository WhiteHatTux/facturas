package de.ctimm.domain

import java.sql.Timestamp

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class Bill {
    Date collectionTimestamp = new Date()
    Integer account
    Owner owner
    String number
    Timestamp issued
    String accessKey
    Timestamp dateOfAuthorization
    def xml
    Integer xmlNumber

    Bill(Integer account) {
        this.account = account
    }

    protected Bill() {
    }
}
