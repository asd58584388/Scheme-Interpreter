package com.company.parser;

import com.company.ast.Node;
import com.company.lexer.Token;

/**
 * 语法分析异常处理
 */
public class ParserException extends Exception
{
    //错误的行、列、起始位置
    public int line;
    public int col;
    public int start;


    public ParserException(String message, Node token )
    {
        super(message);

    }

    @Override
    public String toString()
    {
        return (line + 1) + ":" + (col + 1) + " parse错误： " + getMessage();
    }
}
