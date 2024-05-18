package com.company.ast;

import com.company.lexer.Token;
import com.company.interpret.Environment;
import com.company.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * ast的节点的抽象类
 */
public abstract class Node extends Token
{

    //用于产生中间代码
    public static Map<String,Integer> table=new HashMap<>();
    public  static int globalID=0;

    public Node(int start, int end, int line, int col)
    {
        super(start, end, line, col);
    }


    public abstract Value typeCheck(Environment env);


    //无参的情况
    public abstract Value interp(Environment env);

    //有参的情况
    public Value interp (Node node,Environment env)
    {
        node.interp(env);
        return this.interp(env);
    }

    public String getLineCol() {
        return (line + 1) + ":" + (col + 1);
    }



}
