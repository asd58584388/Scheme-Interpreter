package com.company.ast;

import com.company.interpret.Binder;
import com.company.interpret.Closure;
import com.company.interpret.Environment;
import com.company.lexer.Identifier;
import com.company.lexer.Name;
import com.company.value.Value;

import java.util.List;

public class Lambda extends Node
{
    public List<Name> parameters;
    public Node body;
    public Identifier hasList;


    public Lambda(List<Name> parameters, Node body, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.parameters = parameters;
        this.body = body;
    }

    public Lambda(List<Name> parameters, Node body, Identifier hasList, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.parameters = parameters;
        this.body = body;
        this.hasList = hasList;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        Environment env1=new Environment(env);

        for (Name parameter : parameters)
        {
            Binder.assign(parameter,parameter.typeCheck(env),env1);
        }

        return body.typeCheck(env1);

    }

    /**
     * 返回闭包过程
     *
     * @param env
     * @return Value
     */
    @Override
    public Value interp(Environment env)
    {
//        //如果没有参数，直接执行（会递归执行下去，不能这样写）
//        if (parameters==null ||parameters.size()==0)
//        {
//            return body.interp(env);
//        }
//        else
//        {
        return new Closure(this, env);
    }
}
