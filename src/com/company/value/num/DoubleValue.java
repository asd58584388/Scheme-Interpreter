package com.company.value.num;

import com.company.value.Value;

public class DoubleValue extends Value
{
    public double value;

    public DoubleValue(double value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return Double.toString(value);
    }
}
