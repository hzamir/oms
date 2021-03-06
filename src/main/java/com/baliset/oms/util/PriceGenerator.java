package com.baliset.oms.util;

public class PriceGenerator
{
  private RangeGenerator rg;

  // this is a convenience constructor
  public PriceGenerator(double min, double max)
  {
      rg = new RangeGenerator((int)min * 100, (int)max * 100);
  }

  public double generate()
  {
    return rg.generate() * 0.01;
  }


}
