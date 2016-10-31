package de.ctimm.domain

import java.sql.Timestamp

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
class Bill {
    Integer account
    String owner
    String number
    Timestamp issued
    String accessKey
    Timestamp dateOfAuthorization
    def xml
    Integer xmlNumber

}
