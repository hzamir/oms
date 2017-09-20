package com.baliset.oms.util;

public class Fluctuator
{
  private RangeGenerator rg;
  private RangeGenerator overUnder;


  // this is a convenience constructor
  public Fluctuator(double min, double max)
  {
    overUnder = new RangeGenerator(0,1);
    rg = new RangeGenerator(NumConverter.dtoi(min), NumConverter.dtoi(max));   // degree of fluctuation
  }


  public double fluctuate(double v)
  {
    int i = rg.generate();

    if(overUnder.generate() > 0) {
      return v + NumConverter.itod(i);
    } else {
      return v -  NumConverter.itod(i);
    }
  }

}
