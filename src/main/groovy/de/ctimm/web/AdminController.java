package de.ctimm.web;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import de.ctimm.service.FacturaService;
import de.ctimm.service.OwnerService;
import io.swagger.annotations.Api;

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
public class AdminController {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

  @Autowired
  private OwnerService ownerService;

  @RequestMapping(value = "accountList", method = RequestMethod.GET)
  public ResponseEntity<List<Integer>> getAccountList() {
    return new ResponseEntity<>(ownerService.getAccountList(), HttpStatus.OK);
  }
}
