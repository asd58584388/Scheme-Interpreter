package com.company.value;

public class StringValue extends Value
{
    public String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
