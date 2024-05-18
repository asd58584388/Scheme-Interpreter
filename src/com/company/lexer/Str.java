package com.company.lexer;

import com.company.ast.Node;
import com.company.interpret.Environment;
import com.company.value.Constants;
import com.company.value.StringValue;
import com.company.value.Type.Type;
import com.company.value.Value;

public class Str extends Node
{
    public String content;
    public Str(String content,int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.content=content;
    }



    @Override
    public Value interp(Environment env) {
        //处理转义字符
        char[] chars = content.toCharArray();
        StringBuilder sb=new StringBuilder(content);

        int deletes=0;
        for (int i = 0; i < chars.length; i++)
        {
            char c=chars[i];

            if (c== Constants.STRING_ESCAPE_CHAR && i+1< chars.length)
            {
                sb.deleteCharAt(i-deletes);
                deletes++;
                i++;
            }

        }

        return new StringValue(sb.toString());
    }

    @Override
    public Value typeCheck(Environment env)
    {
        return Type.STRING;
    }

    @Override
    public String toString() {
        return "\"" + content + "\"";
    }
}
