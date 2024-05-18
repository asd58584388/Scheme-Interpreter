package com.company.value;

import com.company.lexer.Str;

import java.util.ArrayList;
import java.util.List;

public class PairValue extends Value
{
    public List<Value> vals;

    public String hasQuote = null;

    public PairValue(List<Value> vals)
    {
        this.vals = vals;
    }

    public PairValue(List<Value> values, String hasQuote)
    {

        this.vals = values;
        this.hasQuote = hasQuote;
    }


    @Override
    public String toString()
    {

        StringBuilder sb = new StringBuilder();


        //空pair
        if (vals == null || vals.size() == 0)
        {
            return "()";
        }
        //一个参数的pair(不可能有带quote的，又只有一个参数的pair）
        else if (vals.size() == 1)
        {
            sb.append("(");
            sb.append(vals.get(0).toString());
            sb.append(")");
        }
        //如果hasQuote != null，那么第一个参数肯定是quote或者quasiquote
        else if (hasQuote != null)
        {
            sb.append(hasQuote);
            sb.append(vals.get(1).toString());
        }
        //其它
        //(如果是连着的list，后面的list不需要括号）
        else
        {
            sb.append("(");

            sb.append(vals.get(0).toString());
            sb.append(" ");

            for (int i = 1; i < vals.size(); i++)
            {
                if (vals.get(i) instanceof PairValue)
                {
                    sb.append(((PairValue) vals.get(i)).toStringNonParen());
                }
                else
                {
                    sb.append(vals.get(i).toString());
                }

                if (i != vals.size() - 1)
                {
                    sb.append(" ");
                }
            }

            sb.append(")");


        }


        return sb.toString();
    }

    /**
     * toString如果存在第二个参数，则第二个参数不需要括号，quote之后的参数也不需要括号
     *
     * @return
     */
    public String toStringNonParen()
    {

        StringBuilder sb = new StringBuilder();

        //空pair
        if (vals == null || vals.size() == 0)
        {
            return "";
        }
        //一个参数的pair
        else if (vals.size() == 1)
        {
            sb.append(vals.get(0).toString());
        }
        //如果hasQuote != null，那么第一个参数肯定是quote或者quasiquote
        else if (hasQuote != null)
        {
            sb.append(hasQuote);
            //quote之后的参数不需要括号
            sb.append(((PairValue) vals.get(1)).toStringNonParen());
        }
        else
        {
            sb.append(vals.get(0).toString());
            sb.append(" ");
            for (int i = 1; i < vals.size(); i++)
            {
                if (vals.get(i) instanceof PairValue)
                {
                    sb.append(((PairValue) vals.get(i)).toStringNonParen());
                }
                else
                {
                    sb.append(vals.get(i).toString());
                }

                if (i != vals.size() - 1)
                {
                    sb.append(" ");
                }
            }
        }

        return sb.toString();
    }


}
