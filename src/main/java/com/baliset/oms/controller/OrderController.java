package com.baliset.oms.controller;

import com.baliset.oms.model.*;
import com.baliset.oms.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

public class OrderController
{

  private final OrderService orderService;
  private final PartyService partyService;

  @Autowired public OrderController(OrderService orderService, PartyService partyService)
  {
    this.orderService = orderService;
    this.partyService = partyService;
  }


  @RequestMapping(value="/order/bid", method = RequestMethod.POST, produces={"application/json"})
  public ResponseEntity<Quote>
  bid(@RequestBody OrderRequest req) throws Exception
  {
    orderService.bid(req.symbol, partyService.lookup(req.party), req.quantity, req.price);

    HttpStatus httpStatus =  HttpStatus.OK;
    return new ResponseEntity<>(orderService.top(req.symbol),httpStatus);
  }

  @RequestMapping(value="/order/ask", method = RequestMethod.POST, produces={"application/json"})
  public ResponseEntity<Quote>
  ask(@RequestBody OrderRequest req) throws Exception
  {
    orderService.ask(req.symbol, partyService.lookup(req.party), req.quantity, req.price);

    HttpStatus httpStatus =  HttpStatus.OK;
    return new ResponseEntity<>(orderService.top(req.symbol),httpStatus);
  }

}
