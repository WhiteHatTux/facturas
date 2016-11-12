package de.ctimm.dao

import de.ctimm.domain.Bill
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
@Component
class BillDaoRest implements BillDao {

    private static final Logger logger = LoggerFactory.getLogger(BillDaoRest.class);
    private RestOperations restTemplate
    private String baseUrl
    private String xmlParameter

    BillDaoRest(RestOperations restTemplate,
                @Value('${baseUrl}') String baseUrl,
                @Value('${xmlParameter}') String xmlParameter) {
        assert restTemplate != null
        this.restTemplate = restTemplate
        this.baseUrl = baseUrl
        this.xmlParameter = xmlParameter
    }

    @Override
    String getBillHtml(int account) {
        String url = baseUrl + 'listadoFE.jsp'

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>()

        map.add("cuenta", String.valueOf(account))
        map.add("submit", "Buscar")

        String html = restTemplate.postForObject(
                url,
                map,
                String.class)
        html
    }

    @Override
    String getBillXml(Integer xmlNumber) {
        String result = ""
        def url = baseUrl + xmlParameter + xmlNumber
        try {
            result = restTemplate.getForObject(url, String.class)
        } catch (RestClientException ignored) {
            logger.warn("Request to {} failed ", url)
        }
        return result
    }

    @Override
    String getOwnerHtml(int account) {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>()

        map.add("cuenta", String.valueOf(account))
        map.add("submit", "Buscar")

        restTemplate.postForObject(baseUrl + 'registro.jsp', map, String.class)
    }
}
