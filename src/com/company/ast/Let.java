package com.company.ast;

import com.company.interpret.Binder;
import com.company.interpret.Environment;
import com.company.value.Value;

import java.util.List;

public class Let extends Node
{
    public List<Define> binds;
    public Begin body;

    public Let(List<Define> binds,Begin body,int start, int end, int line, int col)
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
        //part 1：创建一个新环境，将bind存入这个新环境
        Environment env1=new Environment(env);
        for (Define bind : binds)
        {
            //part 1.1：先用env来求值
            Value val=bind.value.interp(env);
            //part 1.2：再用env1来assign
            Binder.assignLocal(bind.var,val,env1);
        }

        //part 2：用这个新环境来执行begin
        return body.interp(env1);
    }


}
