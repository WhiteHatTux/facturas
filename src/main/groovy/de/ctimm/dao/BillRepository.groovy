package de.ctimm.dao

import de.ctimm.domain.Bill
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Component
class BillRepository {
    Map<Integer, Bill> billRepository = new HashMap<>()
    private static final Logger logger = LoggerFactory.getLogger(BillRepository.class);

    Bill getBill(Integer account) {
        Bill bill = billRepository.get(account)
        if (bill == null) {
            return null
        } else {
            if (isExpired(bill)) {
                billRepository.remove(bill.account)
                return null
            }
        }
        return bill
    }

    void addBill(Bill bill) {
        billRepository.put(bill.account, bill)
    }

    static boolean isExpired(Bill bill) {
        TimeDuration td = TimeCategory.minus(new Date(), bill.collectionTimestamp)
        if (td.getHours() > 23 || td.getDays() > 0) {
            return true
        } else {
            return false
        }
    }

    // Run once an hour
    @Scheduled(cron = "0 0 * * * *")
    void removeExpired() {
        def billsToDelete = [];
        billRepository.findAll { Integer account, Bill bill ->
            if (isExpired(bill)){
                billsToDelete.add(account)
                logger.info("Bill {} was removed, because it is expired", bill.account)
            }
        }
        billsToDelete.findAll{
            billRepository.remove(it)
        }
    }

}
