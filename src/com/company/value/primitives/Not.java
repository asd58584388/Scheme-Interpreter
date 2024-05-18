package com.company.value.primitives;

import com.company.ast.Node;
import com.company.value.BoolValue;
import com.company.value.Value;

import java.util.List;

public class Not extends PrimitiveFun
{
    public Not()
    {
        super("not",1);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        Value arg1=args.get(0);

        return new BoolValue(!((BoolValue)arg1).value);
    }
}
