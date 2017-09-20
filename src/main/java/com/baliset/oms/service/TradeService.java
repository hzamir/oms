package com.baliset.oms.service;

import com.baliset.oms.model.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Service
public class TradeService
{
  private static Logger logger = LoggerFactory.getLogger(TradeService.class);
  private ArrayList<TradeEx> tradeHistory = new ArrayList<>(1000);

  @SuppressWarnings(value = "unchecked")
  public  List<TradeEx> listAll()
  {
    return (List<TradeEx>)tradeHistory.clone();
  }

  public List<TradeEx> listAll(long from, long to)
  {
    return tradeHistory.stream()
                       .filter(tradeEx -> tradeEx.getTimestamp() >= from && tradeEx.getTimestamp() < to)
                       .collect(Collectors.toList());

  }

  public List<TradeEx> list(String symbol)
  {
    return tradeHistory.stream()
                       .filter(tradeEx -> tradeEx.getSymbol().equals(symbol))
                       .collect(Collectors.toList());
  }

  public List<TradeEx> list(String symbol, long from, long to)
  {
    return tradeHistory.stream()
                       .filter(tradeEx -> tradeEx.getSymbol().equals(symbol))
                       .filter(tradeEx -> tradeEx.getTimestamp() >= from && tradeEx.getTimestamp() < to)
                       .collect(Collectors.toList());

  }

  public void reportTrades(List<TradeEx> trades)
  {
    addToHistory(trades);
    trades.forEach(tradeEx -> logger.info(tradeEx.toString()));
  }


  private void addToHistory(List<TradeEx> trades)
  {
    tradeHistory.addAll(trades);
  }
}
