package com.baliset.oms.util;

import java.text.*;

public final class NumConverter
{
  private DecimalFormat df;

  public NumConverter()
  {
    df = new DecimalFormat("#0.00");
    df.setGroupingUsed(true);
    df.setGroupingSize(3);
  }

  public static final int dtoi(double v)        { return (int)(v * 100.0); }
  public static final double itod(int i)        { return (double)i * 0.01; }
  public        final String format(double v)   { return df.format(v);     }
}
