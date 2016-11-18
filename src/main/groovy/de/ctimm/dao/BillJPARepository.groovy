package de.ctimm.dao

import de.ctimm.domain.Bill
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository


/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
interface BillJPARepository extends CrudRepository<Bill, Long> {

    Page<Bill> findAll(Pageable pageable)

    Bill save(Bill bill)

}