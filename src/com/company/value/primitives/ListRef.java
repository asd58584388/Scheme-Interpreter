package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.PairValue;
import com.company.value.Value;
import com.company.value.num.IntValue;

import java.util.List;

public class ListRef extends PrimitiveFun
{
    public ListRef()
    {
        super("list-ref", 2);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        //错误处理
        if (args==null||args.size()!=2)
        {
            Util.abort(location,"参数数量错误");
            return null;
        }
        else
        {
            if (!(args.get(0) instanceof PairValue))
            {
                Util.abort(location,"第一个参数必须是pair");
                return null;
            }
            else if (!(args.get(1) instanceof IntValue) || ((IntValue) args.get(1)).value<0)
            {
                Util.abort(location,"第二个参数必须是int且大于0");
                return null;
            }

        }

        int index=((IntValue) args.get(1)).value;
        if(index==0)
        {
            return ((PairValue) args.get(0)).vals.get(((IntValue) args.get(1)).value);
        }
        else
        {
            return getRef((PairValue) args.get(0),index);
        }

    }

    public Value getRef(PairValue pairValue,int index)
    {
        if(index ==0 )
        {
            return pairValue.vals.get(0);
        }
        else if (index==1)
        {
            if (pairValue.vals.get(1) instanceof  PairValue)
            {
                return getRef((PairValue) pairValue.vals.get(1),0);
            }
            else
            {
                return pairValue.vals.get(1);
            }
        }
        else
        {
            if (pairValue.vals.get(1) instanceof  PairValue)
            {
                return getRef((PairValue) pairValue.vals.get(1),index-1);
            }
            else
            {
                Util.abort("超出索引");
                return null;
            }
        }
    }

}
