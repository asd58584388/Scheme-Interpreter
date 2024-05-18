package com.company.ast;

import com.company.interpret.Environment;
import com.company.util.Util;
import com.company.value.Type.BoolType;
import com.company.value.Type.Type;
import com.company.value.Type.UnionType;
import com.company.value.Value;

import java.util.List;

public class When extends Node
{
    public Node boolExpr;
    public Node consequent;

    public When(Node boolExpr, Node consequent,int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.boolExpr = boolExpr;
        this.consequent = consequent;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        Value tv = boolExpr.typeCheck(env);
        if (!(tv instanceof BoolType)) {
            Util.abort(boolExpr, "judge is not boolean: " + tv);
            return null;
        }

        Value type1 = consequent.typeCheck(env);
        return UnionType.union(type1, Type.VOID);
    }

    @Override
    public Value interp(Environment env)
    {
        Value bool=boolExpr.interp(env);
        if (bool.equals(Value.TRUE))
        {
            return consequent.interp(env);
        }
        else
        {
            return null;
        }
    }
}
