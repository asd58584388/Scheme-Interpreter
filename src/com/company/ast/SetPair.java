package com.company.ast;

import com.company.interpret.Binder;
import com.company.interpret.Environment;
import com.company.lexer.Name;
import com.company.util.Util;
import com.company.value.PairValue;
import com.company.value.Value;

import java.util.List;

public class SetPair extends Node
{
    Node identifier;
    Node expression;
    boolean isCar;  //不是car就是cdr

    public SetPair(Node identifier, Node expression, boolean isCar, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.identifier = identifier;
        this.expression = expression;
        this.isCar = isCar;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        Value v=expression.typeCheck(env);

        return Value.VOID;
    }

    @Override
    public Value interp(Environment env)
    {

        Value val = expression.interp(env);

        //被赋值的是pair
        Value pair = env.lookupValue(((Name) identifier).name);
        if (pair instanceof PairValue)
        {
            List<Value> pairVals = ((PairValue) pair).vals;

            if (isCar)
            {
                pairVals.set(0, val);
            }
            else
            {
                pairVals.set(1, val);
            }

//            ((PairValue) pair).vals=pairVals;
            Binder.assign(identifier,pair,env);
        }
        //被赋值的不是pair
        else
        {
            Util.abort("第一个参数必须是pair");
            return null;
        }

        return Value.VOID;
    }

}



