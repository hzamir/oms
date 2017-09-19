package com.baliset.oms.service;

import com.baliset.oms.model.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class TradeService
{
  private static Logger logger = LoggerFactory.getLogger(TradeService.class);

  public void reportTrades(List<TradeEx> trades)
  {
    trades.stream().forEach(tradeEx -> logger.info(tradeEx.toString()));
  }
}
