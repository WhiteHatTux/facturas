package de.ctimm.web

import de.ctimm.domain.Owner
import de.ctimm.service.OwnerService
import io.swagger.annotations.Api
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by:
 *
 * @author Christopher Timm (chris@timmch.de)
 */
@Controller
@RequestMapping(value = "/v1/admin/", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "/v1/admin",
        description = "Allows getting an overview over the whole current application state",
        produces = MediaType.APPLICATION_JSON_VALUE
)
class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class)

    @Autowired
    private OwnerService ownerService

    @RequestMapping(value = "accounts", method = RequestMethod.GET)
    public ResponseEntity<List<Integer>> getAccountList() {
        return new ResponseEntity<>(ownerService.getAccountList(), HttpStatus.OK)
    }

    @RequestMapping(value = "owners", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Integer>>> getOwnerList() {
        List<Owner> owners = ownerService.getAllOwners()
        def resultList = new ArrayList()
        owners.each {
            def resultMap = [:]
            resultMap.put(it.name, it.account)
            resultList.add(resultMap)
        }
        resultList = resultList.sort()
        return new ResponseEntity<>(resultList, HttpStatus.OK)
    }
}
