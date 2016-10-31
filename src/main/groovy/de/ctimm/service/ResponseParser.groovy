package de.ctimm.service

import de.ctimm.domain.Bill
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations

import java.sql.Timestamp

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
@Component
class ResponseParser {

    private String baseUrl

    private String xmlParameter

    RestOperations restTemplate;

    @Autowired
    ResponseParser(RestOperations restTemplate,
                   @Value('${baseUrl}') String baseUrl,
                   @Value('${xmlParameter}') String xmlParameter) {
        assert restTemplate != null
        this.restTemplate = restTemplate
        this.baseUrl = baseUrl
        this.xmlParameter = xmlParameter
    }

    def getXml(Bill bill) {
        def slurper = new XmlSlurper()
        String xml = restTemplate.getForObject(baseUrl + xmlParameter + bill.xmlNumber, String.class)
        def xmlxml = slurper.parseText(xml)
        assert xmlxml.numeroAutorizacion.text().equals(bill.accessKey)
        def comprobante = xmlxml.comprobante.text().replace("<![CDATA[", "").replace("]]>", "")
        def xmlxmlxml = slurper.parseText(comprobante)
        return xmlxmlxml
    }

    List<Bill> getBills(String html, Integer account) {
        html = html.replace('<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">', '<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>')
        html = html.replace("Font", "font")
        html = html.replace("<br>", "")
        html = html.replace('<input type="submit" name="submit" value="Regresar">', '<input type="submit" name="submit" value="Regresar"/>')

        List<Bill> billsList = new ArrayList<Bill>()

        Document doc = Jsoup.parse(html)

        String owner = doc.select("body").first()

                .select("article").get(1)
                .select("tr").first()
                .select("td").first()
                .select("font").first().text()

        Elements jsoupBills = doc.select("body").first()
                .select("article").get(2)
                .select("tr")

        for (int i = 1; i < jsoupBills.indexOf(jsoupBills.last()); i++) {
            Bill bill = new Bill()
            Elements jsoupBill = jsoupBills.get(i).select("td")
            bill.number = jsoupBill.get(0).text().replace("FACTURA", "").trim()
            String[] date = jsoupBill.get(1).text().replace("Emitido: ", "").replaceAll("Mes consumo.*", "").split("-")
            String day = date[0].trim()
            String month = date[1].trim()
            String year = date[2].trim()
            bill.issued = Timestamp.valueOf(year + "-" + month + "-" + day + " 00:00:00")
            bill.accessKey = jsoupBill.get(2).text().trim()
            bill.dateOfAuthorization = Timestamp.valueOf(jsoupBill.get(3).text().split("Autoriza")[0].replace("Fecha: ", "").replace("T", " ").replace("-05:00", ""))
            bill.xmlNumber = Integer.valueOf(jsoupBill.get(4).select("font").first().select("a").first().attributes().first().value.split("=")[1])
            bill.owner = owner
            bill.account = account
            billsList.add(bill)
        }
        return billsList
    }
}
