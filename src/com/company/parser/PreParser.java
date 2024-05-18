package com.company.parser;

import com.company.ast.Node;
import com.company.ast.Quote;
import com.company.ast.Tuple;
import com.company.lexer.*;
import com.company.value.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * 第一次parse，先形成tuple元组
 */
public class PreParser
{

    Lexer lexer;


    public PreParser(Lexer lexer)
    {
        this.lexer = lexer;
    }


    public Node nextNode() throws ParserException, LexerException
    {
        return nextNode1(0);
    }


    public Node nextNode1(int depth) throws ParserException, LexerException
    {
        Node first = lexer.nextToken();

        //part 1：文件结束
        if (first == null)
        {
            return null;
        }
        //part 2：匹配(...)和[...]这样的元组
        if (Delimiter.isOpen(first))
        {
            //elements存放nodes
            List<Node> elements = new ArrayList<>();
            Node next;
            for (next = nextNode1(depth + 1); !Delimiter.match(first, next); next = nextNode1(depth + 1))
            {
                if (next == null)
                {
                    throw new ParserException("没有找到close分隔符: " + first.toString(), first);
                }
                else if (Delimiter.isClose(next))
                {
                    throw new ParserException("close分隔符不匹配: " + next.toString() + " 不匹配 " + first.toString(), next);
                }
                else
                {
                    elements.add(next);
                }
            }

            //(...)
            if (((Delimiter)first).shape.equals("("))
            {
                return new Tuple(elements, first, next, first.start, next.end, first.line, first.col);
            }
            //[...]
            else
            {
                return new Tuple(elements, first, next, ((Delimiter) first).shape,first.start, next.end, first.line, first.col);
            }
        }
        //关于part 3 和 part 4，因为 ' ` #可以直接确定一个node，因此对于这三个分隔符开头的token，可以一次parse完毕
        //part 3：匹配Quote ( ' )
        else if (Delimiter.isQuote(first))
        {

            Node node = nextNode1(depth + 1);
            //part 3.1：判断Quote之后的符号，如果是分隔符(因为得到的肯定不是分隔符，如果得到了必定错误)
            if (node instanceof Delimiter)
            {
                throw new ParserException("语法错误：' 或 ` 分隔符后不能是除open paren或者 # 以外的符号",node);
            }
            //part 3.2：Quote之后不是分隔符
            else
            {
                return new Quote(node,first.toString(), first.start, node.end, first.line, first.col);
            }
        }
        //part 4：匹配Quasiquote
        //暂时和Quote一样
        else if (Delimiter.isQuasiquote(first))
        {

            Node node = nextNode1(depth + 1);
            //part 4.1：判断Quote之后的符号，如果是分隔符(因为得到的肯定不是分隔符，如果得到了必定错误)
            if (node instanceof Delimiter)
            {
                throw new ParserException("语法错误：' 或 ` 分隔符后不能是除open paren或者 # 以外的符号",node);
//                }
            }
            //part 4.2：Quote之后不是分隔符
            else
            {
                //quote可以嵌套
                return new Quote(node,first.toString(), first.start, node.end, first.line, first.col);
            }
        }
        //part 5：匹配Vector( # )
        else if (Delimiter.isVector(first))
        {
            List<Node> result=new ArrayList<>();
            //将这样的节点转换成 (vector ...) ，将#当成语法糖
            result.add(new Name(Constants.VECTOR_PRIMITIVE, first.start, first.end, first.line, first.col));

            Node node = nextNode1(depth + 1);

            if (node instanceof Tuple)
            {
                List<Node> elements=((Tuple) node).elements;

                for (Node element : elements)
                {
                    result.add(new Quote(element, node.start, node.end,node.line,node.col));
                }

                return new Tuple(result, ((Tuple) node).open, ((Tuple) node).close,node.start,node.end, first.line, first.col);
            }
//            else if (node instanceof Str)
//            {
//                return node;
//            }
            else
            {
                throw new ParserException("语法错误：# 分隔符后面只能接上tuple元组", node);
            }
        }
        //part 6：根环境下遇到close分隔符
        else if (depth == 0 && Delimiter.isClose(first))
        {
            throw new ParserException("不匹配的close分隔符: " + first.toString() + " 没有匹配到任何open分隔符", first);
        }
        //part 7：其它
        else
        {
            return first;
        }
    }


    public Node preparse() throws ParserException, LexerException
    {
        List<Node> elements = new ArrayList<>();
        elements.add(Keyword.genKeyword(Constants.BEGIN_KEYWORD));

        Node s = nextNode();
        //标识位置
        Node first = s;
        Node last = null;
        for (; s != null; last = s, s = nextNode())
        {
            elements.add(s);
        }

        return new Tuple(elements, Delimiter.genDelimiter(Constants.PAREN_BEGIN), Delimiter.genDelimiter(Constants.PAREN_END), first == null ? 0 : first.start, last == null ? 0 : last.end, 0, 0);
    }
}
