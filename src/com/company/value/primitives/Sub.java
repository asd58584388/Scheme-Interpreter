package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.Value;
import com.company.value.num.DoubleValue;
import com.company.value.num.IntValue;

import java.util.List;

public class Sub extends PrimitiveFun
{


    public Sub()
    {
        super("-", 2);
    }


    @Override
    public Value apply(List<Value> args, Node location)
    {

        if (args.size()<2)
        {
            Util.abort(location,"-的参数必须大于或等于2个");
            return null;
        }
        else
        {
            double res1 = 0;
            int res = 0;
            boolean hasDouble = false;

            Value first = args.get(0);
            if (first instanceof DoubleValue)
            {
                res1 = ((DoubleValue) first).value;
                hasDouble = true;
            }
            else if (first instanceof IntValue)
            {
                res = ((IntValue) first).value;
            }
            else
            {
                Util.abort("类型错误");
                return null;
            }


            for (int n = 1; n < args.size(); n++)
            {
                Value arg = args.get(n);

                if (hasDouble)
                {
                    if (arg instanceof DoubleValue)
                    {
                        res1 -= ((DoubleValue) arg).value;
                    }
                    else if (arg instanceof IntValue)
                    {
                        res1 -= ((IntValue) arg).value;
                    }
                }
                else
                {
                    if (arg instanceof DoubleValue)
                    {
                        res -= ((DoubleValue) arg).value;
                        hasDouble = true;
                        res1 = res;
                    }
                    else if (arg instanceof IntValue)
                    {
                        res -= ((IntValue) arg).value;
                    }
                }

            }

            if (!hasDouble)
            {

                return new IntValue(res);
            }
            else
            {
                return new DoubleValue(res1);
            }
        }
    }
}
