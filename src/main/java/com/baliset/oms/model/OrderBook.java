package com.baliset.oms.model;

import com.baliset.oms.util.*;
import org.slf4j.*;

import java.util.*;

public class OrderBook
{
  private static Logger logger = LoggerFactory.getLogger(OrderBook.class);


  private Comparator<Integer> ascendingOrder  = (o1, o2) -> o1 - o2;
  private Comparator<Integer> descendingOrder = (o1, o2) -> o2 - o1;

  // Use Integer keys, because Double keys just don't work well for this
  private final SortedMap<Integer, AgOrder> bids = new TreeMap<>(descendingOrder);
  private final SortedMap<Integer, AgOrder> asks = new TreeMap<>(ascendingOrder);

  private final String symbol;
  private final NumConverter nc = new NumConverter();

  public OrderBook(String symbol)
  {
    this.symbol = symbol;
  }


  // if greater than zero there is a match, and returned value is the execution price
  private int executionPrice(boolean bid, Order order, AgOrder counterpart)
  {
    int op = NumConverter.dtoi(order.getPrice());
    int cp = NumConverter.dtoi(counterpart.getPrice());

    if(bid) return cp <= op? cp:0;
    else    return cp >= op? cp:0;
  }


  private List<TradeEx> bidOrAsk(SortedMap<Integer, AgOrder> sameSide, SortedMap<Integer, AgOrder> otherSide, Order order)
  {
    logger.info(String.format("%04d: %s %s for %s %s",
        order.getSequence(),
        order.getParty(),
        (order.isBid()? "bids": "asks"),
        symbol,
        priceAndQuantity(order)));

    boolean bid = sameSide == bids;

    List<TradeEx> result = null;
    // match bids or asks against best prices on opposing side
    while(true) {

      if(otherSide.isEmpty())
        break;

      Integer counterpartKey   = otherSide.firstKey();
      AgOrder counterpart      = otherSide.get(counterpartKey);
      List<TradeEx> tradesPerAgOrder = counterpart.match(bid, order);

      if (counterpart.getUnfilled() == 0)
        otherSide.remove(counterpartKey);   // remove exhausted AgOrder

      if (tradesPerAgOrder.isEmpty())       // we are done matching
        break;

      // add to total list of executions
      if(result == null)
        result = tradesPerAgOrder;
      else
        result.addAll(tradesPerAgOrder);
    };
    
    //--- add any unmatched part of order to sameside either a new or existing agorder
    int     unmatchedQuantity = order.getUnfilled();
    if (unmatchedQuantity > 0) {
      Integer limitKey     = keyFor(order.getPrice());
      AgOrder   toCoalesce = sameSide.get(limitKey);

      if (toCoalesce != null) {
        toCoalesce.addOrder(order);
      }  else {
        sameSide.put(limitKey, new AgOrder(order));   // an entirely new order
      }
    }

    return result;
  }

  private String priceAndQuantity(IOrder o)
  {
    return o.getUnfilled() + "@" + nc.format(o.getPrice());
  }

  public List<TradeEx> bid(Party party, double limitPrice, int quantity)
  {
    Order order = new Order(true, party, limitPrice, quantity);


    return bidOrAsk(bids, asks, order);
  }


  public List<TradeEx>  ask(Party party, double limitPrice, int quantity)
  {
    Order order = new Order(false, party, limitPrice, quantity);

    return bidOrAsk(asks, bids, order);
  }


  static private Integer keyFor(double v) { return NumConverter.dtoi(v);          }

  public Quote topOfBook()
  {
    Integer bestBid = bids.firstKey();
    Integer bestAsk = asks.firstKey();

    // todo: yes, but how many at that price? And instrument is the wrong term is should be BidAsk or something.
    return new Quote(symbol, NumConverter.itod(bestBid != null? bestBid: 0), NumConverter.itod(bestAsk != null? bestAsk: Integer.MAX_VALUE));
  }

  private void appendOrders(StringBuilder sb, SortedMap<Integer, AgOrder> orders)
  {
    boolean prefixCommas = false;
    sb.append("{");
    for (AgOrder o : orders.values()) {
      if (prefixCommas)
        sb.append(", ");
      else
        prefixCommas = true;
      sb.append(priceAndQuantity(o));
    }
    sb.append("}");
  }

  //"{bids={500.0=62, 430.1=7, 430.0=10}, asks={}}"
  public String getBookAsString()
  {
    StringBuilder sb = new StringBuilder(10 * (asks.size() + bids.size()));
    sb.append("{bids=");
    appendOrders(sb, bids);
    sb.append(", asks=");
    appendOrders(sb, asks);
    sb.append("}");
    return sb.toString();
  }
}
