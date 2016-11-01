package de.ctimm.domain

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
class OwnerTest extends GroovyTestCase {
    void testEquals() {
        Owner owner = new Owner(123456)
        Owner owner1 = new Owner(123456)
        assertTrue(owner1.equals(owner))
        assertTrue(owner.equals(owner1))
    }

    void testHashCode() {
        Owner owner = new Owner(123456)
        Owner owner1 = new Owner(123456)
        Owner owner2 = new Owner(654321)

        HashSet<Owner> map = new HashSet<>()

        map.add(owner)
        map.add(owner1)
        map.add(owner2)

        assertEquals(2, map.size())

    }
}
