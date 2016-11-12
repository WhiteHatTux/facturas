package de.ctimm.service

import de.ctimm.domain.Owner

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
interface OwnerService {

    Owner updateOwner(Integer account)

    Owner getOwner(Integer account)

    void addOwner(Owner owner)

    void deleteOwner(Integer account)

    void updateExpired()

}