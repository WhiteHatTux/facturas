package de.ctimm.service

import de.ctimm.dao.BillDao
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.sql.Timestamp

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
@Component
@Scope
class FacturaServiceImpl implements FacturaService {

    private static final Logger logger = LoggerFactory.getLogger(FacturaServiceImpl.class);

    private ResponseParser responseParser

    private OwnerService ownerService

    private BillDao billDao

    boolean forceReloadOwner = false

    @Autowired
    FacturaServiceImpl(ResponseParser responseParser, BillDao billDao, OwnerService ownerService) {
        this.responseParser = responseParser
        this.billDao = billDao
        this.ownerService = ownerService
    }

    Bill getBill(Integer account, int age) {
        logger.debug("getBill for account {} and age {}", account, age)
        getOwner(account)
        Owner owner = ownerService.getOwner(account)
        ArrayList<Bill> bills = owner.billsList.sort { it.issued }
        def length = bills.size()
        Bill bill = bills.get(length - age - 1)
        if (bill.xml == null) {
            bill.xml = responseParser.getXml(bill)
            owner.addBill(bill)
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
        Double.valueOf((String) bill.xml.infoFactura.importeTotal.text())
    }

    @Override
    String getOwnerName(Integer account, int age) {
        logger.debug("getOwnerName")
        def bill = getBill(account, age)
        bill.xml.infoFactura.razonSocialComprador.text()
    }

    @Override
    String getIdentification(Integer account, int age) {
        logger.debug("getid")
        def bill = getBill(account, age)
        bill.xml.infoFactura.identificacionComprador.text()
    }

    @Override
    Double getDiscounts(Integer account, int age) {
        logger.debug("getDiscount")
        def bill = getBill(account, age)
        Double.valueOf((String) bill.xml.infoFactura.totalDescuento.text())
    }

    @Override
    Timestamp getIssueDate(Integer account, int age) {
        logger.debug("getIssue")
        def bill = getBill(account, age)
        bill.issued
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
        if (age > owner.billsList.size()-1){
            values.put("message", "The requested bill does not exist, Returning the oldest bill")
            age = owner.billsList.size()-1
        }

        values.put("Identification", getIdentification(account, age))
        values.put("Discounts", String.valueOf(getDiscounts(account, age)))
        values.put("Total", String.valueOf(getTotalAmount(account, age)))
        values.put("Issued", getIssueDate(account, age).toString())
        logger.debug("Finished summary creation for {}", account)
        return values
    }
}
