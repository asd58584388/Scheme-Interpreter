package com.company.lexer;

import com.company.interpret.Environment;
import com.company.util.Util;
import com.company.value.Value;

public class Keyword extends Identifier
{

    public Keyword(String name,int start, int end, int line, int col)
    {
        super(name,start, end, line, col);
    }


    @Override
    public Value interp(Environment env)
    {
        Util.abort("关键字无法被解释");
        return null;
    }

    public static Keyword genKeyword(String keyword)
    {
        return new Keyword(keyword,0 ,0,0,0);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
