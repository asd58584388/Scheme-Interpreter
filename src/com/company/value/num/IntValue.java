package com.company.value.num;

import com.company.value.Value;

public class IntValue extends Value
{
    public int value;

    public IntValue(int val)
    {
        this.value = val;
    }

    @Override
    public String toString()
    {
        return Integer.toString(value);
    }
}
