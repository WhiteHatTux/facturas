package de.ctimm.web

import de.ctimm.domain.Owner
import de.ctimm.service.FacturaService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Controller
@RequestMapping
class FacturasController {

    private static final Logger logger = LoggerFactory.getLogger(FacturasController.class)

    private FacturaService facturaService;

    @Autowired
    FacturasController(FacturaService facturaService) {
        this.facturaService = facturaService
    }

    @RequestMapping(value = "/{account}/total", method = RequestMethod.GET)
    ResponseEntity<Double> getCurrentCost(
            @PathVariable Integer account
    ) {

        logger.info("Start creating currentTotal for {}", account)
        Double totalAmountThisMonth = facturaService.getTotalAmount(account)
        return new ResponseEntity<Double>(totalAmountThisMonth, HttpStatus.OK)
    }

    @RequestMapping(value = "/{account}/owner", method = RequestMethod.GET)
    ResponseEntity<Owner> getOwner(
            @PathVariable Integer account
    ) {
        logger.info("Start creating Owner for {}", account)
        Owner owner = facturaService.getOwner(account);
        return new ResponseEntity<Owner>(owner, HttpStatus.OK)
    }

    @RequestMapping(value = "/{account}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getSummary(
            @PathVariable Integer account
    ) {
        logger.info("Start creating summary for {}", account)
        Map<String, Object> values = facturaService.getSummary(account, false)
        logger.info("Finished summary creation for {}", account)
        return new ResponseEntity<Map<String, Object>>(values, HttpStatus.OK)
    }

    @RequestMapping(value = "/{account}/{age}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getSummaryForBill(
            @PathVariable Integer account,
            @PathVariable Integer age
    ) {
        logger.info("Start creating summary for {} and age {}", account, age)
        Map<String, Object> values = facturaService.getSummaryForBill(account, age)
        logger.info("Finished summary creation for {} and age {}", account, age)
        return new ResponseEntity<Map<String, Object>>(values, HttpStatus.OK)
    }

    @RequestMapping(value = "/force/{account}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getSummaryForceReload(
            @PathVariable Integer account
    ) {
        logger.info("Start creating summary for {}", account)
        Map<String, Object> values = facturaService.getSummary(account, true)
        logger.info("Finished summary creation for {}", account)
        return new ResponseEntity<Map<String, Object>>(values, HttpStatus.OK)
    }
}
