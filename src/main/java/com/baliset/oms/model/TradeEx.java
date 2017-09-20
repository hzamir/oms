package com.baliset.oms.model;

import com.baliset.oms.util.*;

import java.util.concurrent.atomic.*;

// a single execution (matching quantities from two orders)
public class TradeEx
{
  private static AtomicLong at = new AtomicLong(0);
  private long    sequence;
  private long    timestamp;
  private boolean bidAgressor;
  private double  price;
  private int     quantity;
  private Order   bid;
  private Order   ask;


  // given two matching orders, create an execution
  TradeEx(boolean bidAgressor, int execprice, Order bid, Order ask)
  {
    sequence = at.incrementAndGet();
    timestamp = System.currentTimeMillis();
    this.bid = bid;
    this.bidAgressor = bidAgressor;
    this.ask = ask;
    price = NumConverter.itod(execprice);
    quantity = Math.min(bid.getUnfilled(), ask.getUnfilled());  // the smallest of the two values
  }

  public long   getSequence()  { return sequence;  }
  public long   getTimestamp() { return timestamp; }
  public int    getQuantity()  { return quantity;  }
  public Order  getBid()       { return bid;       }
  public Order  getAsk()       { return ask;       }
  public double getPrice()     { return price;     }
  public String getSymbol()    { return bid.getSymbol(); }

  @Override public String toString()
  {
    if(bidAgressor)
      return String.format("                                      T%04d: %s(B%04d)->%s(A%04d) buys %d shares at %f ts=%d",
          sequence,
          bid.getParty().toString(), bid.getSequence(),
          ask.getParty().toString(), ask.getSequence(),
          quantity, price, timestamp);
    else
      return String.format("                                      T%04d: %s(A%04d)->%s(B%04d) sells %d shares at %f ts=%d",
          sequence,
          ask.getParty().toString(), ask.getSequence(),
          bid.getParty().toString(), bid.getSequence(),
          quantity, price, timestamp);
  }
}
