package com.company.value;

import com.company.util.Util;

import java.util.Arrays;
import java.util.List;

public class VectorValue extends Value
{
    public Value[] values;

    public VectorValue(List<Value> values)
    {
        this.values=new Value[values.size()];
        values.toArray(this.values);
    }

    public Value VectorRef(int i)
    {
        if (i>=0)
        {
            return values[i];
        }
        else
        {
            Util.abort("参数错误：ref参数不能为负数");
            return null;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(Constants.VECTOR_ABBR);

        if (values==null||values.length==0)
        {
            sb.append(Constants.PAREN_BEGIN);
            sb.append(Constants.PAREN_END);
        }
        else
        {
            sb.append("(");

            for (int i = 0; i < values.length; i++)
            {
                sb.append(values[i].toString());

                if (i != values.length - 1)
                {
                    sb.append(" ");
                }
            }

            sb.append(")");
        }

        return sb.toString();
    }
}
