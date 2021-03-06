package de.ctimm.service

import de.ctimm.dao.BillDao
import de.ctimm.dao.BillJPARepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import groovy.time.TimeCategory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.text.SimpleDateFormat

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

    private boolean forceReloadOwner = false

    @Override
    boolean getForceReloadOwner() {
        return forceReloadOwner
    }

    @Override
    void setForceReloadOwner(boolean forceReloadOwner) {
        this.forceReloadOwner = forceReloadOwner
    }

    @Autowired
    FacturaServiceImpl(ResponseParser responseParser, BillDao billDao, OwnerService ownerService, BillJPARepository billJPARepository) {
        this.responseParser = responseParser
        this.billDao = billDao
        this.ownerService = ownerService
        this.billJPARepository = billJPARepository
    }

    private ensureBillDataIsfilled(Bill bill) {
        if (bill.getXml() == null) {
            bill.setXml(responseParser.getXml(bill))
        }
        if (bill.total == null) {
            bill.total = Double.valueOf((String) bill.getXml().infoFactura.importeTotal.text())
            bill.discounts = Double.valueOf((String) bill.getXml().infoFactura.totalDescuento.text())
            bill.identification = bill.getXml().infoFactura.identificacionComprador.text()
            billJPARepository.save(bill)
            logger.info("Filled missing billData for {}", bill)
        }
        if (bill.dateOfNecessaryPayment == null) {
            use(TimeCategory) {
                bill.dateOfNecessaryPayment =
                        new SimpleDateFormat("dd/MM/yyyy").parse(bill.xml.infoFactura.fechaEmision.text()) +
                                (Integer.valueOf(bill.xml.infoFactura.pagos.pago.plazo.text())).day
            }

        }
    }

    private void cleanBills(Owner owner) {
        logger.info("Starting to clean bills for {}", owner)
        def bills = owner.billsList.sort { it.id }
        def billstoKeep = []
        def billstoDelete = []
        bills.each {
            if (!billstoKeep.contains(it)) {
                billstoKeep.add(it)
            } else {
                billstoDelete.add(it)
            }
        }
        logger.info("Will delete {} bills for owner {}", billstoDelete.size(), owner)
        logger.info("Will keep {} bills for owner {}", billstoKeep.size(), owner)
        billstoDelete.each {
            billJPARepository.delete(it)
        }
        if (billstoDelete.size() > 0) {
            owner.billsList = billstoKeep
            ownerService.updateOwner(owner.account)
        }
    }

    @Override
    public void housekeep() {
        List<Owner> owners = ownerService.getAllOwners()
        owners.each {
            cleanBills(it)
        }

        def billsList = billJPARepository.findAll()
        billsList.each {
            // Only fix values for those, that already have xml fetched.
            // We don't want to load a bunch of unnecessary xmls
            if (it.xml != null) {
                ensureBillDataIsfilled(it)
            }
        }


    }

    @Override
    public Bill getBill(Integer account, int age) {
        logger.debug("getBill for account {} and age {}", account, age)
        getOwner(account)
        Owner owner = ownerService.getOwner(account)
        ArrayList<Bill> bills = owner.billsList.sort { it.issued }
        billJPARepository.save(bills)
        def length = bills.size()
        Bill bill = bills.get(length - age - 1)
        if (bill.getXml() == null) {
            ensureBillDataIsfilled(bill)
            billJPARepository.save(bill)
            owner.addBill(bill)
        } else {
            ensureBillDataIsfilled(bill)
        }
        return bill
    }

    @Override
    List<Bill> getBills(Integer account) {
        Owner owner = getOwner(account)
        List<Bill> billList = owner.billsList.sort { it.issued }
        def length = billList.size()
        [length - 1, length - 2, length - 3].each {
            if (it >= 0) {
                ensureBillDataIsfilled(billList.get(it))
            }
        }
        return billList
    }

    @Override
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
