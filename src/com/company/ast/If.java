package com.company.ast;

import com.company.util.Util;
import com.company.interpret.Environment;
import com.company.value.BoolValue;
import com.company.value.Type.BoolType;
import com.company.value.Type.UnionType;
import com.company.value.Value;

public class If extends Node
{
    Node judge;
    Node t;
    Node f;

    public If(Node judge,Node t,Node f,int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.judge=judge;
        this.t=t;
        this.f=f;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        Value tv = judge.typeCheck(env);
        if (!(tv instanceof BoolType)) {
            Util.abort(judge, "judge is not boolean: " + tv);
            return null;
        }

        Value type1 = t.typeCheck(env);
        Value type2 = f.typeCheck(env);
        return UnionType.union(type1, type2);
    }

    @Override
    public Value interp(Environment env)
    {
        Value test = judge.interp(env);

        if (test instanceof BoolValue)
        {
            // true
            if (((BoolValue) test).value)
            {
                return t.interp(env);
            }
            //false
            else
            {
                return f.interp(env);
            }
        }
        else
        {
            Util.abort("得到错误的judge类型： "+test.getClass());
            return null;
        }
    }
}
