package com.company.lexer;

import com.company.ast.Node;
import com.company.interpret.Environment;
import com.company.value.Type.Type;
import com.company.value.Value;
import com.company.value.num.DoubleValue;
import com.company.value.num.IntValue;

public class Number extends Node
{
    public String value;

    public Number(String  value,int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.value=value;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        if (value.contains("."))
        {
            return Type.DOUBLE;
        }
        else
        {
            return Type.INT ;
        }
    }

    @Override
    public Value interp(Environment env)
    {
        if (value.contains("."))
        {
            return new DoubleValue(Double.parseDouble(value));
        }
        else
        {
            return new IntValue(Integer.parseInt(value)) ;
        }
    }

    @Override
    public String toString()
    {
        return value;
    }
}
