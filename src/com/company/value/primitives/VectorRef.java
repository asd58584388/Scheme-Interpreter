package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.Value;
import com.company.value.VectorValue;
import com.company.value.num.IntValue;

import java.util.List;

public class VectorRef extends PrimitiveFun
{
    public VectorRef()
    {
        super("vector-ref",2);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        if (args == null || args.size()!=2)
        {
            Util.abort(location,"参数数量错误");
            return null;
        }

        Value vectorVal=args.get(0);
        Value intVal=args.get(1);

        if (!(vectorVal instanceof VectorValue))
        {
            Util.abort(location,"第一个参数必须是Vector");
            return null;
        }
        else if (!(intVal instanceof IntValue))
        {
            Util.abort(location,"第二个参数必须是Int");
            return null;
        }
        else
        {
            return ((VectorValue) vectorVal).VectorRef(((IntValue) intVal).value);
        }


    }
}
