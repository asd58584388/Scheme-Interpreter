package com.company.value;

public class LiteralValue extends Value
{
    public String value;

    public LiteralValue(String value) {
        this.value = value;
    }

    public String toString() {
        return  value ;
    }
}
