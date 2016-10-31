package de.ctimm.service

import de.ctimm.domain.Bill
import de.ctimm.domain.BillRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestOperations

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

    private RestOperations restTemplate

    private BillRepository billRepository

    Bill bill

    boolean forceReload = false

    @Autowired
    FacturaServiceImpl(ResponseParser responseParser, RestOperations restTemplate, BillRepository billRepository) {
        this.responseParser = responseParser
        this.restTemplate = restTemplate
        this.billRepository = billRepository
    }

    void getLastComprobante(Integer account) {
        if (billRepository.getBill(account) == null || forceReload) {
            //account = 194799
            String url = "http://www1.eeasa.com.ec:8080/FacturaElec/listadoFE.jsp"

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>()

            map.add("cuenta", String.valueOf(account))
            map.add("submit", "Buscar")

            String html = restTemplate.postForObject(
                    url,
                    map,
                    String.class)
            List<Bill> bills = responseParser.getBills(html, account)
            Bill bill2 = bills[0]
            bill2.xml = responseParser.getXml(bill2)
            billRepository.addBill(bill2)
        }
        bill = billRepository.getBill(account)
    }

    @Override
    Double getTotalAmount(Integer account) {
        getLastComprobante(account)
        Double.valueOf(bill.xml.infoFactura.importeTotal.text())
    }

    @Override
    String getOwner(Integer account) {
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
        Map<String, String> values = new HashMap<>()
        values.put("Owner", getOwner(account))
        values.put("Identification", getIdentification(account))
        values.put("Discounts", String.valueOf(getDiscounts(account)))
        values.put("Total", String.valueOf(getTotalAmount(account)))
        values.put("Issued", getIssueDate(account).toString())

        logger.debug("Finished summary creation for {}", account)
        return values
    }
}
