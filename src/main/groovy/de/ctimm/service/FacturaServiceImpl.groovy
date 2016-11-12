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

    Bill getLastBill(Integer account) {
        logger.debug("getlastBill")
        return getBill(account, 0);
    }

    Bill getBill(Integer account, int sort) {
        logger.debug("getbill {}", sort)
        getOwner(account)
        Owner owner = ownerService.getOwner(account)
        ArrayList<Bill> bills = owner.billsList.sort { it.issued }
        def length = bills.size()
        Bill bill = bills.get(length - sort - 1)
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
    Double getTotalAmount(Integer account) {
        logger.debug("getTotal")
        def bill = getLastBill(account)
        Double.valueOf(bill.xml.infoFactura.importeTotal.text())
    }

    @Override
    String getOwnerName(Integer account) {
        logger.debug("getOwnerName")
        def bill = getLastBill(account)
        bill.xml.infoFactura.razonSocialComprador.text()
    }

    @Override
    String getIdentification(Integer account) {
        logger.debug("getid")
        def bill = getLastBill(account)
        bill.xml.infoFactura.identificacionComprador.text()
    }

    @Override
    Double getDiscounts(Integer account) {
        logger.debug("getDiscount")
        def bill = getLastBill(account)
        Double.valueOf(bill.xml.infoFactura.totalDescuento.text())
    }

    @Override
    Timestamp getIssueDate(Integer account) {
        logger.debug("getIssue")
        def bill = getLastBill(account)
        bill.issued
    }

    @Override
    Map<String, Object> getSummary(Integer account, boolean forceReload) {
        this.forceReloadOwner = forceReload
        logger.debug("Start creating summary for {}", account)
        Map<String, Object> values = new HashMap<>()
        values.put("Owner", getOwner(account))
        values.put("Identification", getIdentification(account))
        values.put("Discounts", String.valueOf(getDiscounts(account)))
        values.put("Total", String.valueOf(getTotalAmount(account)))
        values.put("Issued", getIssueDate(account).toString())
        logger.debug("Finished summary creation for {}", account)
        return values
    }
}
