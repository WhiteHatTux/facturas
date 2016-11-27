package de.ctimm.service

import de.ctimm.dao.BillDao
import de.ctimm.dao.BillJPARepository
import de.ctimm.dao.OwnerJPARepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Component
@Transactional
class OwnerServiceImpl implements OwnerService {

    private ResponseParser responseParser

    private OwnerJPARepository ownerRepository

    private BillJPARepository billJPARepository

    private BillDao billDao

    private static final Logger logger = LoggerFactory.getLogger(OwnerServiceImpl.class);

    @Autowired
    public OwnerServiceImpl(ResponseParser responseParser, OwnerJPARepository ownerRepository, BillDao billDao, BillJPARepository billJPARepository) {
        this.responseParser = responseParser
        this.ownerRepository = ownerRepository
        this.billDao = billDao
        this.billJPARepository = billJPARepository
    }

    @Override
    Owner updateOwner(Integer account) {
        Owner existOwner = ownerRepository.findByAccount(account)
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
            // This creates the link between the bill and the owner
            it.owner = existOwner
            // I only want to save the bill, if it is new
            // otherwise i just add the id from the database to it.
            Bill existingBill = billJPARepository.findByNumber(it.number)
            if (existingBill == null) {
                it = billJPARepository.save(it)
            } else {
                it = existingBill
            }
            if (existOwner.getBill(it.number) != null) {
                logger.debug("Bill with number {} already exists, not reloading", it.number)
            } else {
                existOwner.addBill(it)
            }
        }
        this.addOwner(existOwner)
        return existOwner
    }

    @Override
    Owner getOwner(Integer account) {
        Owner existOwner = ownerRepository.findByAccount(account)
        if (existOwner == null) {
            return this.updateOwner(account)
        }
        this.updateIfExpired(existOwner)
        return existOwner
    }

    @Override
    List<Owner> getAllOwners() {
        (List<Owner>) ownerRepository.findAll()
    }

    @Override
    void addOwner(Owner owner) {
        Owner owner1 = ownerRepository.save(owner)
        owner1
    }

    @Override
    void deleteOwner(Integer account) {
        Owner existOwner = ownerRepository.findByAccount(account)
        if (existOwner != null) {
            logger.info("Will remove owner {} with {} current bills", existOwner.account, existOwner.billsList.size())
            ownerRepository.deleteByAccount(account)
        }
    }

    private Boolean updateIfExpired(Owner owner) {
        TimeDuration td = TimeCategory.minus(new Date(), owner.collectionTimestamp)
        if (td.getHours() > 23 || td.getDays() > 0) {
            this.updateOwner(owner.account)
            logger.info("Owner {} was updated, because it was expired", owner.account)
            return true
        } else {
            return false
        }

    }

    @Scheduled(fixedRate = 500000L)
    void updateExpired() {
        ownerRepository.findAll().each { Owner owner ->
            updateIfExpired(owner)
        }
    }
}
