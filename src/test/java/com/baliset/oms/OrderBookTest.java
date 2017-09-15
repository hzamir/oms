package com.baliset.oms;

import com.baliset.oms.model.*;
import org.junit.Assert;
import org.junit.Test;

public class OrderBookTest {

  static String[] expected = {
      "{bids={10@430.10, 10@430.00}, asks={}}",
      "{bids={10@430.10, 10@430.00}, asks={2@432.00, 8@432.50, 28@433.00}}",
      "{bids={7@430.10, 10@430.00}, asks={2@432.00, 8@432.50, 28@433.00}}",
      "{bids={62@500.00, 7@430.10, 10@430.00}, asks={}}"
  };


  private void check(OrderBook ob, int i)
  {
    String actual = ob.getBookAsString();
    System.out.println("expected: " +expected[i]);
    System.out.println("  actual: " + actual);

    Assert.assertEquals(expected[i], actual);
  }

  @Test
  public void testBasicOrderBook() {
    OrderBook orderBook = new OrderBook("foo");
    Party party = new Party("testParty");

    orderBook.bid(party,430, 5);
    orderBook.bid(party, 430, 5);
    orderBook.bid(party, 430.10, 10);
    check(orderBook, 0);

    orderBook.ask(party,432, 2);
    orderBook.ask(party, 433, 25);
    orderBook.ask(party, 432.5, 8);
    orderBook.ask(party,433, 3);
    check(orderBook, 1);

    orderBook.ask(party,420, 3);
    check(orderBook, 2);

    orderBook.bid(party,500, 100);
    check(orderBook, 3);
  }

}
