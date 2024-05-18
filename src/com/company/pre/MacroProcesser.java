package com.company.pre;

import com.company.ast.Node;
import com.company.ast.Tuple;
import com.company.interpret.Environment;
import com.company.lexer.*;
import com.company.lexer.Number;
import com.company.parser.ParserException;
import com.company.util.Util;
import com.company.value.Constants;

import java.util.ArrayList;
import java.util.List;

public class MacroProcesser
{
    Environment env;
    Node preAst;
    Environment matchedEnv;

    public MacroProcesser(Node preAst)
    {
        //这里的Environment用来存放新定义的语法，经过这一阶段后就被废弃
        env = new Environment();
        this.preAst = preAst;
    }

    public Node preProcess() throws ParserException
    {
        return preProcess1(preAst);
    }

    public Node preProcess1(Node pre) throws ParserException
    {
        //part 1：先匹配是否tuple，如果是tuple，则对每个元素进行预处理
        if (pre instanceof Tuple)
        {
            //part 1.1：得到tuple中的list，因为有可能会替换掉某个Node,或整个nodes
            List<Node> nodes = ((Tuple) pre).elements;

            //part 1.2：如果得到的list为空，不必改变nodes，直接返回即可
            if (nodes.size() == 0)
            {
                return pre;
            }

            //part 1.3：对tuple中第一个元素进行分析，分情况处理
            //1.Macro，进行宏处理
            Node first = nodes.get(0);
            if (first instanceof Macro)
            {
                if (((Macro) first).name.equals(Constants.DEFINE_SYNTAX_KEYWORD))
                {
                    DefineSyntax((Tuple) pre);
                    return null;
                }
                //todo:---
                else if (((Macro) first).name.equals(Constants.LET_SYNTAX_KEYWORD))
                {

                }
                else if (((Macro) first).name.equals(Constants.SYNTAX_RULES_KEYWORD))
                {
                    throw new ParserException("语法错误：syntax-rules不能单独出现，必须嵌在define_syntax里", pre);
                }
            }
            //2.Begin，对每个node进行处理，处理完后更新nodes
            else if (first instanceof Keyword && ((Keyword) first).name.equals(Constants.BEGIN_KEYWORD))
            {
                for (int i = 1; i < nodes.size(); i++)
                {
                    Node preProcessed = preProcess1(nodes.get(i));
                    if (preProcessed == null)
                    {
                        //如果某个节点是宏，因为产生的是副作用，则remove掉
                        nodes.remove(i);
                        //移除掉节点i要减1
                        i = i - 1;
                    }
                    else
                    {
                        nodes.set(i, preProcessed);
                    }
                }
            }
            //3.其它identifier，有可能是新定义的syntax
            else if (first instanceof Identifier)
            {
                List<Object> transformer = env.lookupSyntax(((Identifier) first).name);
                if (transformer != null)
                {
                    replaceSyntax((Tuple) pre, nodes, transformer);
                }

                return pre;

            }
            //4.tuple
            else if (first instanceof Tuple)
            {
                nodes.set(0, preProcess1(first));
            }
            //5.其它情况或者处理完之后
            return pre;

        }
        //part 2：其它，直接返回，不可能匹配到identifier是新定义的语法
        // 因为进入preprocessing的肯定先是一个begin作为第一个元素的tuple，同时，又只有每个tuple的第一个元素有可能是新定义的语法
        else
        {
            return pre;
        }
    }

    /**
     * pre实际上就是要被替换的node（通过替换tuple pre里的element来实现替换）
     *
     * @param pre
     * @param nodes
     * @param transformer
     */
    private void replaceSyntax(Tuple pre, List<Node> nodes, List<Object> transformer)
    {
        String[] literals = (String[]) transformer.get(0);
        //patterns和specs里每个node都是tuple
        Node[] patterns = (Node[]) transformer.get(1);
        Node[] specs = (Node[]) transformer.get(2);

        //匹配syntax-rules
        //匹配每个pattern
        int matchedIndex = -1;

        matchedIndex = patternMatch(patterns, nodes, literals);

        if (matchedIndex == -1)
        {
            //不可能既匹配到了语法，又没能匹配成功
            Util.abort("语法错误");
        }

        //替换spec
        Tuple spec = (Tuple) specs[matchedIndex];
        Tuple replacedSpec = replaceSpec(spec);
        pre.elements = replacedSpec.elements;
    }

    /**
     * 对得到pattern之后的spec进行替换
     *
     * @param spec
     *
     * @return 一个被替换的Node
     */
    public Tuple replaceSpec(Tuple spec)
    {
        List<Node> nodeList = spec.elements;

        for (int i = 0; i < nodeList.size(); i++)
        {
            Node node = nodeList.get(i);

            Object matchedNode = matchedEnv.getMatchedNode(node.toString());
            if (matchedNode != null)
            {
                if (matchedNode instanceof List)
                {
                    //需要继续判断是否...
                    if (nodeList.get(i + 1).toString().equals("..."))
                    {
                        //...可以直接替换
                        nodeList.remove(i);
                        nodeList.remove(i);
                        nodeList.addAll(i, (List<Node>) matchedNode);
                    }
                    //是.
                    else
                    {
                        //.要生成一个list
                        Name list = new Name("list");
                        ((List<Node>) matchedNode).add(0, list);
                        Tuple tuple = new Tuple((List<Node>) matchedNode);
                        tuple.shape="(";
                        nodeList.set(i, tuple);
                    }
                }
                else if (matchedNode instanceof Node)
                {
                    nodeList.set(i, (Node) matchedNode);
                }
            }
            else if (node instanceof Tuple)
            {
                nodeList.set(i, replaceSpec((Tuple) node));
            }
            //可能是其它新定义的语法
            else if (i == 0 && node instanceof Identifier)
            {

                List<Object> transformer = env.lookupSyntax(((Identifier) node).name);

                if (transformer != null)
                {
                    replaceSyntax(spec, spec.elements, transformer);
                }

                //如果不是则不需要改变
            }
        }

        return spec;

    }

    //todo: 1.解决literal 2.如果匹配成功之后，spec中还有被define的syntax
    //todo: 1.将预处理得到的环境作为之后执行的环境 2.替换spec中的define，let表达式为lambda表达式

    /**
     * 根据nodes匹配pattern
     *
     * @param patterns
     * @param nodes
     *
     * @return 匹配成功返回一个已经包含被匹配的pattern的环境，pattern的name作为key，nodes作为value ，匹配失败返回null
     */
    public int patternMatch(Node[] patterns, List<Node> nodes, String[] literals)
    {
        for (int i = 0; i < patterns.length; i++)
        {
            //对每个pattern里的每个element进行匹配（因为一个pattern肯定是一个tuple，因此，当成tuple匹配）
            if (patterns[i] instanceof Tuple)
            {

                //1. 先匹配第一个参数，如果有pattern的第一个参数不是identifier，直接判定语法错误，如果不相等，continue
                if (!(((Tuple) patterns[i]).elements.get(0) instanceof Identifier && nodes.get(0) instanceof Identifier))
                {
                    Util.abort("语法错误");
                }
                else if (!(((Identifier) ((Tuple) patterns[i]).elements.get(0)).name.equals(((Identifier) nodes.get(0)).name)))
                {
                    continue;
                }

                //2. 匹配tuple
                Node[] tupleArray = ((Tuple) patterns[i]).toArray();
                //这里matchedEnv的父环境是env
                Environment matchedEnv = new Environment(env);
                if (tupleMatch(tupleArray, nodes, literals, matchedEnv))
                {
                    //匹配成功，返回新的环境，用于下一步匹配和替换spec
                    this.matchedEnv = matchedEnv;
                    return i;
                }
                //匹配失败，继续匹配

            }
            else
            {
                Util.abort("语法错误");
            }


        }

        //匹配失败
        return -1;
    }

    private boolean tupleMatch(Node[] pattern, List<Node> nodes, String[] literals, Environment matchedEnv)
    {

        //先根据长度判断
        int last = pattern.length - 1;
        if ((pattern[last] instanceof Identifier && ((Identifier) pattern[last]).name.equals(Constants.ELLIPSIS_SPECIAL_TOKEN)) || (pattern[last - 1] instanceof Identifier && ((Identifier) pattern[last - 1]).name.equals(Constants.DOT_SPECIAL_TOKEN)))
        {
            if (nodes.size() < pattern.length - 2)
            {
                return false;
            }

        }
        else
        {
            if (nodes.size() != pattern.length)
            {
                return false;
            }
        }

        //这里size是用于遍历的长度，这里是为了确保不会超出索引
        int size;
        if (nodes.size() > pattern.length)
        {
            size = pattern.length;
        }
        else
        {
            size = nodes.size();
        }

        //对每个node进行分情况匹配
        for (int i = 0; i < size; i++)
        {
            Node node = nodes.get(i);

            //1. tuple
            if (pattern[i] instanceof Tuple && node instanceof Tuple)
            {

                Node[] nextTuple = ((Tuple) pattern[i]).toArray();
                //1.1 tuple匹配成功，继续匹配,(tuple不用管环境）
                if (tupleMatch(nextTuple, ((Tuple) node).elements, literals, matchedEnv))
                {
                    continue;
                }
                //1.2 tuple匹配失败，返回false
                else
                {
                    return false;
                }

            }
            //2. identifier (pattern variable)
            else if (pattern[i] instanceof Identifier)
            {
                //对于identifier需要进一步分情况
                //1: a . b    ( . b) (a . b)  (.后面必须附加一个identifier)
                //2: a b ...  ( ... ) (a b ... )  (...后面不允许再出现 元素)
                //3: a b

                //2.1 如果是 .
                if (((Identifier) pattern[i]).name.equals(Constants.DOT_SPECIAL_TOKEN))
                {
                    // .后面有且只能跟一个元素（代表一个 list ）
                    if (i + 1 == pattern.length - 1 && pattern[i + 1] instanceof Identifier)
                    {
                        //修改matcheEnv，产生副作用
                        List<Node> dotList = new ArrayList<>();
                        for (int j = i; j < nodes.size(); j++)
                        {
                            dotList.add(nodes.get(j));
                        }
                        matchedEnv.put(pattern[i + 1].toString(), dotList);


                        //这里可以返回，不必继续匹配，因为 . 后面是identifier就行，node 任意匹配
                        return true;
                    }
                    else
                    {
                        Util.abort("语法错误");
                    }

                }
                //2.2 如果是 ...
                else if (((Identifier) pattern[i]).name.equals(Constants.ELLIPSIS_SPECIAL_TOKEN))
                {
                    // ... 之后不能有元素
                    if (i + 1 == pattern.length)
                    {
                        boolean isMatch = false;
                        List<Node> subNode = nodes.subList(i - 1, nodes.size());

                        //这里直接将subNode放到matchEnv就行,因为如果返回false,整个匹配就作废，matchEnv就没用
                        matchedEnv.put(pattern[i - 1].toString(), subNode);

                        //  ... 之前是tuple
                        if (pattern[i - 1] instanceof Tuple && node instanceof Tuple)
                        {
                            for (Node element : subNode)
                            {
                                if (element instanceof Tuple)
                                {
                                    Node[] nextTuple = ((Tuple) pattern[i - 1]).toArray();

                                    //这里用新的环境，因为已经将 ... 代表的list放入了matchEnv;
                                    Environment tempEnv = new Environment();
                                    isMatch = tupleMatch(nextTuple, ((Tuple) element).elements, literals, tempEnv);

                                }
                                //不是tuple，也失败
                                else
                                {
                                    return false;
                                }

                                //有一个没匹配成功，就失败
                                if (!isMatch)
                                {
                                    return false;
                                }
                            }

                            //这里可以返回，而不是继续匹配，因为...后面没有元素了
                            return isMatch;
                        }
                        // ... 之前是identifier,直接返回true
                        else if (pattern[i - 1] instanceof Identifier && !isLiteral(literals, ((Identifier) pattern[i - 1]).name))
                        {
                            return true;
                        }
                        else
                        {
                            Util.abort("语法错误");
                        }

                    }
                    else
                    {
                        Util.abort("语法错误");
                    }
                }
                //2.3 如果是Litetal
                else if (isLiteral(literals, ((Identifier) pattern[i]).name))
                {
                    if (node instanceof Identifier && ((Identifier) node).name.equals(((Identifier) pattern[i]).name))
                    {
                        //literal匹配成功，继续匹配元素
                        continue;
                    }
                }
                //2.4 其它（不用匹配）
                else
                {
                    //副作用
                    matchedEnv.put(((Identifier) pattern[i]).name, node);

                    continue;
                }
            }
            //3. number
            else if (pattern[i] instanceof Number && node instanceof Number)
            {
                if (((Number) pattern[i]).value.equals(((Number) node).value))
                {
                    continue;
                }
                else
                {
                    return false;
                }
            }
            //4. str
            else if (pattern[i] instanceof Str && node instanceof Str)
            {
                if (((Str) pattern[i]).content.equals(((Str) node).content))
                {
                    continue;
                }
                else
                {
                    return false;
                }
            }
            //5. 匹配失败，
            else
            {
                return false;
            }
        }

        //满足以下条件可以不用继续匹配
        //1. 最后一个元素是 ...
        //2. 倒数第二个元素 是 . （在上面已经将情形匹配完了，如果没有大于等于1个参数，必定报错）（也就是说不能省略）
        //3. nodes数量和pattern数量一致
        //否则需要继续匹配（因为没匹配到后面的pattern）
        if (nodes.size() == pattern.length)
        {
            return true;
        }
        else if (pattern[last] instanceof Identifier && ((Identifier) pattern[last]).name.equals(Constants.ELLIPSIS_SPECIAL_TOKEN))
        {
            //没有 ...参数
            if (nodes.size() == last - 1)
            {
                //用一个空list
                matchedEnv.put(pattern[last - 1].toString(),new ArrayList<>());
                return true;
            }
            //有一个 ...参数
            else if (nodes.size() == last)
            {
                List<Node> nodeList=new ArrayList<>();
                nodeList.add(nodes.get(last-1));
                matchedEnv.put(pattern[last - 1].toString(), nodeList);
                return true;
            }
            else
            {
                return false;
            }

        }
        else
        {
            return false;
        }

    }


    public boolean isLiteral(String[] literals, String name)
    {
        for (String literal : literals)
        {
            if (literal.equals(name)) return true;
        }

        return false;
    }


    public void DefineSyntax(Tuple tuple) throws ParserException
    {
        List<Node> elements = tuple.elements;
        if (elements.size() != 3)
        {
            throw new ParserException("语法错误：define-syntax参数数量不正确", tuple);
        }

        Node identifier = elements.get(1);
        if (!(identifier instanceof Identifier))
        {
            throw new ParserException("语法错误：define-syntax第一个参数必须是identifier " + identifier.toString(), identifier);
        }

        Node syntaxRule = elements.get(2);
        List<Object> syntaxRules;
        if (syntaxRule instanceof Tuple)
        {
            //rules的第一个参数存放literal，其它参数存放specs
            syntaxRules = parseSyntaxRules((Tuple) syntaxRule);
        }
        else
        {
            throw new ParserException("语法错误：define-syntax第二个参数必须是tuple " + syntaxRule.toString(), syntaxRule);
        }

        Environment newEnv = new Environment(env);
        this.env = newEnv;
        env.put(((Identifier) identifier).name, syntaxRules);

    }

    /**
     * @param tuple
     *
     * @return 返回一个第一个位置存放literal的list，第二个参数存放patterns，第三个参数存放specs的list
     *
     * @throws ParserException
     */
    public List<Object> parseSyntaxRules(Tuple tuple) throws ParserException
    {
        List<Node> elements = tuple.elements;
        if (elements.size() < 3)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        Node literal = elements.get(1);
        String[] literals;
        if (!(literal instanceof Tuple))
        {
            throw new ParserException("语法错误：第一个参数必须是tuple " + literal.toString(), literal);
        }
        else
        {
            literals = new String[((Tuple) literal).elements.size()];
            for (int i = 0; i < ((Tuple) literal).elements.size(); i++)
            {
                Node element = ((Tuple) literal).elements.get(i);

                if (!(element instanceof Identifier))
                {
                    throw new ParserException("语法错误：literal中每个元素必须是identifier " + element.toString(), element);
                }
                else
                {
                    literals[i] = ((Identifier) element).name;
                }
            }
        }

        List<Node> Rules = elements.subList(2, elements.size());
        Node[] patterns = new Node[Rules.size()];
        Node[] specs = new Node[Rules.size()];
        for (int i = 0; i < Rules.size(); i++)
        {
            Node node = Rules.get(i);

            if (!(node instanceof Tuple))
            {
                throw new ParserException("语法错误：每条rule必须是tuple " + node.toString(), node);
            }
            else if (((Tuple) node).elements.size() != 2)
            {
                throw new ParserException("语法错误：每条rule参数数量必须是2 " + node.toString(), node);
            }
            else if (!(((Tuple) node).elements.get(0) instanceof Tuple && ((Tuple) node).elements.get(1) instanceof Tuple))
            {
                throw new ParserException("语法错误：pattern和spec必须是tuple " + node.toString(), node);

            }

            List<Node> PatternAndSpec = ((Tuple) node).elements;

            patterns[i] = PatternAndSpec.get(0);
            specs[i] = PatternAndSpec.get(1);

        }


        List<Object> result = new ArrayList<>();
        result.add(literals);
        result.add(patterns);
        result.add(specs);
        return result;
    }

//    public Node parseLetSyntax(Tuple tuple) throws ParserException, LexerException
//    {
//        //part 1：判断参数数量是否正确
//        List<Node> elements = tuple.elements;
//        if (elements.size() < 3)
//        {
//            throw new ParserException("语法错误：参数数量不正确", tuple);
//        }
//
//        //part 2：判断第一个参数是否是元组
//        Node preBinds = elements.get(1);
//        if (!(preBinds instanceof Tuple))
//        {
//            throw new ParserException("语法错误：参数格式错误 " + preBinds.toString(), preBinds);
//        }
//
//        //part 3:parse第一个参数(tuple),分成两次parse这里很好处理
//        List<DefineSyntax> binds = new ArrayList<>();
//        for (Node preBind : ((Tuple) preBinds).elements)
//        {
//            if (preBind instanceof Tuple)
//            {
//                binds.add(parseDefineSyntax((Tuple) preBind));
//            }
//            else
//            {
//                throw new ParserException("语法错误：参数格式错误 " + preBind.toString(), preBinds);
//            }
//        }
//
//        //part 4:parse body部分（当成是一个begin表达式）
//        List<Node> statements = parseList(elements.subList(2, elements.size()));
//        int start = statements.get(0).start;
//        int end = statements.get(statements.size() - 1).end;
//        Begin body = new Begin(statements, start, end, tuple.line, tuple.col);
//        return new LetSyntax(binds, body, tuple.start, tuple.end, tuple.line, tuple.col);
//    }

}
