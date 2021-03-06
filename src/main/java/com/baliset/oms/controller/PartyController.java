package com.baliset.oms.controller;

import com.baliset.oms.model.*;
import com.baliset.oms.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class PartyController
{
  private final PartyService partyService;

  @Autowired public PartyController(PartyService partyService) {this.partyService = partyService;}

  @RequestMapping(value={"party/list"}, method = RequestMethod.GET,  produces={"application/json"})
  public ResponseEntity<List<Party>> list()
  {
    List<Party> list       = partyService.list();
    HttpStatus  httpStatus =  HttpStatus.OK;
    return new ResponseEntity<>(list,httpStatus);
  }

  @RequestMapping(value="/party/create/{id}", method = RequestMethod.POST, produces={"application/json"})
  public ResponseEntity<Party>
  create(@PathVariable("id") String name) throws Exception
  {
    Party p = partyService.create(name);     // if it fails it will throw an exception which should be ok
    HttpStatus httpStatus =  HttpStatus.OK;
    return new ResponseEntity<>(p,httpStatus);
  }

  @RequestMapping(value="/party/lookup/{id}", method = RequestMethod.GET, produces={"application/json"})
  public ResponseEntity<Party>
  lookup(@PathVariable("id") String name) throws Exception
  {
    Party p = partyService.lookup(name);
    HttpStatus httpStatus =  p != null? HttpStatus.OK: HttpStatus.NOT_FOUND;
    return new ResponseEntity<>(p,httpStatus);
  }
  
}
