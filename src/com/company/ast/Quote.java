package com.company.ast;

import com.company.interpret.Environment;
import com.company.lexer.Keyword;
import com.company.lexer.Name;
import com.company.lexer.Number;
import com.company.lexer.Str;
import com.company.value.Constants;
import com.company.value.LiteralValue;
import com.company.value.PairValue;
import com.company.value.Type.Type;
import com.company.value.Value;
import com.company.value.num.IntValue;

import java.util.ArrayList;
import java.util.List;

public class Quote extends Node
{
    Node argument;
    String token = null;

    public Quote(Node argument, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.argument = argument;
        //默认quote为 '
        this.token="'";
    }

    public Quote(Node argument, String token, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.argument = argument;
        this.token = token;
    }


    @Override
    public Value typeCheck(Environment env)
    {
        return Type.QUOTE;
    }

    /**
     * interpret得到的肯定是Value，name和keyword可以当成LiteralValue
     *
     * @param env
     * @return Value
     */
    @Override
    public Value interp(Environment env)
    {
        return interpSingle(argument,env);
    }

    public PairValue interpList(List<Node> nodes, Environment env)
    {

        //nodes里的每个node只有可能是 vector ，token ，quote ，tuple

        //结束条件
        if (nodes.size()==1)
        {
            List<Value> pairVal=new ArrayList<>();
            pairVal.add(interpSingle(nodes.get(0),env));
            return new PairValue(pairVal);
        }

        List<Value> pairVal=new ArrayList<>();
        pairVal.add(interpSingle(nodes.get(0),env));
        pairVal.add(interpList(nodes.subList(1,nodes.size()),env));

        return new PairValue(pairVal);



    }

    private Value interpSingle(Node element,Environment env)
    {
        if ((element instanceof Name) || (element instanceof Keyword) || (element instanceof Str))
        {
            return new LiteralValue(element.toString());
        }
        else if (element instanceof Number)
        {
            return element.interp(env);
        }
        else if (element instanceof Quote)
        {
            List<Value> values = new ArrayList<>();
            if (((Quote) element).token.equals(Constants.QUOTE_ABBR))
            {
                values.add(new LiteralValue(Constants.QUOTE_KEYWORD));
            }
            else if (((Quote) element).token.equals(Constants.QUASIQUOTE_ABBR))
            {
                values.add(new LiteralValue(Constants.QUASIQUOTE_KEYWORD));
            }

            values.add(element.interp(env));

            return new PairValue(values, ((Quote) element).token);
        }
        else if (element instanceof Tuple)
        {
            List<Node> elements = ((Tuple) element).elements;

            if (elements.size()==0)
            {
                return new PairValue(null);
            }

//            List<Value> values=new ArrayList<>();
//            values.add(interpList(elements,env));
//
//            return new PairValue(values);

            return interpList(elements,env);
        }
        else
        {
            return null;
        }
    }


//    @Override
//    public String toString()
//    {
//        //如果是(quote arg1)
//        if (token == null)
//        {
//            return argument.toString();
//        }
//        //如果是'...或者`...
//        else
//        {
//            return token + argument.toString();
//        }
//    }
}
