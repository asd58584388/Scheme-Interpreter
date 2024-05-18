package com.company.value.Type;

import com.company.value.Constants;
import com.company.value.Value;

import java.util.HashSet;
import java.util.Set;

public class UnionType extends Value
{
    public Set<Value> values = new HashSet<>();

    public void add(Value value) {
        if (value instanceof UnionType) {
            values.addAll(((UnionType) value).values);
        } else {
            values.add(value);
        }
    }

    public static Value union(Value... values) {
        UnionType u = new UnionType();
        for (Value v : values) {
            u.add(v);
        }
        if (u.size() == 1) {
            return u.first();
        } else {
            return u;
        }
    }

    public Value first() {
        return values.iterator().next();
    }

    public int size() {
        return values.size();
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.PAREN_BEGIN).append("U ");

        boolean first = true;
        for (Value v : values) {
            if (!first) {
                sb.append(" ");
            }
            sb.append(v);
            first = false;
        }

        sb.append(Constants.PAREN_END);
        return sb.toString();
    }
}
