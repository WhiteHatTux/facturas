package de.ctimm.service

import de.ctimm.dao.BillDao
import de.ctimm.dao.OwnerRepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
@Component
class OwnerServiceImpl implements OwnerService {

    private ResponseParser responseParser

    private OwnerRepository ownerRepository

    private BillDao billDao


    private static final Logger logger = LoggerFactory.getLogger(OwnerServiceImpl.class);

    @Autowired
    public OwnerServiceImpl(ResponseParser responseParser, OwnerRepository ownerRepository, BillDao billDao) {
        this.responseParser = responseParser
        this.ownerRepository = ownerRepository
        this.billDao = billDao
    }

    @Override
    Owner updateOwner(Integer account) {
        Owner existOwner = ownerRepository.getOwner(account)
        Owner owner = responseParser.getOwnerInformation(account)

        existOwner = existOwner == null ? new Owner(account) : existOwner
        existOwner.collectionTimestamp = new Date()
        existOwner.account = owner.account
        existOwner.name = owner.name
        existOwner.email = owner.email
        existOwner.email1 = owner.email1
        existOwner.email2 = owner.email2
        existOwner.cellphone = owner.cellphone
        existOwner.phone = owner.phone
        existOwner.direction = owner.direction
        // Get the updated list of current bills
        String html = billDao.getBillHtml(account)
        List<Bill> bills = responseParser.getBills(html, account)
        bills.each {
            it.owner = existOwner
        }
        bills.each {
            if (existOwner.getBill(it.number) == null) {
                existOwner.addBill(it)
            }
        }
        addOwner(existOwner)
        return existOwner
    }

    @Override
    Owner getOwner(Integer account) {
        Owner existOwner = ownerRepository.getOwner(account)
        if (existOwner == null) {
            return updateOwner(account)
        }
        updateIfExpired(existOwner)
        return existOwner
    }

    @Override
    void addOwner(Owner owner) {
        deleteOwner(owner.account)
        ownerRepository.addOwner(owner)
    }

    @Override
    void deleteOwner(Integer account) {
        Owner existOwner = ownerRepository.getOwner(account)
        if (existOwner != null) {
            logger.info("Will remove owner {} with {} current bills", existOwner.account, existOwner.billsList.size())
            ownerRepository.removeOwner(account)
        }
    }

    private Boolean updateIfExpired(Owner owner) {
        TimeDuration td = TimeCategory.minus(new Date(), owner.collectionTimestamp)
        if (td.getHours() > 23 || td.getDays() > 0) {
            updateOwner(owner.account)
            logger.info("Owner {} was updated, because it was expired", owner.account)
            return true
        } else {
            return false
        }

    }

    @Scheduled(fixedRate = 500000L)
    void updateExpired() {
        ownerRepository.ownerRepository.each { Integer account, Owner owner ->
            updateIfExpired(owner)
        }
    }
}
