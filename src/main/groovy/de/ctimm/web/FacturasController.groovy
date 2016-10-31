package de.ctimm.web

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
    ResponseEntity<String> getOwner(
            @PathVariable Integer account
    ) {
        logger.info("Start creating Owner for {}", account)
        String owner = facturaService.getOwner(account);
        return new ResponseEntity<String>(owner, HttpStatus.OK)
    }

    @RequestMapping(value = "/{account}", method = RequestMethod.GET)
    ResponseEntity<Map<String, String>> getSummary(
            @PathVariable Integer account
    ) {
        def start = System.currentTimeMillis()
        logger.info("Start creating summary for {}", account)
        Map<String, String> values = facturaService.getSummary(account, false)
        logger.info("Finished summary creation for {}", account)
        def stop = System.currentTimeMillis()
        logger.info("Execution took {} milliseconds", stop - start)
        return new ResponseEntity<Map<String, String>>(values, HttpStatus.OK)

    }

    @RequestMapping(value = "/force/{account}", method = RequestMethod.GET)
    ResponseEntity<Map<String, String>> getSummaryForceReload(
            @PathVariable Integer account
    ) {
        def start = System.currentTimeMillis()
        logger.info("Start creating summary for {}", account)
        Map<String, String> values = facturaService.getSummary(account, true)
        logger.info("Finished summary creation for {}", account)
        def stop = System.currentTimeMillis()
        logger.info("Execution took {} milliseconds", stop - start)
        return new ResponseEntity<Map<String, String>>(values, HttpStatus.OK)

    }
}
