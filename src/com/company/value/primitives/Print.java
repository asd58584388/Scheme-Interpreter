package com.company.value.primitives;

import com.company.ast.Node;
import com.company.value.Value;

import java.util.List;

public class Print extends PrimitiveFun
{
    public Print()
    {
        super("print", 1);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        boolean first = true;
        for (Value v : args)
        {
            if (!first)
            {
                System.out.print(", ");
            }
            System.out.print(v);
            first = false;
        }
        System.out.println();
        return Value.VOID;
    }
}
