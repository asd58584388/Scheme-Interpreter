package com.company.lexer;

import com.company.ast.Node;
import com.company.interpret.Environment;
import com.company.value.Constants;
import com.company.value.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//delimiter既是token同时还是node
public class Delimiter extends Node
{
    public String shape;

    //存放所有分隔符
    public static final Set<String> delims=new HashSet<>();

    //存放开闭符号，方便匹配
    public static final Map<String ,String> delimMap= new HashMap<>();

    static {
        delims.add("(");
        delims.add(")");
        delims.add("[");
        delims.add("]");
        delimMap.put("[","]");
        delimMap.put("(",")");

        delims.add(Constants.QUASIQUOTE_ABBR);
        delims.add(Constants.QUOTE_ABBR);
        delims.add(Constants.VECTOR_ABBR);
    }

    public Delimiter(String shape , int start, int end, int line, int col)
    {
        super(start,end,line,col);
        this.shape = shape;
    }

    public static Delimiter genDelimiter(String shape)
    {
        return new Delimiter(shape,0 ,0,0,0);
    }

    public static boolean isDelimiter(char ch)
    {
        return delims.contains(Character.toString(ch));
    }

    public static boolean isQuasiquote(Token delimiter)
    {
        if (delimiter instanceof Delimiter)
        {
            return ((Delimiter) delimiter).shape.equals(Constants.QUASIQUOTE_ABBR) ;
        }
        else
        {
            return false;
        }
    }

    public static boolean isQuote(Token delimiter)
    {
        if (delimiter instanceof Delimiter)
        {
            return ((Delimiter) delimiter).shape.equals(Constants.QUOTE_ABBR) ;
        }
        else
        {
            return false;
        }

    }

    public static boolean isVector(Token delimiter)
    {
        if (delimiter instanceof Delimiter)
        {
            return ((Delimiter) delimiter).shape.equals(Constants.VECTOR_ABBR);
        }
        else
        {
            return false;
        }
    }


    public static boolean isOpen(Token t)
    {
        return (t instanceof Delimiter) && delimMap.keySet().contains(((Delimiter) t).shape );
    }
    public static boolean isClose(Token t)
    {
        return (t instanceof Delimiter) && delimMap.values().contains(((Delimiter) t).shape);
    }

    //语法分析阶段，所以node
    public static boolean match(Node start,Node end)
    {
        if (!(start instanceof Delimiter) ||
                !(end instanceof  Delimiter))
        {
            return false;
        }

        String gotStart=delimMap.get(((Delimiter) start).shape);
        return gotStart != null && gotStart.equals(((Delimiter) end).shape);
    }


    @Override
    public Value typeCheck(Environment env)
    {
        return null;
    }

    @Override
    public Value interp(Environment env)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return shape;
    }
}
