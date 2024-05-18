package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.BoolValue;
import com.company.value.Value;
import com.company.value.num.IntValue;

import java.util.List;

public class And extends PrimitiveFun
{
    public And()
    {
        super("and",2);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        Value arg1=args.get(0);
        Value arg2=args.get(1);

        if (!(arg1 instanceof BoolValue) || !(arg2 instanceof BoolValue))
        {
            Util.abort("and表达式的参数必须为Bool类型");
            return null;
        }
        else
        {
            return new BoolValue(((BoolValue)arg1).value && ((BoolValue)arg2).value);
        }
    }
}
