package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.PairValue;
import com.company.value.Value;

import java.util.List;

public class Cons extends PrimitiveFun
{

    public Cons()
    {
        super("cons", 2);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        if (args==null || args.size()!=2)
        {
            Util.abort("参数必须为2个");
            return null;
        }
        else
        {

            Value arg2=args.get(1);
            //第二个参数如果是list，则消去一层括号
            if (arg2 instanceof PairValue)
            {
                args.remove(1);

//                args.addAll(((PairValue) arg2).vals);
                args.add(arg2);
            }

            return new PairValue(args);

        }
    }
}
