package com.baliset.oms;

import com.baliset.oms.model.*;
import org.junit.Assert;
import org.junit.Test;

public class OrderBookTest {

  @Test
  public void testBasicOrderBook() {
    OrderBook orderBook = new OrderBook("foo");
    Party party = new Party("testParty");

    orderBook.bid(party,430, 5);
    orderBook.bid(party, 430, 5);
    orderBook.bid(party, 430.10, 10);
    Assert.assertEquals("{bids={430.1=10, 430.0=10}, asks={}}", orderBook.getBookAsString());

    orderBook.ask(party,432, 2);
    orderBook.ask(party, 433, 25);
    orderBook.ask(party, 432.5, 8);
    orderBook.ask(party,433, 3);

    System.out.println("-->{bids={430.1=10, 430.0=10}, asks={432.0=2, 432.5=8, 433.0=28}}");
    Assert.assertEquals("{bids={430.1=10, 430.0=10}, asks={432.0=2, 432.5=8, 433.0=28}}",
        orderBook.getBookAsString());

    orderBook.ask(party,420, 3);
    System.out.println("-->{bids={430.1=7, 430.0=10}, asks={432.0=2, 432.5=8, 433.0=28}}");

    Assert.assertEquals("{bids={430.1=7, 430.0=10}, asks={432.0=2, 432.5=8, 433.0=28}}",
        orderBook.getBookAsString());

    orderBook.bid(party,500, 100);
    System.out.println("-->{bids={500.0=62, 430.1=7, 430.0=10}, asks={}}");

    Assert.assertEquals("{bids={500.0=62, 430.1=7, 430.0=10}, asks={}}", orderBook.getBookAsString());
  }

}
