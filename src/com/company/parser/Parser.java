package com.company.parser;

import com.company.ast.*;
import com.company.lexer.Number;
import com.company.lexer.*;
import com.company.pre.MacroProcesser;
import com.company.value.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 语法分析器
 */
public class Parser
{
    Lexer lexer;

    public Parser(Lexer lexer)
    {
        this.lexer = lexer;
    }

    /**
     * parse过程，parse生成一个Begin Node的过程
     *
     * @return Node
     * @throws ParserException
     * @throws LexerException
     */
    public Node parse() throws ParserException, LexerException
    {
        //先preparse
        PreParser preParser = new PreParser(lexer);
        Node preAst = preParser.preparse();

        //再预处理
        MacroProcesser preProcess = new MacroProcesser(preAst);

        return parseNode(preProcess.preProcess());
    }

    /**
     * 在preparse进行第一次parse生成tuple之后，第二次parse生成最终ast
     *
     * @param preAst
     * @return Node
     */
    public Node parseNode(Node preAst) throws ParserException, LexerException
    {
        //Case 1：预parse生成的ast不是一个tuple即(...)，因此直接返回。
        if (!(preAst instanceof Tuple))
        {
            return preAst;
        }
        //Case 2：preAst是一个tuple (...)
        else
        {
            Tuple tuple = (Tuple) preAst;
            List<Node> nodes = ((Tuple) preAst).elements;
            //Case 2.1：节点为()
            if (nodes.isEmpty())
            {
//                throw new ParserException("语法错误：空的元组", tuple);
                //因为syntax-rules中可能有空tuple
                return tuple;
            }
            //Case 2.2：节点为(...)
            else if (tuple.shape.equals("("))
            {
                Node node = nodes.get(0);

                //Case 2.2.1: 节点为 (keyword ...)
                if (node instanceof Keyword)
                {
                    switch (((Keyword) node).name)
                    {
                        case Constants.BEGIN_KEYWORD:
                            return parseBegin(tuple);
                        case Constants.IF_KEYWORD:
                            return parseIf(tuple);
                        case Constants.DEF_KEYWORD:
                            return parseDefine(tuple);
                        case Constants.ASSIGN_KEYWORD:
                            return parseAssign(tuple);
                        case Constants.LAMBDA_KEYWORD:
                            return parseLambda(tuple);
                        case Constants.LET_KEYWORD:
                            return parseLet(tuple);
                        case Constants.COND_KEYWORD:
                            return parseCond(tuple);
                        case Constants.WHEN_KEYWORD:
                            return parseWhen(tuple);
                        case Constants.LET_STAR_KEYWORD:
                            return parseLetStar(tuple);
                        case Constants.CASE_KEYWORD:
                            return parseCase(tuple);
                        case Constants.QUOTE_KEYWORD:
                            return parseQuote(tuple);
                        case Constants.LETREC_KEYWORD:
                            return parseLetrec(tuple);
                        case Constants.DELAY_KEYWORD:
                            return parseDelay(tuple);
                        case Constants.FORCE_KEYWORD:
                            return parseForce(tuple);
                        case Constants.SET_CAR_KEYWORD:
                        case Constants.SET_CDR_KEYWORD:
                            return parseSetPair(tuple);
                        case Constants.DEFINE_SYNTAX_KEYWORD:
                            throw new ParserException("语法定义不能出现在表达式上下文中",node);
//                        case Constants.LET_SYNTAX_KEYWORD:
//                        case Constants.SYNTAX_RULES_KEYWORD:
                        default:
                            throw new ParserException("关键字没有匹配", node);
                    }
                }
                //Case 2.2.2：节点为调用 (Name ...) 或者 ((...) ...)
                else
                {
                    return parseCall(tuple);
                }
            }
            //case 2.3: 节点为[...]
            else
            {
                return parseTypedName(tuple);
            }

        }
    }


    public Name parseTypedName(Tuple tuple) throws ParserException
    {
        List<Node> elements = tuple.elements;
        if (elements.size()!=2)
        {
            throw new ParserException("参数数量不正确",tuple);
        }

        Identifier type= (Identifier) elements.get(0);
        Name name= (Name) elements.get(1);

        name.type=type.name;
        return name;
    }

    public SetPair parseSetPair(Tuple tuple) throws ParserException, LexerException
    {
        List<Node> elements = tuple.elements;

        if (elements.size() != 3)
        {
            throw new ParserException("参数数量不正确", tuple);
        }

        boolean isCar = false;
        Keyword set = (Keyword) elements.get(0);
        if (set.name.equals(Constants.SET_CAR_KEYWORD))
        {
            isCar = true;
        }

        Node var = parseNode(elements.get(1));
        if (!(var instanceof Name))
        {
            throw new ParserException("被赋值对象必须是identifier ", var);
        }

        Node value = parseNode(elements.get(2));
        return new SetPair(var, value, isCar, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    /**
     * delay负责加上一层lambda ()壳
     *
     * @param tuple
     * @return Lambda
     * @throws ParserException
     * @throws LexerException
     */
    public Lambda parseDelay(Tuple tuple) throws ParserException, LexerException
    {
        List<Node> elements = tuple.elements;
        if (elements.size() != 2)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        Node result = parseNode(elements.get(1));
        return new Lambda(null, result, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    /**
     * force就是脱掉lambda () ...这层壳
     *
     * @param tuple
     * @return Node
     * @throws ParserException
     * @throws LexerException
     */
    public Node parseForce(Tuple tuple) throws ParserException, LexerException
    {
        List<Node> elements = tuple.elements;
        if (elements.size() != 2)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        Node delay = parseNode(elements.get(1));

        //如果是lambda 表达式，就脱壳
        if (delay instanceof Lambda)
        {
            if (((Lambda) delay).parameters == null || ((Lambda) delay).parameters.size() == 0)
            {
                return ((Lambda) delay).body;
            }

        }

        //不是lambda表达式，返回parse之后的node
        return delay;
    }


    public Letrec parseLetrec(Tuple tuple) throws ParserException, LexerException
    {
        //part 1：判断参数数量是否正确
        List<Node> elements = tuple.elements;
        if (elements.size() < 3)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        //part 2：判断第一个参数（parameter）是否是元组
        Node preBinds = elements.get(1);
        if (!(preBinds instanceof Tuple))
        {
            throw new ParserException("语法错误：参数格式错误 " + preBinds.toString(), preBinds);
        }

        //part 3:parse第一个参数(tuple),分成两次parse这里很好处理
        List<Define> binds = new ArrayList<>();
        for (Node preBind : ((Tuple) preBinds).elements)
        {
            if (preBind instanceof Tuple)
            {
                binds.add(parseBind(preBind));
            }
            else
            {
                throw new ParserException("语法错误：参数格式错误 " + preBind.toString(), preBinds);
            }
        }

        //part 4:parse body部分（当成是一个begin表达式）
        List<Node> statements = parseList(elements.subList(2, elements.size()));
        int start = statements.get(0).start;
        int end = statements.get(statements.size() - 1).end;
        Begin body = new Begin(statements, start, end, tuple.line, tuple.col);
        return new Letrec(binds, body, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    public Quote parseQuote(Tuple tuple) throws ParserException
    {

        List<Node> elements = tuple.elements;
        if (elements.size() != 2)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        Node result = elements.get(1);

        //如果是tuple还需要继续parse生成
        //不需要parse了，留到interpret来生成pairvalue
//        if (result instanceof Tuple)
//        {
//            List<Node> nodes=((Tuple) result).elements;
//            result=new PairValue(nodes);
//
//        }

        return new Quote(result, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    public Case parseCase(Tuple tuple) throws ParserException, LexerException
    {
        //part 1：判断参数数量是否正确
        List<Node> elements = tuple.elements;
        if (elements.size() < 3)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        //part 2：判断key是否元组
        Node key = elements.get(1);
        if (key instanceof Tuple)
        {
            //key只是preAst，因此如果是tuple还需要进一步parse
            key = parseNode(key);
        }

        //part 3：移除掉Case 和key
        List<Node> preClauses = elements.subList(2, elements.size());

        //part 4:parse每条preClause
        List<Tuple> parsedClauses = new ArrayList<>();//没有必要，因为修改的是preClause内部的elements，但更容易理解
        //hasElse用于匹配是否在clause中的datum部分是否有else存在
        boolean hasElse = false;
        //Else返回的是consequnt部分，即表达式部分
        Begin Else = null;
        for (int i = 0; i < preClauses.size(); i++)
        {
            Node preClause = preClauses.get(i);

            //part 4.1：判定每条preClause
            if (!(preClause instanceof Tuple))
            {
                throw new ParserException("语法错误：case表达式中每条 clause必须是一个元组", preClause);
            }
            else
            {

                //part 4.2：判定preClause里的每条datum
                List<Node> clauseElements = ((Tuple) preClause).elements;
                Node datums = clauseElements.get(0);
                //datum部分必须是元组或者else
                if (datums instanceof Tuple)
                {
                    List<Node> datumList = ((Tuple) datums).elements;
                    for (Node datum : datumList)
                    {
                        if (!(datum instanceof Name) && !(datum instanceof Number))
                        {
                            throw new ParserException("语法错误：case表达式中每条clause里的datum如果是一个Tuple，则内部每个元素必须是Name或Number", datum);
                        }
                    }
                }
                else if (isElse(datums))
                {
                    if (i != preClauses.size() - 1)
                    {
                        throw new ParserException("语法错误：case表达式中的else必须在最后一条clause中", datums);
                    }
                    else
                    {
                        hasElse = true;
                        List<Node> preExpressions = clauseElements.subList(1, clauseElements.size());
                        List<Node> parsedExpressions = parseList(preExpressions);
                        int start = preExpressions.get(0).start;
                        int end = preExpressions.get(preExpressions.size() - 1).end;
                        Else = new Begin(parsedExpressions, start, end, preClause.line, preClause.col);
                        break;
                    }
                }
                else
                {
                    throw new ParserException("语法错误：case表达式中每条clause里的datum必须是元组或else", datums);
                }

                //part 4.3：preClause里的datum经过判定后，parse expressions 生成一个begin node
                List<Node> preExpressions = clauseElements.subList(1, clauseElements.size());
                List<Node> parsedExpressions = parseList(preExpressions);

                int start = preExpressions.get(0).start;
                int end = preExpressions.get(preExpressions.size() - 1).end;

                Begin expressions = new Begin(parsedExpressions, start, end, preClause.line, preClause.col);


                //part 4.4：将生成的begin node加入到parsedClause （或者直接修改precluse的elements(修改引用)）
                List<Node> parsedClause = new ArrayList<>();
                parsedClause.add(datums);
                parsedClause.add(expressions);

//                ((Tuple) preClause).elements=parsedClause;
                ((Tuple) preClause).elements = parsedClause;
                parsedClauses.add((Tuple) preClause);

            }
        }

        return new Case(key, parsedClauses, hasElse, Else, tuple.start, tuple.end, tuple.line, tuple.col);

    }

    //和let一样，只是返回的值不同
    public LetStar parseLetStar(Tuple tuple) throws LexerException, ParserException
    {
        //part 1：判断参数数量是否正确
        List<Node> elements = tuple.elements;
        if (elements.size() < 3)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        //part 2：判断第一个参数（parameter）是否是元组
        Node preBinds = elements.get(1);
        if (!(preBinds instanceof Tuple))
        {
            throw new ParserException("语法错误：参数格式错误 " + preBinds.toString(), preBinds);
        }

        //part 3:parse第一个参数(tuple),分成两次parse这里很好处理
        List<Define> binds = new ArrayList<>();
        for (Node preBind : ((Tuple) preBinds).elements)
        {
            if (preBind instanceof Tuple)
            {
                binds.add(parseBind(preBind));
            }
            else
            {
                throw new ParserException("语法错误：参数格式错误 " + preBind.toString(), preBinds);
            }
        }

        //part 4:parse body部分（当成是一个begin表达式）
        List<Node> statements = parseList(elements.subList(2, elements.size()));
        int start = statements.get(0).start;
        int end = statements.get(statements.size() - 1).end;
        Begin body = new Begin(statements, start, end, tuple.line, tuple.col);
        return new LetStar(binds, body, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    private When parseWhen(Tuple tuple) throws ParserException, LexerException
    {
        //part 1：判断参数数量是否正确
        List<Node> elements = tuple.elements;
        if (elements.size() != 3)
        {
            throw new ParserException("语法错误：参数数量小于 2 个", tuple);
        }

        //part 2：parse
        List<Node> nodes = tuple.elements;
        Node boolExpr = parseNode(nodes.get(1));
        Node consequent = parseNode(nodes.get(2));

        return new When(boolExpr, consequent, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    public Condition parseCond(Tuple tuple) throws ParserException, LexerException
    {
        //part 1：判断参数数量是否正确
        List<Node> elements = tuple.elements;
        if (elements.size() < 2)
        {
            throw new ParserException("语法错误：参数数量小于 2 个", tuple);
        }

        //part 2：移除掉cond element，对剩下的每一个condition进行parse,生成when节点，加入到list中
        List<When> conditions = new ArrayList<>();
        boolean hasElse = false;
        Node Else = null;
        elements.remove(0);
        Node element = null;
        for (int i = 0; i < elements.size(); i++)
        {
            element = elements.get(i);

            if (element instanceof Tuple)
            {
                List<Node> nodes = ((Tuple) element).elements;
                if (nodes.size() != 2)
                {
                    throw new ParserException("语法错误：参数需要 2 个，却有 " + nodes.size() + " 个", element);
                }

                Node boolExpr = parseNode(nodes.get(0));
                if (isElse(boolExpr))
                {
                    if (i != elements.size() - 1)
                    {
                        throw new ParserException("else在cond表达式中必须在最后", element);
                    }
                    else
                    {
                        hasElse = true;
                        Else = parseNode(nodes.get(1));
                        break;
                    }
                }

                Node consequent = parseNode(nodes.get(1));
                When condition = new When(boolExpr, consequent, element.start, element.end, element.line, element.col);
                conditions.add(condition);

            }
            else
            {
                throw new ParserException("语法错误：参数错误", element);
            }
        }

        return new Condition(conditions, hasElse, Else, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    public boolean isElse(Node node)
    {
        if (node instanceof Keyword)
        {
            return ((Keyword) node).name.equals(Constants.ELSE_KEYWORD);
        }
        else
        {
            return false;
        }
    }

    public Lambda parseLambda(Tuple tuple) throws ParserException, LexerException
    {
        //part 1：判断参数数量是否正确
        List<Node> elements = tuple.elements;
        if (elements.size() != 3)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        //part 2：判断第一个参数（parameter）是否是元组
        Node preParams = elements.get(1);
        if (!(preParams instanceof Tuple))
        {
            throw new ParserException("语法错误：参数格式错误 " + preParams.toString(), preParams);
        }

        //part 3:parse第一个参数(tuple)
        List<Name> paramNames = new ArrayList<>();
        List<Node> tempParams = ((Tuple) preParams).elements;
        Identifier hasList = null;
        for (int i = 0; i < tempParams.size(); i++)
        {
            Node Param = tempParams.get(i);
            if (Param instanceof Name)
            {
                paramNames.add((Name) Param);
            }
            //可变参数列表
            else if (i == tempParams.size() - 2 && i != 0 && Param instanceof Keyword && ((Keyword) Param).name.equals(Constants.DOT_SPECIAL_TOKEN))
            {
                // “.” 之后的参数必须是identifier
                Node nextParam = tempParams.get(i + 1);
                if (nextParam instanceof Identifier)
                {
                    hasList = (Identifier) nextParam;
                }
                else
                {
                    throw new ParserException("语法错误：参数格式错误 " + nextParam.toString(), preParams);
                }

                break;
            }
            else
            {
                throw new ParserException("语法错误：参数格式错误 " + Param.toString(), preParams);
            }
        }

        //part 4:parse body部分（当成是一个begin表达式）
        List<Node> statements = parseList(elements.subList(2, elements.size()));
        int start = statements.get(0).start;
        int end = statements.get(statements.size() - 1).end;
        Begin body = new Begin(statements, start, end, tuple.line, tuple.col);

        //固定参数
        if (hasList == null)
        {
            return new Lambda(paramNames, body, tuple.start, tuple.end, tuple.line, tuple.col);
        }
        //可变参数
        else
        {
            return new Lambda(paramNames, body, hasList, tuple.start, tuple.end, tuple.line, tuple.col);
        }
    }

    public Let parseLet(Tuple tuple) throws LexerException, ParserException
    {
        //part 1：判断参数数量是否正确
        List<Node> elements = tuple.elements;
        if (elements.size() < 3)
        {
            throw new ParserException("语法错误：参数数量不正确", tuple);
        }

        //part 2：判断第一个参数（parameter）是否是元组
        Node preBinds = elements.get(1);
        if (!(preBinds instanceof Tuple))
        {
            throw new ParserException("语法错误：参数格式错误 " + preBinds.toString(), preBinds);
        }

        //part 3:parse第一个参数(tuple),分成两次parse这里很好处理
        List<Define> binds = new ArrayList<>();
        for (Node preBind : ((Tuple) preBinds).elements)
        {
            if (preBind instanceof Tuple)
            {
                binds.add(parseBind(preBind));
            }
            else
            {
                throw new ParserException("语法错误：参数格式错误 " + preBind.toString(), preBinds);
            }
        }

        //part 4:parse body部分（当成是一个begin表达式）
        List<Node> statements = parseList(elements.subList(2, elements.size()));
        int start = statements.get(0).start;
        int end = statements.get(statements.size() - 1).end;
        Begin body = new Begin(statements, start, end, tuple.line, tuple.col);
        return new Let(binds, body, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    public Define parseBind(Node preBind) throws ParserException, LexerException
    {
        if (!(preBind instanceof Tuple))
        {
            throw new ParserException("语法错误：参数格式错误 " + preBind.toString(), preBind);
        }
        else
        {
            Tuple tuple = (Tuple) preBind;
            List<Node> elements = tuple.elements;
            if (elements.size() != 2)
            {
                throw new ParserException("参数数量不正确", preBind);
            }

            Node var = parseNode(elements.get(0));
            Node value = parseNode(elements.get(1));
            if (!(var instanceof  Identifier))
            {
                throw new ParserException("let语句中的binding中的第一个参数必须是identifier",var);
            }

            return new Define((Identifier) var, value, tuple.start, tuple.end, tuple.line, tuple.col);
        }

    }

    public Begin parseBegin(Tuple tuple) throws LexerException, ParserException
    {
        List<Node> elements = tuple.elements;
        if (elements.size() < 2)
        {
            throw new ParserException("参数数量不正确", tuple);
        }
        List<Node> statements = parseList(elements.subList(1, elements.size()));
        return new Begin(statements, tuple.start, tuple.end, tuple.line, tuple.col);
    }

    public Assign parseAssign(Tuple tuple) throws LexerException, ParserException
    {
        List<Node> elements = tuple.elements;
        if (elements.size() != 3)
        {
            throw new ParserException("参数数量不正确", tuple);
        }
        Node var = parseNode(elements.get(1));
        if (!(var instanceof Name))
        {
            throw new ParserException("被赋值对象必须是identifier ", var);
        }

        Node value = parseNode(elements.get(2));
        return new Assign((Name) var, value, tuple.start, tuple.end, tuple.line, tuple.col);

    }

    public Call parseCall(Tuple tuple) throws ParserException, LexerException
    {
        List<Node> elements = tuple.elements;
        Node op = parseNode(elements.get(0));
        List<Node> parsedArgs = parseList(elements.subList(1, elements.size()));
        Argument args = new Argument(parsedArgs);
        return new Call(op, args, tuple.start, tuple.end, tuple.line, tuple.col);
    }


    public List<Node> parseList(List<Node> nodes) throws ParserException, LexerException
    {
        List<Node> parsed = new ArrayList<>();
        for (Node s : nodes)
        {
            parsed.add(parseNode(s));
        }
        return parsed;
    }


    public Define parseDefine(Tuple tuple) throws LexerException, ParserException
    {
        List<Node> elements = tuple.elements;
        if (elements.size() != 3)
        {
            throw new ParserException("参数数量不正确", tuple);
        }
        Node var = parseNode(elements.get(1));
        if (!(var instanceof  Identifier))
        {
            throw new ParserException("define语句中的第一个参数必须是identifier",var);
        }
        Node value = parseNode(elements.get(2));
        return new Define((Identifier) var, value, tuple.start, tuple.end, tuple.line, tuple.col);

    }

    public If parseIf(Tuple tuple) throws LexerException, ParserException
    {
        List<Node> elements = tuple.elements;
        if (elements.size() != 4)
        {
            throw new ParserException("参数数量不正确", tuple);
        }
        Node judge = parseNode(elements.get(1));
        Node t = parseNode(elements.get(2));
        Node f = parseNode(elements.get(3));
        return new If(judge, t, f, tuple.start, tuple.end, tuple.line, tuple.col);
    }


}
