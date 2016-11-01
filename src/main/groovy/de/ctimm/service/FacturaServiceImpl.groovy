package de.ctimm.service

import de.ctimm.dao.BillDao
import de.ctimm.dao.BillRepository
import de.ctimm.dao.OwnerRepository
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.sql.Timestamp

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Component
@Scope("prototype")
class FacturaServiceImpl implements FacturaService {

    private static final Logger logger = LoggerFactory.getLogger(FacturaServiceImpl.class);

    private ResponseParser responseParser

    private BillRepository billRepository

    private OwnerRepository ownerRepository

    private BillDao billDao

    Bill bill

    Owner owner

    boolean forceReload = false

    @Autowired
    FacturaServiceImpl(ResponseParser responseParser, BillRepository billRepository, BillDao billDao, OwnerRepository ownerRepository) {
        this.responseParser = responseParser
        this.billRepository = billRepository
        this.billDao = billDao
        this.ownerRepository = ownerRepository
    }

    void getLastComprobante(Integer account) {
        if (billRepository.getBill(account) == null || forceReload) {
            String html = billDao.getBillHtml(account)
            List<Bill> bills = responseParser.getBills(html, account)
            Bill bill2 = bills[0]
            bill2.xml = responseParser.getXml(bill2)
            billRepository.addBill(bill2)
        }
        bill = billRepository.getBill(account)
    }

    Owner getOwner(Integer account) {
        if (ownerRepository.getOwner(account) == null || forceReload) {
            owner = responseParser.getOwnerInformation(account)
            ownerRepository.addOwner(owner)
        }
        owner = ownerRepository.getOwner(account)
    }

    @Override
    Double getTotalAmount(Integer account) {
        getLastComprobante(account)
        Double.valueOf(bill.xml.infoFactura.importeTotal.text())
    }

    @Override
    String getOwnerName(Integer account) {
        getLastComprobante(account)
        bill.xml.infoFactura.razonSocialComprador.text()
    }

    @Override
    String getIdentification(Integer account) {
        getLastComprobante(account)
        bill.xml.infoFactura.identificacionComprador.text()
    }

    @Override
    Double getDiscounts(Integer account) {
        getLastComprobante(account)
        Double.valueOf(bill.xml.infoFactura.totalDescuento.text())
    }

    @Override
    Timestamp getIssueDate(Integer account) {
        getLastComprobante(account)
        bill.issued
    }

    @Override
    Map<String, String> getSummary(Integer account, boolean forceReload) {
        this.forceReload = forceReload
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
