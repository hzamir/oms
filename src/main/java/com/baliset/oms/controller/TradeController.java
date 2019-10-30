package com.baliset.oms.controller;

import com.baliset.oms.model.*;
import com.baliset.oms.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.format.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class TradeController
{
  private final TradeService tradeService;
  private final ZoneOffset zoneOffset;

  @Autowired public TradeController( TradeService tradeService)
  {
    this.tradeService = tradeService;
    zoneOffset = LocalDateTime.now()
                              .atZone(ZoneId.of("America/New_York"))
                              .getOffset();
  }

  private long toMillis(LocalDateTime localDateTime)
  {
    return localDateTime.toInstant(zoneOffset).toEpochMilli();
  }

  @RequestMapping(value="/trade/listfromto/{symbol}", method = RequestMethod.GET, produces={"application/json"})
  public ResponseEntity<List<TradeEx>>
  listSymbolFromTo(@PathVariable(name = "symbol") String symbol,
                   @RequestParam("from") @DateTimeFormat(pattern = "yyyyMMdd-HHmmss") LocalDateTime from,
                   @RequestParam("to")   @DateTimeFormat(pattern = "yyyyMMdd-HHmmss") LocalDateTime to
  ) throws Exception
  {
    long millisFrom = toMillis(from);
    long millistTo = toMillis(to);
    return new ResponseEntity<>(tradeService.list(symbol, millisFrom, millistTo),HttpStatus.OK);
  }


  @RequestMapping(value="/trade/list/{symbol}", method = RequestMethod.GET, produces={"application/json"})
  public ResponseEntity<List<TradeEx>>
  listSymbol(@PathVariable(name = "symbol") String symbol) throws Exception
  {
    return new ResponseEntity<>(tradeService.list(symbol),HttpStatus.OK);
  }

  @RequestMapping(value="/trade/listfromto", method = RequestMethod.GET, produces={"application/json"})
  public ResponseEntity<List<TradeEx>>
  listAllFromTo(
      @RequestParam("from") @DateTimeFormat(pattern = "yyyyMMdd-HHmmss") LocalDateTime from,
      @RequestParam("to")   @DateTimeFormat(pattern = "yyyyMMdd-HHmmss") LocalDateTime to
  ) throws Exception
  {
    return new ResponseEntity<>(tradeService.listAll(toMillis(from), toMillis(to)),HttpStatus.OK);
  }

  @RequestMapping(value="/trade/list", method = RequestMethod.GET, produces={"application/json"})
  public ResponseEntity<List<TradeEx>>
  listAll() throws Exception
  {
    return new ResponseEntity<>(tradeService.listAll(),HttpStatus.OK);
  }
  
}
