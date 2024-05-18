package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.PairValue;
import com.company.value.Value;

import java.util.ArrayList;
import java.util.List;

public class ListFun extends PrimitiveFun
{
    public ListFun()
    {
        super("list", 1);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        if (args==null)
        {
            Util.abort(location,"参数数量错误");
            return null;
        }

        if (args.size()==0)
        {
            List<Value> valList=new ArrayList<>();
            return new PairValue(valList);
        }

        return makeList(args);
    }

    public static Value makeList(List<Value> args)
    {
        //结束条件
        if (args.size()==1)
        {
            List<Value> pairVal=new ArrayList<>();
            pairVal.add(args.get(0));
            return new PairValue(pairVal);
        }

        List<Value> pairVal=new ArrayList<>();
        pairVal.add(args.get(0));
        pairVal.add(makeList((args.subList(1,args.size()))));

        return new PairValue(pairVal);
    }
}
