package de.ctimm.dao

import de.ctimm.domain.Owner

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
class OwnerRepositoryTest extends GroovyTestCase {
    OwnerRepository ownerRepository = new OwnerRepository()

    void testAddOwner() {
        Integer account = 194799
        Owner owner = new Owner(account)
        Date original = owner.collectionTimestamp
        ownerRepository.addOwner(owner)
        Owner newOwner = ownerRepository.getOwner(account)
        assertEquals(original, newOwner.collectionTimestamp)

    }

}
