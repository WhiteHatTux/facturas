package de.ctimm.service

import de.ctimm.domain.Owner

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
interface OwnerService {

    Owner updateOwner(Integer account)

    List<Integer> getAccountList()

    Owner getOwner(Integer account)

    List<Owner> getAllOwners()

    Owner addOwner(Owner owner)

    void deleteOwner(Integer account)

    void updateExpired()

}