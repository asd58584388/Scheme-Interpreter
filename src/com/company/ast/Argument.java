package com.company.ast;

import com.company.interpret.Environment;
import com.company.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * 没有把argument当成节点，当成是call节点里的子节点
 */
public class Argument
{
    List<Node> args;

    public Argument(List<Node> args)
    {
        this.args=args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Node e : args) {
            if (!first) {
                sb.append(" ");
            }
            sb.append(e);
            first = false;
        }
        return sb.toString();
    }

}
