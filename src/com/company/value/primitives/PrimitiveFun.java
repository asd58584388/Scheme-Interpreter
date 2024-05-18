package com.company.value.primitives;

import com.company.ast.Node;
import com.company.value.Value;

import java.util.List;

public abstract class PrimitiveFun extends Value
{
    public String name;
    public int argNum;

    public PrimitiveFun(String name, int argNum)
    {
        this.name = name;
        this.argNum = argNum;
    }

    //location是用于得到节点的位置
    public abstract Value apply(List<Value> args, Node location) ;
}
