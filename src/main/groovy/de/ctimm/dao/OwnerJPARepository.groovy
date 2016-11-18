package de.ctimm.dao

import de.ctimm.domain.Owner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
interface OwnerJPARepository extends CrudRepository<Owner, Long> {

    Page<Owner> findAll(Pageable pageable)

    Owner findByAccount(Integer account)

    void deleteByAccount(Integer account)
}