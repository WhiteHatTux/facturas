package de.ctimm.web

import de.ctimm.domain.Bill
import de.ctimm.domain.Owner
import de.ctimm.service.FacturaService
import de.ctimm.service.ResponseParser
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "/",
        description = "Allows consulting invoice data for electricity in Ambato, Ecuaodor",
        produces = MediaType.APPLICATION_JSON_VALUE
)
class FacturasController {

    private static final Logger logger = LoggerFactory.getLogger(FacturasController.class)

    private FacturaService facturaService;

    @Autowired
    FacturasController(FacturaService facturaService) {
        this.facturaService = facturaService
    }

    @RequestMapping(value = "/v1/{account}/total", method = RequestMethod.GET)
    ResponseEntity<Double> getCurrentCost(
            @PathVariable Integer account
    ) {

        logger.info("Start creating currentTotal for {}", account)
        Double totalAmountThisMonth = facturaService.getTotalAmount(account, 0)
        return new ResponseEntity<Double>(totalAmountThisMonth, HttpStatus.OK)
    }

    @RequestMapping(value = "/v1/{account}/owner", method = RequestMethod.GET)
    ResponseEntity<Owner> getOwner(
            @PathVariable Integer account
    ) {
        logger.info("Start creating Owner for {}", account)
        Owner owner = facturaService.getOwner(account);
        return new ResponseEntity<Owner>(owner, HttpStatus.OK)
    }

    @RequestMapping(value = "/v1/force/{account}/{age}/bill", method = RequestMethod.GET)
    ResponseEntity<Bill> getForceBillForAge(
            @PathVariable Integer account,
            @PathVariable Integer age
    ){
        facturaService.setForceReloadOwner(true)
        getBillForAge(account, age)
    }

    /**
     * Get a list of all bills for the corrsponding account
     * this API will deliver all bills, but the {@link Bill#total} value might be null.
     * It is however <b>always</b> filled for the <b>3</b> most current bill
     *
     * @param account
     * @return
     */
    @RequestMapping(value = "/v1/{account}/bills", method = RequestMethod.GET)
    ResponseEntity<List<Bill>> getBillsForAccount(
            @PathVariable Integer account,
            @RequestParam(name = "force", defaultValue = "false") Boolean forceUpdate
    ){
        facturaService.setForceReloadOwner(forceUpdate)
        List<Bill> billList = facturaService.getBills(account)
        return new ResponseEntity<List<Bill>>(billList, HttpStatus.OK)
    }

    @RequestMapping(value = "/v1/housekeep", method = RequestMethod.POST)
    ResponseEntity<Void> housekeep(){
        logger.info("Start housekeeping")
        facturaService.housekeep()
        logger.info("Finished housekeeping")
        return new ResponseEntity<Void>(HttpStatus.OK)
    }

    @RequestMapping(value = "/v1/{account}/{age}/bill", method = RequestMethod.GET)
    ResponseEntity<Bill> getBillForAge(
            @PathVariable Integer account,
            @PathVariable Integer age
    ){
        Bill bill = facturaService.getBill(account, age)
        return new ResponseEntity<Bill>(bill, HttpStatus.OK);
    }

    /**
     * Create a summary for the corresponding account with the ownerdata and the newest bill
     * @param account
     * @return
     */
    @ApiOperation(
            value = "Get a basic summary of the requested account and the last bill",
            responseContainer = 'Map<String,Object>'
    )
    @RequestMapping(value = "/v1/{account}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getSummary(
            @PathVariable Integer account
    ) {
        logger.info("Start creating summary for {}", account)
        Map<String, Object> values = new HashMap<>()
        try {
            values = facturaService.getSummary(account, false)
        } catch (RuntimeException re) {
            return processRuntimeException(values, re)
        }
        logger.info("Finished summary creation for {}", account)
        return new ResponseEntity<Map<String, Object>>(values, HttpStatus.OK)
    }

    private ResponseEntity<Map<String, Object>> processRuntimeException(HashMap<String, Object> values, RuntimeException re) {
        // TODO blacklist requestParams, that don't work for a certain time
        values.put("ErrorMessage", "Malformed request could not be processed " + re.getMessage())
        logger.warn("Error processing request with message {}", re.message)
        re.printStackTrace()
        return new ResponseEntity<Map<String, Object>>(values, HttpStatus.BAD_REQUEST)
    }

    /**
     * Get the owner data and the bill of the supplied age
     *
     * @param account
     * @param age
     * @return
     */
    @RequestMapping(value = "/v1/{account}/{age}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getSummaryForBill(
            @ApiParam(value = "AcountNumber as seen on the box in front of the house", required = true) @PathVariable Integer account,
            @ApiParam(value = "0 represents the current invoice, higher values represent older invoices", required = true, defaultValue = "0") @PathVariable Integer age
    ) {
        logger.info("Start creating summary for {} and age {}", account, age)
        Map<String, Object> values = new HashMap<>()
        try {
            values = facturaService.getSummaryForBill(account, age)
        } catch (RuntimeException re) {
            return processRuntimeException(values, re)
        }
        logger.info("Finished summary creation for {} and age {}", account, age)
        return new ResponseEntity<Map<String, Object>>(values, HttpStatus.OK)
    }

    /**
     * Force the update of the user for the requested account and return the newest values
     * @param account
     * @return
     */
    @ApiOperation(
            value = "Get a basic summary of the requested account and the last bill and update data, that can get old",
            responseContainer = 'Map<String,Object>'
    )
    @RequestMapping(value = "/v1/force/{account}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getSummaryForceReload(
            @PathVariable Integer account
    ) {
        logger.info("Start creating force summary for {}", account)
        Map<String, Object> values = new HashMap<>()
        try {
            values = facturaService.getSummary(account, true)
        } catch (RuntimeException re) {
            return processRuntimeException(values, re)
        }
        logger.info("Finished force summary creation for {}", account)
        return new ResponseEntity<Map<String, Object>>(values, HttpStatus.OK)
    }

}
