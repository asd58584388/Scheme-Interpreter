package com.company.lexer;


import com.company.ast.Node;
import com.company.interpret.Environment;
import com.company.value.Constants;
import com.company.value.Value;
import com.company.value.primitives.Cons;

public class Identifier extends Node
{
    public String name;
    public int id;

    public Identifier(String name,int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.name=name;

        Integer ID= table.get(name);
        if (ID==null)
        {
            this.id=globalID;
            Node.globalID++;

            table.put(name,id);
        }
        else
        {
            this.id=ID;
        }

    }

    @Override
    public Value typeCheck(Environment env)
    {
        System.exit(1);
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
        return name;
    }
}
