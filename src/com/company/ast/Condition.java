package com.company.ast;

import com.company.interpret.Environment;
import com.company.lexer.Keyword;
import com.company.util.Util;
import com.company.value.BoolValue;
import com.company.value.Constants;
import com.company.value.Type.BoolType;
import com.company.value.Type.UnionType;
import com.company.value.Value;

import java.util.List;

public class Condition extends Node
{
    List<When> conditions;
    boolean hasElse;
    Node Else;

    public Condition(List<When> conditions,boolean hasElse,Node Else, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.conditions = conditions;
        this.hasElse=hasElse;
        this.Else=Else;
    }

    @Override
    public Value typeCheck(Environment env)
    {

        UnionType unionType=new UnionType();

        for (When condition : conditions)
        {
            Value v=condition.boolExpr.typeCheck(env);
            if (!(v instanceof BoolType))
            {
                Util.abort("cond表达式中的每个case的test必须是bool表达式");
                return null;
            }

            unionType.add(condition.consequent.typeCheck(env));

        }

        if (hasElse)
        {
            unionType.add(Else.typeCheck(env));
        }

        return unionType;
    }

    @Override
    public Value interp(Environment env)
    {
        //part 1：匹配condition
        for (When condition : conditions)
        {
            Node boolExpr = condition.boolExpr;
            Node consequent = condition.consequent;

            Value boolVal=boolExpr.interp(env);

            if (boolVal instanceof BoolValue)
            {
                if (((BoolValue) boolVal).value)
                {
                    return consequent.interp(env);
                }
            }
            else
            {
                Util.abort("cond表达式的每个clause的第一个参数必须返回bool值");
            }
        }

        //part 2：如果有else，匹配else
        if (hasElse)
        {
            return Else.interp(env);
        }

        //part 3：未找到匹配的condition
        return Value.VOID;
    }

    public boolean isElse(Node node)
    {
        if (node instanceof Keyword)
        {
            return ((Keyword) node).name.equals(Constants.ELSE_KEYWORD);
        }
        else
        {
            return false;
        }
    }
}
