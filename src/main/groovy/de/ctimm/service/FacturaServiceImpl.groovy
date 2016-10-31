package de.ctimm.service

import de.ctimm.domain.Bill
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

    private ResponseParser responseParser;

    private RestOperations restTemplate;

    Bill bill

    @Autowired
    FacturaServiceImpl(ResponseParser responseParser, RestOperations restTemplate) {
        this.responseParser = responseParser
        this.restTemplate = restTemplate
    }

    void getLastComprobante(Integer account) {
        if (bill == null || bill.xml == null || bill.account != account) {
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
            bill = bills[0]
            bill.xml = responseParser.getXml(bill)
        } else {
            return
        }
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
    Map<String, String> getSummary(Integer account) {
        logger.info("Start creating summary for {}", account)
        Map<String, String> values = new HashMap<>()
        values.put("Owner", getOwner(account))
        values.put("Identification", getIdentification(account))
        values.put("Discounts", String.valueOf(getDiscounts(account)))
        values.put("Total", String.valueOf(getTotalAmount(account)))
        values.put("Issued", getIssueDate(account).toString())

        logger.info("Finished summary creation for {}", account)
        return values
    }
}
