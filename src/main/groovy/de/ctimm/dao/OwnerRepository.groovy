package de.ctimm.dao

import de.ctimm.domain.Owner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Component
class OwnerRepository {
    Map<Integer, Owner> ownerRepository = new HashMap<>()
    private static final Logger logger = LoggerFactory.getLogger(OwnerRepository.class);

    Owner getOwner(Integer account) {
        return ownerRepository.get(account)
    }

    void addOwner(Owner owner) {
        ownerRepository.put(owner.account, owner)
    }

    void removeOwner(Integer account) {
        ownerRepository.remove(account)
    }


}
