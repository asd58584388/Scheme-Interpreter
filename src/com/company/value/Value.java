package com.company.value;

public abstract class Value
{
    public static final Value VOID = new VoidValue();
    public static final Value TRUE = new BoolValue(true);
    public static final Value FALSE = new BoolValue(false);
}
