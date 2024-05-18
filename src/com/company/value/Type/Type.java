package com.company.value.Type;

import com.company.value.Value;
import com.company.value.VoidValue;

public class Type
{
    public static final Value BOOL=new BoolType();
    public static final Value INT = new IntType();
    public static final Value DOUBLE= new DoubleType();
    public static final Value VOID = new VoidValue();
    public static final Value STRING = new StringType();
    public static final Value QUOTE=new QuoteType();
}
