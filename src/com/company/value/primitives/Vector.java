package com.company.value.primitives;

import com.company.ast.Node;
import com.company.util.Util;
import com.company.value.PairValue;
import com.company.value.Value;
import com.company.value.VectorValue;

import java.util.ArrayList;
import java.util.List;

public class Vector extends PrimitiveFun
{

    public Vector()
    {
        super("vector", 1);
    }

    @Override
    public Value apply(List<Value> args, Node location)
    {
        if (args==null)
        {
            Util.abort(location,"参数数量错误");
            return null;
        }

        return new VectorValue(args);
    }
}
