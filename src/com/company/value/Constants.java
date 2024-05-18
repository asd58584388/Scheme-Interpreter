package com.company.value;

import java.util.Arrays;
import java.util.List;

/**
 * 定义了分隔符和关键字
 */
public class Constants
{
    public static final String COMMENT = "//" ;

    //程序开始与结束的括号
    public static final String PAREN_BEGIN = "(";
    public static final String PAREN_END = ")";

    //一些用于外部表示的符号 (abbreviation)
    public static final String QUASIQUOTE_ABBR="`";
    public static final String QUOTE_ABBR="'";
    public static final String VECTOR_ABBR="#";

    //标识符（identifier）
    public static final List<Character> IDENTIFIER_SPECIAL_INITIAL=
            Arrays.asList('!','$','%','&','*','/',':','<','=','>','?','^','_','~','.','+','-','@');
    public static final List<Character> IDENTIFIER_CHARS =
            Arrays.asList('!','$','%','&','*','/',':','<','=','>','?','^','_','~','+','-','@','.');



    //字符串
    public static final String STRING_START = "\"";
    public static final String STRING_END = "\"";
    public static final String STRING_ESCAPE = "\\";
    public static final char STRING_ESCAPE_CHAR='\\';


    //关键字
    public static final String BEGIN_KEYWORD = "begin";
    public static final String IF_KEYWORD = "if";
    public static final String DEF_KEYWORD = "define";
    public static final String ASSIGN_KEYWORD = "set!";
    public static final String LET_KEYWORD="let";
    public static final String LAMBDA_KEYWORD="lambda";
    public static final String COND_KEYWORD="cond";
    public static final String ELSE_KEYWORD="else";
    public static final String WHEN_KEYWORD="when";
    public static final String CASE_KEYWORD="case";
    public static final String LET_STAR_KEYWORD="let*";
    public static final String QUOTE_KEYWORD="quote";
    public static final String QUASIQUOTE_KEYWORD="quasiquote";
    public static final String LETREC_KEYWORD="letrec";
    public static final String DELAY_KEYWORD="delay";
    public static final String FORCE_KEYWORD="force";
    public static final String SET_CAR_KEYWORD="set-car!";
    public static final String SET_CDR_KEYWORD="set-cdr!";

    public static final String DOT_SPECIAL_TOKEN=".";
    public static final String ELLIPSIS_SPECIAL_TOKEN="...";

    //宏
    public static final String LET_SYNTAX_KEYWORD="let-syntax";
    public static final String SYNTAX_RULES_KEYWORD="syntax-rules";
    public static final String DEFINE_SYNTAX_KEYWORD="define-syntax";

    //类型
    public static final String INT_TYPE="int";
    public static final String DOUBLE_TYPE="double";
    public static final String BOOL_TYPE="bool";
    public static final String STRING_TYPE="string";

    //primitive function
    public static final String VECTOR_PRIMITIVE="vector";




    //用于产生中间代码
    //加载和存储
    public static String    OP_LOAD ="load";
    public static String    OP_STORE="store";
    //调用
    public static String    OP_CALL ="call";
    public static String    OP_RETURN ="return";
    //堆栈操作
    public static String    OP_PUSH ="push";
    public static String    OP_POP ="pop";
    //加减乘除
    public static String    OP_ADD ="add";
    public static String    OP_SUB ="sub";
    public static String    OP_MUL="mul";
    public static String    OP_DIV ="div";
    //比较
    public static String    OP_EQ ="eq";
    public static String    OP_LT ="lt";
    public static String    OP_LE ="le";
    public static String    OP_GT ="gt";
    public static String    OP_GE ="ge";

}
