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

  private void bidOrAsk(SortedMap<Integer, AgOrder> sameSide, SortedMap<Integer, AgOrder> otherSide, double limitPrice, int quantity)
  {

    if (quantity <= 0) // no guidance on handling negative quantities
      return;

    int     unmatchedQuantity = quantity;
    boolean moreMatching;

    // match bids or asks against best prices on opposing side
    do {
      moreMatching = false;
      if (!otherSide.isEmpty()) {
        Integer counterpartKey = otherSide.firstKey();
        AgOrder   counterpart    = otherSide.get(counterpartKey);


        if (sameSide == bids ? counterpart.limitPrice <= limitPrice : counterpart.limitPrice >= limitPrice) {
          if (counterpart.quantity > unmatchedQuantity)  // put unmatched balance back
          {
            otherSide.put(counterpartKey, new AgOrder(counterpart.limitPrice,
                counterpart.quantity - unmatchedQuantity));
            unmatchedQuantity = 0;
          } else {
            otherSide.remove(counterpartKey);
            unmatchedQuantity -= counterpart.quantity;
          }
          if (unmatchedQuantity > 0)
            moreMatching = true;
        }
      }
    } while (moreMatching);

    // any unmatched quantities need to be added to orders
    // in the real world, we might prefer that they exist even briefly as orders prior to matching
    // or otherwise be recorded as a match
    // but for the purposes of this test, only remaining unmatched orders are needed per spec
    if (unmatchedQuantity > 0) {
      Integer limitKey   = keyFor(limitPrice);
      AgOrder   toCoalesce = sameSide.get(limitKey);

      if (toCoalesce != null) {
        unmatchedQuantity += toCoalesce.quantity;
      }
      // add or overwrite the order for the same price with correct quantity
      sameSide.put(limitKey, new AgOrder(limitPrice, unmatchedQuantity));
    }

  }

  public void bid(Party party, double limitPrice, int quantity)
  {
    // todo because of where I am plugging in this log statement, am creating an order for no other reason than to print
    Order fixme = new Order(party, limitPrice, quantity);

    // to store the original order, so it can then be partially or fully fulfilled with an execution if/when it crosses

    logger.info(String.format("%s bids for %s %s",party, symbol, priceAndQuantity(fixme)));

    bidOrAsk(bids, asks, limitPrice, quantity);
  }

  private String priceAndQuantity(IOrder o)
  {
    return o.getQuantity() + "@" + nc.format(o.getPrice());
  }

  public void ask(Party party, double limitPrice, int quantity)
  {
    Order fixme = new Order(party, limitPrice, quantity);

    logger.info(String.format("%s asks for %s %s", party, symbol, priceAndQuantity(fixme)));

    bidOrAsk(asks, bids, limitPrice, quantity);
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
