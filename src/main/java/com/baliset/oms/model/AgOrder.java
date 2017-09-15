package com.baliset.oms.model;

public class AgOrder implements IOrder
{
  double limitPrice;
  int quantity;

  AgOrder(double limitPrice, int quantity)
  {
    this.limitPrice = limitPrice;
    this.quantity = quantity;
  }


  @Override public double getPrice() { return limitPrice; }
  @Override public int getQuantity() { return quantity;   }
}
