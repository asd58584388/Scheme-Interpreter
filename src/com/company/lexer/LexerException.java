package com.company.lexer;

public class LexerException extends Exception
{
    //错误的行、列、起始位置
    public int line;
    public int col;
    public int start;

    public LexerException(String message, int line, int col, int start)
    {
        super(message);
        this.line = line;
        this.col = col;
        this.start = start;
    }

    @Override
    public String toString()
    {
        return (line + 1) + ":" + (col + 1) + " lexer错误： " + getMessage();
    }
}
