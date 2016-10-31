package de.ctimm.domain

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Component
class BillRepository {
    Map<Integer, Bill> billRepository = new HashMap<>()

    Bill getBill(Integer account) {
        Bill bill = billRepository.get(account)
        if (bill == null) {
            return null
        } else {
            TimeDuration td = TimeCategory.minus(new Date(), bill.collectionTimestamp)
            if (td.getHours() > 23 || td.getDays() > 0) {
                billRepository.remove(account)
                return null
            }
        }
        return bill
    }

    void addBill(Bill bill) {
        billRepository.put(bill.account, bill)
    }

    boolean isExpired(Bill bill){
        TimeDuration td = TimeCategory.minus(new Date(), bill.collectionTimestamp)
        if (td.getHours() > 23 || td.getDays() > 0) {
            billRepository.remove(bill.account)
            return true
        } else {
            return false
        }
    }

    @Scheduled(fixedRate = 500000L)
    void removeExpired(){
        billRepository.each{ Integer account, Bill bill ->
            isExpired(bill)
        }
    }

}
