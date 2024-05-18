package com.company.ast;

import com.company.interpret.Environment;
import com.company.value.Value;

import java.util.ArrayList;
import java.util.List;

public class Begin extends Node
{
    List<Node> nodes;

    public Begin(List<Node> nodes,int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.nodes=nodes;
    }


    @Override
    public Value typeCheck(Environment env)
    {
        Value lastValue=null;

        for (Node node : nodes)
        {
            lastValue=node.typeCheck(env);
        }

        return  lastValue;
    }

    @Override
    public Value interp(Environment env)
    {
        Value last=null;

        for (Node node : nodes)
        {
            last=node.interp(env);
        }

        return last;

    }

}
