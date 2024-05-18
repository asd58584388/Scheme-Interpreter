package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.BoolValue;
import com.company.value.Value;
import com.company.value.num.IntValue;

import java.util.List;

public class Zero extends PrimitiveFun
{
    public Zero()
    {
        super("zero?",1);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        Value arg1=args.get(0);

        if (arg1 instanceof IntValue)
        {
            return new BoolValue(((IntValue) arg1).value==0);
        }
        else
        {
            Util.abort("参数类型错误 "+arg1.toString());
            return null;
        }
    }
}
