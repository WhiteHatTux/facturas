package de.ctimm.dao

import de.ctimm.domain.Owner
import groovy.time.Duration
import groovy.time.TimeCategory

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class OwnerRepositoryTest extends GroovyTestCase {
    OwnerRepository ownerRepository = new OwnerRepository()

    void testGetOwner() {
        Integer account = 194799
        Owner owner = new Owner(account)
        owner.collectionTimestamp = TimeCategory.minus(owner.collectionTimestamp, new Duration(1, 0, 0, 0, 0))
        ownerRepository.addOwner(owner)
        Owner newOwner = ownerRepository.getOwner(account)
        assertNull(newOwner)

    }

    void testIsExpired() {
        Integer account = 194799
        Owner owner = new Owner(account)
        Date original = owner.collectionTimestamp
        ownerRepository.addOwner(owner)
        Owner newOwner = ownerRepository.getOwner(account)
        assertEquals(original, newOwner.collectionTimestamp)

    }

    void testAddOwner() {
        Owner owner = new Owner(194799)
        owner.collectionTimestamp = TimeCategory.minus(owner.collectionTimestamp, new Duration(1, 0, 0, 0, 0))
        ownerRepository.addOwner(owner)
        ownerRepository.removeExpired()
        assertEquals(Collections.emptyMap(), ownerRepository.ownerRepository)
    }
}
