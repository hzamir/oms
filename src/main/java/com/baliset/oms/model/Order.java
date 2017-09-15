package com.baliset.oms.model;

public class Order implements IOrder
{
  Party party;
  private double limitPrice;
  private int quantity;

  Order(Party p, double limitPrice, int quantity)
  {
    party = p;
    this.limitPrice = limitPrice;
    this.quantity = quantity;
  }

  public double getPrice() { return limitPrice;}
  public int getQuantity() { return quantity;  }
}
