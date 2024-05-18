package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.BoolValue;
import com.company.value.LiteralValue;
import com.company.value.Value;
import com.company.value.num.DoubleValue;
import com.company.value.num.IntValue;

import java.util.List;

public class Eq extends PrimitiveFun
{
    public Eq()
    {
        super("eq?", 2);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        Value arg1 = args.get(0);
        Value arg2 = args.get(1);

        if (arg1 instanceof  IntValue && arg2 instanceof  IntValue)
        {
            return new BoolValue(((IntValue) arg1).value == ((IntValue) arg2).value);
        }
        else if (arg1 instanceof  DoubleValue && arg2 instanceof DoubleValue)
        {
            return new BoolValue(((DoubleValue) arg1).value == ((DoubleValue) arg2).value);
        }
        else if (arg1 instanceof LiteralValue && arg2 instanceof  LiteralValue)
        {
            return new BoolValue(((LiteralValue) arg1).value.equals(((LiteralValue) arg2).value));
        }
        else
        {
            Util.abort("类型不匹配");
            return  null;
        }



    }
}
