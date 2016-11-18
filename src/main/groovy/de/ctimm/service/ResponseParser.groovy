package de.ctimm.service

import de.ctimm.dao.BillDao
import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Component
class ResponseParser {

    private BillDao billDao

    @Autowired
    ResponseParser(BillDao billDao) {
        this.billDao = billDao
    }

    def getXml(Bill bill) {
        def slurper = new XmlSlurper()
        String xml = billDao.getBillXml(bill.xmlNumber)
        if (xml.isEmpty()){
            throw new RuntimeException("No xml data provided by sourceserver for bill identifier " + bill.xmlNumber + " from " + bill.issued)
        }
        def xmlxml = slurper.parseText(xml)
        if (xmlxml.numeroAutorizacion.text().isEmpty()) {
            throw new RuntimeException("No ruc processing available")
        }
        String comprobante = xmlxml.comprobante.text().replace("<![CDATA[", "").replace("]]>", "")
        def xmlxmlxml = slurper.parseText(comprobante)
        return xmlxmlxml
    }


    static List<Bill> getBills(String html, Integer account) {
        html = html.replace('<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">', '<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>')
        html = html.replace("Font", "font")
        html = html.replace("<br>", "")
        html = html.replace('<input type="submit" name="submit" value="Regresar">', '<input type="submit" name="submit" value="Regresar"/>')

        List<Bill> billsList = new ArrayList<Bill>()

        Document doc = Jsoup.parse(html)

        Elements jsoupBills = doc.select("body").first()
                .select("article").get(2)
                .select("tr")

        for (int i = 1; i < jsoupBills.indexOf(jsoupBills.last()); i++) {
            Bill bill = new Bill(account)
            Elements jsoupBill = jsoupBills.get(i).select("td")
            bill.number = jsoupBill.get(0).text().replace("FACTURA", "").trim()

            String[] date = jsoupBill.get(1).text().replace("Emitido: ", "").replaceAll("Mes consumo.*", "").split("-")
            String day = date[0].trim()
            String month = date[1].trim()
            String year = date[2].trim()
            bill.issued = Timestamp.valueOf(year + "-" + month + "-" + day + " 00:00:00")

            bill.accessKey = jsoupBill.get(2).text().trim()

            String dateOfAuthorization = jsoupBill.get(3).text().split("Autoriza")[0].replace("Fecha: ", "").replace("T", " ").replace("-05:00", "")
            if (dateOfAuthorization.isEmpty() || dateOfAuthorization.startsWith("null")) {
                bill.dateOfAuthorization = null
            } else {
                bill.dateOfAuthorization = Timestamp.valueOf(dateOfAuthorization)
            }

            bill.xmlNumber = Integer.valueOf(jsoupBill.get(4).select("font").first().select("a").first().attributes().first().value.split("=")[1])

            billsList.add(bill)
        }
        return billsList
    }

    Owner getOwnerInformation(Integer account) {
        Owner owner = new Owner(account)

        String html = billDao.getOwnerHtml(account)
        Document doc = Jsoup.parse(html)
        String name = doc.select("body").first()
                .select("form").get(0)
                .select("table").first()
                .select("tr").first()
                .select("td").get(1)
                .select("input").first()
                .attr("value")
        owner.name = name
        String direction = doc.select("body").first()
                .select("form").get(0)
                .select("table").first()
                .select("tr").get(1)
                .select("td").get(1)
                .select("input").first()
                .attr("value")
        owner.direction = direction
        String email = doc.select("body").first()
                .select("form").get(0)
                .select("table").first()
                .select("tr").get(2)
                .select("td").get(1)
                .select("input").first()
                .attr("value")
        owner.email = email
        String email1 = doc.select("body").first()
                .select("form").get(0)
                .select("table").first()
                .select("tr").get(3)
                .select("td").get(1)
                .select("input").first()
                .attr("value")
        if (!email1.isEmpty()) {
            owner.email1 = email1
        }
        String email2 = doc.select("body").first()
                .select("form").get(0)
                .select("table").first()
                .select("tr").get(4)
                .select("td").get(1)
                .select("input").first()
                .attr("value")
        if (!email2.isEmpty()) {
            owner.email2 = email2
        }
        String cellphone = doc.select("body").first()
                .select("form").get(0)
                .select("table").first()
                .select("tr").get(5)
                .select("td").get(1)
                .select("input").first()
                .attr("value")
        if (!cellphone.isEmpty()) {
            owner.cellphone = cellphone
        }
        String phone = doc.select("body").first()
                .select("form").get(0)
                .select("table").first()
                .select("tr").get(6)
                .select("td").get(1)
                .select("input").first()
                .attr("value")
        if (!phone.isEmpty()) {
            owner.phone = phone
        }
        owner


    }


}
