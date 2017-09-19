package com.baliset.oms.service;

import com.baliset.oms.conf.*;
import com.baliset.oms.model.*;
import com.baliset.oms.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Service
public class GhostOrderService
{
  private final OrderService orderService;
  private final GhostConfig  ghostConfig;
  private final TradeService tradeService;


  private final RangeGenerator sizeGenerator;
  private final RangeGenerator partyGenerator;

  private final PriceGenerator priceGenerator;
  private final Fluctuator     fluctuator;

  private final Party[] parties;
  private final String[] partynames = { "Paul", "John", "Mary", "Rand",  "Yoko", "Robb", "Arya", "Quin", "Phil", "Mave"};


  @Autowired public GhostOrderService(OrderService orderService, PartyService partyService, GhostConfig ghostConfig, TradeService tradeService)
  {
    this.orderService = orderService;
    this.tradeService = tradeService;
    this.ghostConfig  = ghostConfig;
    priceGenerator    = new PriceGenerator(1, 500);
    sizeGenerator     = new RangeGenerator(1,100) { @Override public int generate() { return 10*super.generate(); }};
    int numParties = 10;

    assert (numParties == partynames.length);
    partyGenerator = new RangeGenerator(numParties);

    fluctuator        = new Fluctuator(0.01, 0.15);

    parties = new Party[numParties];
    IntStream.range(0, numParties).forEach(index->parties[index] = partyService.create(partynames[index]));

    ghostConfig.getSymbols().forEach(orderService::createBook);   // create some books as specified in ghost config
    ghostConfig.getSymbols().forEach(this::seedOrders);
  }

  private void bid(String symbol, Party party, int quantity, double price)
  {
    List<TradeEx> tradeExes = orderService.bid(symbol,party,quantity, price);
    if(tradeExes != null)
      tradeService.reportTrades(tradeExes);
  }

  private void ask(String symbol, Party party, int quantity, double price)
  {
    List<TradeEx> tradeExes = orderService.ask(symbol,party,quantity, price);
    if(tradeExes != null)
      tradeService.reportTrades(tradeExes);
  }


  // create some initial orders for the a given symbol, the random price chosen will be the basis
  // of future fluctuations
  private void seedOrders(String symbol)
  {
    double bid = priceGenerator.generate();
    double ask = fluctuator.fluctuate(bid);

    if(ask < bid)
    {
      double t = ask; ask  = bid; bid = t;
    }

    Party biddingParty = parties[partyGenerator.generate()];
    Party askingParty;

    do {
      askingParty = parties[partyGenerator.generate()];
    } while(askingParty == biddingParty);

    bid(symbol,biddingParty,sizeGenerator.generate(),bid);
    ask(symbol,askingParty,sizeGenerator.generate(),ask);

    IntStream.range(0, 6).forEach($ -> arbitraryOrder(symbol));

  }

  private void arbitraryOrder(String symbol)
  {
    Quote quote = orderService.top(symbol);

    double bid = quote.getBid();
    double ask = quote.getAsk();

    double nbid = fluctuator.fluctuate(bid);

    double delta = nbid - bid;
    double nask = (delta > 0)? ask + delta: ask - delta;

    Party biddingParty = parties[partyGenerator.generate()];
    Party askingParty;

    do {
      askingParty = parties[partyGenerator.generate()];
    } while(askingParty == biddingParty);


    bid(symbol,biddingParty,sizeGenerator.generate(),nbid);
    ask(symbol,askingParty,sizeGenerator.generate(),nask);
  }

  @Scheduled(cron = "${ghost.cron}")
  private void ghostOrders()
  {
    ghostConfig.getSymbols().forEach(this::arbitraryOrder);
  }

}
