package com.company.ast;

import com.company.interpret.Environment;
import com.company.lexer.Keyword;
import com.company.lexer.Name;
import com.company.value.Constants;
import com.company.value.Value;

import java.util.List;

public class Case extends Node
{
    Node key;
    List<Tuple> clauses;
    boolean hasElse;
    Begin Else;

    public Case(Node key, List<Tuple> clauses, boolean hasElse, Begin Else, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.key = key;
        this.clauses = clauses;
        this.hasElse = hasElse;
        this.Else = Else;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        return null;
    }

    @Override
    public Value interp(Environment env)
    {
        //part 1：求值key
        Value keyVal = key.interp(env);

        //part 2：匹配前面的所有clause
        for (Tuple clause : clauses)
        {
            List<Node> clauseElement = clause.elements;
            Tuple datums = (Tuple) clauseElement.get(0);
            for (Node datum : datums.elements)
            {
                if (datum.toString().equals(keyVal.toString()))
                {
                    //Begin node
                    return clauseElement.get(1).interp(env);
                }
            }
        }

        //part 3：如有else，则匹配else
        if (hasElse)
        {
            return Else.interp(env);
        }

        //part 4：未匹配成功，返回VOID
        return Value.VOID;
    }

}
