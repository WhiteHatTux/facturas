package de.ctimm.dao

import de.ctimm.domain.Owner
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
@Component
class OwnerRepository {
    Map<Integer, Owner> ownerRepository = new HashMap<>()

    Owner getOwner(Integer account) {
        Owner owner = ownerRepository.get(account)
        if (owner == null) {
            return null
        } else {
            if (isExpired(owner)) {
                return null
            }
        }
        return owner
    }

    boolean isExpired(Owner owner) {
        TimeDuration td = TimeCategory.minus(new Date(), owner.collectionTimestamp)
        if (td.getHours() > 23 || td.getDays() > 0) {
            ownerRepository.remove(owner.account)
            return true
        } else {
            return false
        }
    }

    void addOwner(Owner owner) {
        ownerRepository.put(owner.account, owner)
    }

    @Scheduled(fixedRate = 500000L)
    void removeExpired() {
        ownerRepository.each { Integer account, Owner owner ->
            isExpired(owner)
        }
    }
}
