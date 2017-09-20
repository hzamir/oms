package com.baliset.oms.model;

import com.baliset.oms.util.*;

import java.util.concurrent.atomic.*;

public class Order implements IOrder
{
  private static AtomicLong at = new AtomicLong(0);

  private long   sequence;        // unique identifier for order
  private String symbol;          // order for what?
  private boolean bid;            //
  private Party  party;           // recognized party who makes the order
  private double price;           // this is the limitprice
  private int    size;            // size of the original order
  private int    unfilled;        // remaining quantity of order to be filled (see tradeexecutions to find out who filled how much when)
  private int    filled;          // how much of order has been filled


  Order(boolean isBid, Party p, String symbol, double limitPrice, int quantity)
  {
    if(quantity <= 0)
      throw new InvalidSize();
    if(NumConverter.dtoi(limitPrice) <= 0)
      throw new InvalidPrice();


    sequence = at.incrementAndGet();
    this.symbol = symbol;
    bid = isBid;
    party = p;
    filled = 0;
    this.price = limitPrice;
    this.unfilled = this.size = quantity;
  }

  public double  getPrice()    { return price;     }
  public int     getSize()     { return size;      }  // original size of order
  public int     getUnfilled() { return unfilled;  }  // remaining quantity available to be filled
  public int     getFilled()   { return filled;    }  // already filled size
  public long    getSequence() { return sequence;  }
  public Party   getParty()    { return party;     }
  public boolean isBid()       { return bid;       }
  public boolean isAsk()       { return !bid;      }
  public String  getSymbol()   { return symbol;    }

  // return remainder of after filling part of order
  // if it is positive, then the order remains in place
  // if it is negative, the remainder must be filled with next order if it exists
  int fill(int quantity)
  {
    filled += quantity;
    this.unfilled -= quantity;  // it could go negative, that is ok, the caller is responsible for order lifecycle
    return this.unfilled;
  }


  public class InvalidSize  extends RuntimeException {}
  public class InvalidPrice extends RuntimeException {}
}
