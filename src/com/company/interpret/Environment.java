package com.company.interpret;

import com.company.util.Util;
import com.company.value.Value;
import com.company.value.primitives.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environment
{
    //存放变量
    public Map<String,Object> table=new HashMap<>();
    //父环境，只要父环境就行，不需要表示子环境
    public Environment parent;

    //根环境
    public Environment()
    {
        this.parent=null;
    }

    //子环境
    public Environment(Environment parent)
    {
        this.parent=parent;
    }


    //初始化环境
    public static Environment BuildInitEnv()
    {
        Environment init=new Environment();

        //算数运算符
        init.put("+", new Add());
        init.put("-", new Sub());
        init.put("*", new Mult());
        init.put("/", new Div());

        //逻辑运算符号
        init.put("<", new Lt());
        init.put("<=", new LtE());
        init.put(">", new Gt());
        init.put(">=", new GtE());
        init.put("eq?", new Eq());
        init.put("and", new And());
        init.put("or", new Or());
        init.put("not", new Not());

        //其它
        init.put("print",new Print());
        init.put("zero?",new Zero());
        init.put("null?",new isNull());

        //list
        init.put("cons",new Cons());
        init.put("list",new ListFun());
        init.put("car",new Car());
        init.put("cdr",new Cdr());
        init.put("list-ref",new ListRef());

        //bool值
        init.put("true", Value.TRUE);
        init.put("false", Value.FALSE);

        //vector
        init.put("vector",new Vector());
        init.put("vector-ref",new VectorRef());

        return init;
    }



    //往环境中添加键值
    public void put(String key, Object val )
    {
        table.put(key,val);
    }

    //从环境中得到被Matched的key的value（用于预处理阶段）
    public Object getMatchedNode(String key)
    {
        //只在这一级环境中查找，不可能在父环境
        return table.get(key);
    }


    //在环境中根据键修改值
    public void putValue(String key ,Value val)
    {
        if (table.get(key)==null)
        {
            if (parent==null)
            {
                Util.abort("没有找到key对应的value");
            }
            else
            {
                parent.putValue(key,val);
            }
        }
        else
        {
            table.put(key,val);
        }
    }

    //在环境中（包含父级和本级）查找值
    public Value lookupValue(String key)
    {
        //步骤 1：先在本级环境中找
        Object v = table.get(key);
        //步骤 2：判断是否为空
        if (v==null)
        {
            //为空：判断是否存在父环境
            if (this.parent!=null)
            {
                //存在：在父环境中查找
                return parent.lookupValue(key);
            }
            //不存在：返回null
            else
            {
                return null;
            }
        }
        //步骤 3：不为空：判断是否得到的是Value类型
        else if (v instanceof  Value)
        {
            return (Value) v;
        }
        //步骤 4：其它情况：报错
        else
        {
            Util.abort("要查找的key没有对应的Value"+v.toString());
            return null;
        }
    }

    //在本级环境中查找值
    public Value lookupValueLocal(String key)
    {
        Object v = table.get(key);
        if (v==null)
        {
            return null;
        }
        else if (v instanceof  Value)
        {
            return (Value) v;
        }
        else
        {
            Util.abort("要查找的key没有对应的Value"+v.toString());
            return null;
        }
    }

    public List<Object> lookupSyntax(String key)
    {
        List<Object> n= (List<Object>) table.get(key);
        if (n==null)
        {
            if (this.parent!=null)
            {

                return parent.lookupSyntax(key);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return n;
        }
    }
}
