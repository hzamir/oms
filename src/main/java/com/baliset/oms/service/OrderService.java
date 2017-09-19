package com.baliset.oms.service;

import com.baliset.oms.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.*;

@Service
public class OrderService
{
  public OrderService()
  {
    books = new HashMap<>();
  }


  // idempotent methods
  public Quote top(String symbol) {return book(symbol).topOfBook(); }
  public List<Quote> top()
  {
      return books.values().stream()
                  .map(OrderBook::topOfBook)
                  .collect(Collectors.toList());
  }

  // book creation
  void createBook(String symbol)
  {
    if (books.containsKey(symbol))
      throw new ExCreateBook(symbol);

    books.put(symbol, new OrderBook(symbol));
  }

  // order entry (executions of crossing bid-ask are automatic)
  public List<TradeEx> bid(String symbol, Party party, int quantity, double price)
  {
    return book(symbol).bid(party, price, quantity);
  }
  public List<TradeEx> ask(String symbol, Party party, int quantity, double price)
  {
    return book(symbol).ask(party, price, quantity);
  }
  

  //---- private methods ----
  private OrderBook book(String symbol)
  {
    OrderBook book = books.get(symbol);
    if (book == null)
      throw new ExBidOffer(symbol);
    return book;
  }

  // instance variables
  private Map<String, OrderBook> books;

  // static classes
  static class ExCreateBook extends RuntimeException { ExCreateBook(String s) { super(s);}}
  static class ExBidOffer   extends RuntimeException { ExBidOffer(String s)   { super(s);}}
}
