package de.ctimm.service

import de.ctimm.domain.Owner

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
interface OwnerService {

    Owner updateOwner(Integer account)

    Owner getOwner(Integer account)

    void addOwner(Owner owner)

    void deleteOwner(Integer account)

    void updateExpired()

}