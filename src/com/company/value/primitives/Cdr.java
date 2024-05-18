package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.PairValue;
import com.company.value.Value;

import java.util.ArrayList;
import java.util.List;

public class Cdr extends PrimitiveFun
{
    public Cdr()
    {
        super("cdr", 1);
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
            PairValue pairValue= (PairValue) args.get(0);

            if (pairValue.vals ==null || pairValue.vals.size()==0)
            {
                Util.abort(location,"参数格式错误");
                return null;
            }
            //如果只有一个参数，返回一个空pair,或者第二个参数为空
            else if (pairValue.vals.size()==1 || pairValue.vals.get(1)==null)
            {
                List<Value> valList=new ArrayList<>();
                return new PairValue(valList);
            }
            else
            {
                List<Value> resVals=pairValue.vals;
                return resVals.get(1);

            }
        }
    }
}
