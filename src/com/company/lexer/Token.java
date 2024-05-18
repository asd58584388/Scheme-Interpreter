package com.company.lexer;

public abstract class Token
{
    public int start;
    public int end;
    public int line;
    public int col;

    public Token(int start, int end, int line, int col)
    {
        this.start = start;
        this.end = end;
        this.line = line;
        this.col = col;
    }
}
