package com.company.lexer;

import com.company.interpret.Environment;
import com.company.util.Util;
import com.company.value.Constants;
import com.company.value.Type.Type;
import com.company.value.Value;

public class Name extends Identifier
{

    public String type;

    public Name(String name,int start, int end, int line, int col)
    {
        super(name,start, end, line, col);

    }

    public Name(String name)
    {
        super(name,0,0,0,0);
    }


    @Override
    public Value interp(Environment env)
    {

        Value val=env.lookupValue(this.name);

        if (val==null)
        {
            Util.abort("Environment中没有找到变量 "+name.toString()+ " 对应的值");
        }

        return val;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        if (type!=null)
        {
            switch (type)
            {
                case Constants.BOOL_TYPE:return Type.BOOL;
                case Constants.DOUBLE_TYPE:return Type.DOUBLE;
                case Constants.INT_TYPE:return Type.INT;
                case Constants.STRING_TYPE:return Type.STRING;
            }
        }
        Value v = env.lookupValue(name);
        if (v != null) {
            return v;
        } else {
            Util.abort(this, "unbound variable: " + id);
            return Value.VOID;
        }
    }

    @Override
    public String toString()
    {
        return name;
    }

}
