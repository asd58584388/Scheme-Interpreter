package com.company.ast;

import com.company.interpret.Closure;
import com.company.interpret.Environment;
import com.company.lexer.Identifier;
import com.company.lexer.Name;
import com.company.util.Util;
import com.company.value.PairValue;
import com.company.value.Value;
import com.company.value.primitives.ListFun;
import com.company.value.primitives.PrimitiveFun;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Call extends Node
{
    public Node op;
    public Argument args;

//    public static boolean isTailRecur = false;

//    //用来判断尾递归
//    public static Stack<Node> stack = new Stack<>();

    public Call(Node op, Argument args, int start, int end, int line, int col)
    {
        super(start, end, line, col);
        this.op = op;
        this.args = args;
    }

    @Override
    public Value typeCheck(Environment env)
    {
        return op.typeCheck(env);
    }

    @Override
    public Value interp(Environment env)
    {
        //part 1：从环境中得到函数的值
        Value opv = this.op.interp(env);

//        //如果是尾递归
//        if (isTailRecur)
//        {
//
//        }
//        //不是尾递归
//        else
//        {
//            isTailRecur=isTailRecur(op,opv,env);
//            //处理尾递归情形
//            if (isTailRecur)
//            {
//
//            }
//            //依然不是尾递归，什么也不做
//        }

        //用于判定是否是尾递归（会误判非尾递归为尾递归，比如 (+ ... 递归函数) ）（需要对中间代码来优化）
//        if (stack.isEmpty())
//        {
//            stack.push(op);
//        }
//        else
//        {
//            Node stackTop = stack.peek();
//            //primitive对环境没影响，因此不用管是否尾递归
//            if (stackTop.equals(op))
//            {
//                isTailRecur=true;
//            }
//            else
//            {
//                stack.push(op);
//            }
//        }



        //part 2：函数是闭包
        if (opv instanceof Closure)
        {

            Closure closure = (Closure) opv;

            //lambda表达式的参数名
            List<Name> params = closure.fun.parameters;

            //part 2.1：没有参数的情况
            if (params == null || params.size() == 0)
            {
                return closure.fun.body.interp(closure.env);
            }
            //part 2.2：可变参数情况
            else if (closure.fun.hasList != null)
            {
                //实际参数
                List<Node> nodes = args.args;

                //参数是否个数一致
                int arguNum = nodes.size();
                int paramNum = params.size();
                if (arguNum < paramNum)
                {
                    Util.abort("参数个数不一致");
                    return null;
                }



                //-----------------------------
                //用闭包的环境来创建新的子环境来执行（lexical binding）
                //这里闭包的环境只有一层有效
                if (!closure.env.equals(env))
                {
                    closure.env.parent = env;//这里用新的环境，但是也要是在原来环境的基础上
                }

                //闭包环境的父环境是env
                Environment closureEnv = new Environment(env);
                for (String key : closure.env.table.keySet())
                {
                    closureEnv.put(key, closure.env.table.get(key));
                }
                //-----------------------------

                //先将固定参数put入环境中
                for (int i = 0; i < paramNum; i++)
                {
                    Node node = nodes.get(i);
                    Name param = params.get(i);
                    closureEnv.put(param.name, node.interp(closureEnv));
                }

                //处理可变参数
                Identifier hasList = closure.fun.hasList;
                List<Value> listVal = new ArrayList<>();
                for (int i = paramNum; i < arguNum; i++)
                {
                    Node node = nodes.get(i);
                    listVal.add(node.interp(closureEnv));
                }
                PairValue pariVal = (PairValue) ListFun.makeList(listVal);
                closureEnv.put(hasList.name, pariVal);

                return closure.fun.body.interp(closureEnv);
            }
            //part 2.3：有参数情况
            else
            {
                //实际参数
                List<Node> nodes = args.args;

                //判断是否参数个数一致
                if (nodes.size() != params.size())
                {
                    Util.abort("参数个数不一致");
                    return null;
                }


                //-----------------------------
                //用闭包的环境来创建新的子环境来执行（lexical binding）
                //这里闭包的环境只有一层有效
                if (!closure.env.equals(env))
                {
                    closure.env.parent = env;//这里用新的环境，但是也要是在原来环境的基础上
                }

                //闭包环境的父环境是env
                Environment closureEnv = new Environment(env);
                for (String key : closure.env.table.keySet())
                {
                    closureEnv.put(key, closure.env.table.get(key));
                }
                //-----------------------------


                for (int i = 0; i < nodes.size(); i++)
                {
                    Node node = nodes.get(i);
                    Name param = params.get(i);

                    closureEnv.put(param.name, node.interp(closureEnv));
                }
                return closure.fun.body.interp(closureEnv);
            }

        }
        //part 3：函数是primitive函数
        else if (opv instanceof PrimitiveFun)
        {
            PrimitiveFun prim = (PrimitiveFun) opv;
            List<Value> values = new ArrayList<>();
            List<Node> args = this.args.args;
            for (Node arg : args)
            {
                values.add(arg.interp(env));
            }
            return prim.apply(values, this);
        }
        //part 4：其它情况，终止
        else
        {
            Util.abort("错误的函数: " + opv);
            return null;
        }

    }

//    /**
//     * 判断是否尾递归节点
//     *
//     * @param op  call节点的第一个参数
//     * @param opv op求值之后的值
//     * @param env op的求值环境
//     *
//     * @return
//     */
//    private boolean isTailRecur(Node op, Value opv, Environment env)
//    {
//        if (opv instanceof Closure)
//        {
//            Begin lambdaBegin = (Begin) ((Lambda) op).body;
//            List<Node> nodeList = lambdaBegin.nodes;
//            Node tail = nodeList.get(nodeList.size() - 1);
//            return tail.interp(env).equals(opv);
//        }
//        else
//        {
//            return false;
//        }
//    }
}
