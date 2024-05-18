package com.company.ast;

import com.company.interpret.Environment;
import com.company.value.Value;

import java.util.List;

public class Tuple extends Node
{
    public List<Node> elements;
    public Node open;
    public Node close;
    public String shape;

    public Tuple(List<Node> elements, Node open, Node close, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.elements = elements;
        this.open = open;
        this.close = close;
        this.shape="(";
    }

    public Tuple(List<Node> elements, Node open, Node close,String shape, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.elements = elements;
        this.open = open;
        this.close = close;
        this.shape=shape;
    }

    //用于宏生成的tuple
    public Tuple(List<Node> elements)
    {
        super(0, 0, 0, 0);
        this.elements = elements;
    }



//    public Node getFirst()
//    {
//        if (elements.isEmpty())
//        {
//            return null;
//        }
//        else
//        {
//            return elements.get(0);
//        }
//    }


    @Override
    public Value typeCheck(Environment env)
    {
        System.exit(1);
        return null;
    }

    //只是preAst中的节点，最终Ast不存在，因此不需要interpret
    @Override
    public Value interp(Environment env)
    {
        return null;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < elements.size(); i++)
        {
            sb.append(elements.get(i).toString());
            if (i != elements.size() - 1)
            {
                sb.append(" ");
            }
        }

        return (open == null ? "" : open) + sb.toString() + (close == null ? "" : close);
    }

    public boolean isEmpty()
    {
        return elements.size()==0;
    }

    public Node[] toArray()
    {

        Node[] Array = new Node[elements.size()];
        elements.toArray(Array);
        return Array;
    }
}
