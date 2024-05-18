package com.company.value.primitives;

import com.company.ast.Node;
import com.company.value.BoolValue;
import com.company.value.Value;
import com.company.value.num.IntValue;

import java.util.List;

public class Gt extends PrimitiveFun
{
    public Gt()
    {
        super(">",2);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        Value arg1=args.get(0);
        Value arg2=args.get(1);


        return new BoolValue(((IntValue)arg1).value>((IntValue)arg2).value);
    }
}
