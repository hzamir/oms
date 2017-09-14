package com.baliset.oms.model;

import java.text.*;

public class Order
{
  Party party;
  double limitPrice;
  int quantity;

  static DecimalFormat df = new DecimalFormat("#0.0#");

  Order(Party party, double limitPrice, int quantity)
  {
    this.party = party;
    this.limitPrice = limitPrice;
    this.quantity = quantity;
  }

  // utility for output
  String priceAndQuantity()
  {
    return df.format(limitPrice)+"="+quantity;
  }

}
