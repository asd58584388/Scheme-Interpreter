package com.company.ast;

import com.company.interpret.Binder;
import com.company.interpret.Closure;
import com.company.interpret.Environment;
import com.company.lexer.Identifier;
import com.company.lexer.Name;
import com.company.lexer.Number;
import com.company.value.Constants;
import com.company.value.Value;

public class Assign extends Node
{
    //arg1是被set的对象，arg2是set的值
    Name arg1;
    Node arg2;

    public Assign(Name arg1, Node arg2, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        Value valueValue = arg2.typeCheck(env);
        Binder.assign(arg1, valueValue, env);
        return Value.VOID;
    }

    @Override
    public Value interp(Environment env)
    {
        Value val = arg2.interp(env);
        Binder.assign(arg1, val, env);
        return Value.VOID;
    }
}
