package com.company.interpret;

import com.company.ast.Node;
import com.company.lexer.Name;
import com.company.util.Util;
import com.company.value.Value;

/**
 * 通用类，方便interpret阶段环境相关的操作
 */
public class Binder
{

    //define只能生成新的var而不可以重复定义
    public static void define(Node var, Value value, Environment env)
    {
        if (var instanceof Name)
        {
            String name=((Name) var).name;
            Value v=env.lookupValueLocal(name);
            if (v!=null)
            {
                Util.abort("重复定义： "+name);
            }
            else
            {
                env.put(name,value);
            }
        }
        else
        {
            Util.abort("define错误：var为错误类型 "+var.toString());
        }
    }


    //assgin可以生成新的var，也可以覆盖原值
    public static void assign(Node var,Value value,Environment env)
    {

            String name=((Name) var).name;
            Value v=env.lookupValue(name);
            if (v==null)
            {
                env.put(name,value);
            }
            else
            {
                env.putValue(name,value);
            }

    }

    //只在当前环境下assign（生成新的var或者覆盖原值）
    public static void assignLocal(Node var,Value value,Environment env)
    {
        String name=((Name) var).name;
        Value v=env.lookupValueLocal(name);
        if (v==null)
        {
            env.put(name,value);
        }
        else
        {
            env.putValue(name,value);
        }
    }



}
