package de.ctimm.service

import de.ctimm.dao.BillDao
import de.ctimm.dao.BillJPARepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.annotation.PostConstruct

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Component
@Scope
@Transactional
class FacturaServiceImpl implements FacturaService {

    private static final Logger logger = LoggerFactory.getLogger(FacturaServiceImpl.class);

    private ResponseParser responseParser

    private OwnerService ownerService

    private BillJPARepository billJPARepository

    private BillDao billDao

    boolean forceReloadOwner = false

    @Autowired
    FacturaServiceImpl(ResponseParser responseParser, BillDao billDao, OwnerService ownerService, BillJPARepository billJPARepository) {
        this.responseParser = responseParser
        this.billDao = billDao
        this.ownerService = ownerService
        this.billJPARepository = billJPARepository
    }

    @PostConstruct
    private void init(){
        def billsList = billJPARepository.findAll()
        billsList.each {
            ensureBillDataIsfilled(it)
        }
    }

    private ensureBillDataIsfilled(Bill bill) {
        if (bill.getXml() != null) {
            bill.total = Double.valueOf((String) bill.getXml().infoFactura.importeTotal.text())
            bill.discounts = Double.valueOf((String) bill.getXml().infoFactura.totalDescuento.text())
            bill.identification = bill.getXml().infoFactura.identificacionComprador.text()
            billJPARepository.save(bill)
            logger.info("Filled missing billData for {}", bill)
        }
    }

    Bill getBill(Integer account, int age) {
        logger.debug("getBill for account {} and age {}", account, age)
        getOwner(account)
        Owner owner = ownerService.getOwner(account)
        ArrayList<Bill> bills = owner.billsList.sort { it.issued }
        billJPARepository.save(bills)
        def length = bills.size()
        Bill bill = bills.get(length - age - 1)
        if (bill.getXml() == null) {
            bill.setXml(responseParser.getXml(bill))
            bill.total = Double.valueOf((String) bill.getXml().infoFactura.importeTotal.text())
            bill.discounts = Double.valueOf((String) bill.getXml().infoFactura.totalDescuento.text())
            bill.identification = bill.getXml().infoFactura.identificacionComprador.text()
            billJPARepository.save(bill)
            owner.addBill(bill)
        } else {
            if (bill.total == null) {
                ensureBillDataIsfilled(bill)
                logger.warn("Billdata was not up-to-date for bill {}", bill)
            }
        }
        return bill
    }

    Owner getOwner(Integer account) {
        logger.debug("getOwner")
        if (forceReloadOwner) {
            ownerService.updateOwner(account)
            forceReloadOwner = false
        }
        return ownerService.getOwner(account)
    }

    @Override
    Double getTotalAmount(Integer account, int age) {
        logger.debug("getTotal")
        def bill = getBill(account, age)
        bill.total
    }

    @Override
    String getIdentification(Integer account, int age) {
        logger.debug("getid")
        def bill = getBill(account, age)
        bill.identification
    }

    @Override
    Double getDiscounts(Integer account, int age) {
        logger.debug("getDiscount")
        def bill = getBill(account, age)
        bill.discounts
    }

    @Override
    Date getIssueDate(Integer account, int age) {
        logger.debug("getIssue")
        def bill = getBill(account, age)
        bill.issued
    }

    @Override
    Date getDateOfAuthorization(Integer account, int age) {
        logger.debug("getDateOfAuthorization")
        def bill = getBill(account, age)
        bill.dateOfAuthorization
    }

    @Override
    Map<String, Object> getSummary(Integer account, boolean forceReload) {
        this.forceReloadOwner = forceReload
        logger.debug("Start creating summary for {}", account)
        def values = getSummaryForBill(account, 0)
        return values
    }

    @Override
    Map<String, Object> getSummaryForBill(Integer account, int age) {
        Map<String, Object> values = new HashMap<>()
        values.put("Owner", getOwner(account))

        Owner owner = getOwner(account);
        if (age > owner.billsList.size() - 1) {
            values.put("message", "The requested bill does not exist, Returning the oldest bill")
            age = owner.billsList.size() - 1
        }

        values.put("account", account)
        values.put("bill", getBill(account, age))
        logger.debug("Finished summary creation for {}", account)
        return values
    }
}
