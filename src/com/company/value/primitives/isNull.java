package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.BoolValue;
import com.company.value.PairValue;
import com.company.value.Value;

import java.util.List;

public class isNull extends PrimitiveFun
{
    public isNull()
    {
        super("null?", 1);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        //part 1：参数是否正确
        if (args == null || args.size() != argNum)
        {
            Util.abort(location, "参数错误");
            return null;
        }

        //part 2：匹配null
        if (args.get(0) == null)
        {
            return new BoolValue(true);
        }
        else if (args.get(0) instanceof PairValue)
        {
            if (((PairValue) args.get(0)).vals == null || ((PairValue) args.get(0)).vals.size() == 0)
            {
                return new BoolValue(true);
            }
        }

        //part 3：如果没有找到匹配，说明是false
        return new BoolValue(false);
    }
}
