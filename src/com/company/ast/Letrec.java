package com.company.ast;

import com.company.interpret.Binder;
import com.company.interpret.Environment;
import com.company.value.Value;

import java.util.List;

public class Letrec extends Node
{
    public List<Define> binds;
    public Begin body;

    public Letrec(List<Define> binds,Begin body,int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.binds = binds;
        this.body = body;
    }


    @Override
    public Value typeCheck(Environment env)
    {
        Environment env1=new Environment(env);
        for (Define bind : binds)
        {
            bind.typeCheck(env1);
        }

        return body.typeCheck(env1);
    }

    @Override
    public Value interp(Environment env)
    {

        for (Define bind : binds)
        {
            Value val=bind.value.interp(env);
            Binder.assign(bind.var,val,env);
        }

        return body.interp(env);
    }

}
