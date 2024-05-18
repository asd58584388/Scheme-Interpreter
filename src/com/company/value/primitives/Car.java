package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.PairValue;
import com.company.value.Value;

import java.util.List;

public class Car extends PrimitiveFun
{
    public Car()
    {
        super("car", 1);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        if (args==null || args.size()!=1 || !(args.get(0) instanceof PairValue))
        {
            Util.abort(location,"参数格式错误");
            return  null;
        }
        else
        {
            PairValue listValue= (PairValue) args.get(0);

            if (listValue.vals ==null || listValue.vals.size()==0)
            {
                Util.abort(location,"参数格式错误");
                return null;
            }

            return listValue.vals.get(0);
        }
    }
}
