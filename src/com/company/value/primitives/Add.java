package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.Value;
import com.company.value.num.DoubleValue;
import com.company.value.num.IntValue;
import com.sun.source.doctree.UnknownInlineTagTree;

import java.util.List;

public class Add extends PrimitiveFun
{
    public Add()
    {
        super("+", 2);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {

        if (args.size()<2)
        {
            Util.abort(location,"+的参数必须大于或等于2个");
            return null;
        }
        else
        {
            double res1=0;
            int res=0;
            boolean hasDouble=false;
            for (Value arg : args)
            {
                if (arg instanceof DoubleValue )
                {
                    hasDouble=true;
                    res1 +=((DoubleValue) arg).value;
                }
                else if (arg instanceof IntValue)
                {
                    res+=((IntValue) arg).value;
                }
                else
                {
                    Util.abort("类型错误: "+arg.toString());
                }
            }

            if (!hasDouble)
            {

                return new IntValue(res);
            }
            else
            {
                res1+=res;
                return new DoubleValue(res1);
            }


        }
    }
}
