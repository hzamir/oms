package com.baliset.oms.model;

import com.baliset.oms.util.*;

import java.util.*;

public class AgOrder implements IOrder
{
  private double limitPrice;
  private int quantity;
  private final static List<TradeEx> sNoExecutions = Collections.emptyList();

  private List<Order> orders;


  // build agorder from scratch
  AgOrder(Order order)
  {
    limitPrice = order.getPrice();
    quantity = order.getUnfilled();
    orders = new ArrayList<>();
    orders.add(order);
  }

  // add another order
  AgOrder addOrder(Order order)
  {
    quantity += order.getUnfilled();
    orders.add(order);
    return this;
  }


  // if greater than zero there is a match, and returned value is the execution price
  private int executionPrice(boolean bid, Order order)
  {
    int op = NumConverter.dtoi(order.getPrice());
    int cp = NumConverter.dtoi(this.getPrice());

    if(bid) return cp <= op? cp:0;
    else    return cp >= op? cp:0;
  }


  private TradeEx createTrade(boolean bid, int execPrice, Order order)
  {
      Order counterOrder = orders.get(0);
      TradeEx ex =  bid?
          new TradeEx(true,  execPrice, order,         counterOrder):
          new TradeEx(false, execPrice, counterOrder,  order);

      int filled = ex.getQuantity();
      order.fill(filled);

      // remove counterOrder if entirely filled
      if(counterOrder.getUnfilled() == 0)
        orders.remove(0);
      this.quantity -= filled;     // reduce size of agregate order, possibly to zero
                                   // it will be removed by the orderbook if needed

      return ex;
  }


  // given an opposing order on the other side, match it with executions;
  List<TradeEx> match(boolean bid, Order order)
  {
      int execPrice = executionPrice(bid, order);
      if(execPrice <= 0)
        return sNoExecutions;

      List<TradeEx> result = new ArrayList<>(orders.size());

      while(quantity > 0 && order.getUnfilled() > 0) {
         TradeEx ex = createTrade(bid, execPrice, order);
         result.add(ex);
      }

      return result;
  }


  // this creates a new AgOrder like old one, but with additionally queued order
  // it has already been established that the price level is the same
  public AgOrder(AgOrder agOrder, Order order)
  {
    limitPrice = agOrder.getPrice();
    quantity  = agOrder.getUnfilled() + order.getUnfilled();
    orders    = agOrder.orders;
    orders.add(order);
  }



  @Override public double getPrice() { return limitPrice; }
  @Override public int getUnfilled() { return quantity;   }


}
