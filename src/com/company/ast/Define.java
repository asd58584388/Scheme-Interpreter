package com.company.ast;


import com.company.interpret.Binder;
import com.company.interpret.Environment;
import com.company.lexer.Identifier;
import com.company.value.Type.Type;
import com.company.value.Value;

public class Define extends Node
{
    Identifier var;
    Node value;

    public Define(Identifier var,Node value,int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.var = var;
        this.value = value;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        Value v=value.typeCheck(env);
        Binder.assign(var,v,env);
        return Type.VOID;
    }

    /**
     * 解释define语句
     * 产生副作用，返回void类型的值
     *
     * 暂不支持语法糖直接定义带参函数
     * @param env
     * @return Value
     */
    @Override
    public Value interp(Environment env)
    {
        Value val=value.interp(env);
        Binder.define(var,val,env);
        return Value.VOID;
    }


}
