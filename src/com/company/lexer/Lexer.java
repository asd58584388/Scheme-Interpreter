package com.company.lexer;

import com.company.ast.Case;
import com.company.ast.Node;
import com.company.value.Constants;


public class Lexer
{
    //读入的文件
    public String text;

    //当前的位置
    public int line;
    public int col;
    public int offset;

    public Lexer(String text)
    {
        this.text=text;
        this.line = 0;
        this.col = 0;
        this.offset = 0;
    }


    //不检查文件是否末尾，否则太多调用，留到skipCommentsAndSpaces来检查
    public void forward()
    {
        if (text.charAt(offset) == '\n')
        {
            line++;
            col = 0;
            offset++;
        }
        else
        {
            col++;
            offset++;
        }
    }

    public boolean skipSpaces()
    {
        boolean found = false;
        //利用isWhitespace跳过所有一些无关符号
        while (offset < text.length() && Character.isWhitespace(text.charAt(offset)))
        {
            found = true;
            forward();
        }
        return found;
    }

    public boolean skipComments()
    {
        //标记，方便下面的跳过注释和空格
        boolean found = false;

        if (text.startsWith(Constants.COMMENT, offset))
        {
            found = true;

            while (offset < text.length() && text.charAt(offset) != '\n')
            {
                forward();
            }
            if (offset < text.length())
            {
                forward();
            }
        }
        return found;
    }

    public void skipCommentsAndSpaces()
    {
        while (skipComments() || skipSpaces())
        {

        }
    }

    public Node nextToken() throws LexerException
    {
        skipCommentsAndSpaces();

        if (offset >= text.length())
        {
            return null;
        }

        //先得到字符
        char ch = text.charAt(offset);

        //第一种情况,先判断分隔符
        if (Delimiter.isDelimiter(ch))
        {
            Node delim = new Delimiter(Character.toString(ch), offset, offset + 1, line, col);
            forward();
            return delim;
        }
        //第二种情况，数字
        else if (Character.isDigit(ch) || ((text.charAt(offset) == '+' || text.charAt(offset) == '-') && offset + 1 < text.length() && Character.isDigit(text.charAt(offset + 1))))
        {
            return scanNumber();
        }
        //第三种情况，字符串
        else if (text.startsWith(Constants.STRING_START,offset))
        {
            return scanString();
        }
        //第四种情况,标识符
        else if (Character.isLetter(ch) || Constants.IDENTIFIER_SPECIAL_INITIAL.contains(ch))
        {
            return scanNameOrKeywordOrMacro();
        }
        //其它，语法错误
        else
        {
            throw new LexerException("得到Token失败：语法错误", line, col, offset);
        }

    }


    public Str scanString() throws LexerException {
        int start = offset;
        int startLine = line;
        int startCol = col;
        skip(Constants.STRING_START.length());

        while (true) {
            // offset超出文本或者遇到换行符
            if (offset >= text.length() || text.charAt(offset) == '\n') {
                throw new LexerException("词法错误：字符串遇到换行符", startLine, startCol, offset);
            }

            //字符串结束
            else if (text.startsWith(Constants.STRING_END, offset)) {
                skip(Constants.STRING_END.length());
                break;
            }

            //遇到转义字符
            else if (text.startsWith(Constants.STRING_ESCAPE, offset) && offset + 1 < text.length()) {
                skip(Constants.STRING_ESCAPE.length() + 1);
            }

            //其它
            else {
                forward();
            }
        }

        int end = offset;
        String content = text.substring(
                start + Constants.STRING_START.length(),
                end - Constants.STRING_END.length());

        return new Str(content, start, end, startLine, startCol);
    }

    public void skip(int n) {
        for (int i = 0; i < n; i++) {
            forward();
        }
    }


    public static boolean isIdentifierChar(char ch)
    {
        return Character.isLetterOrDigit(ch) || Constants.IDENTIFIER_CHARS.contains(ch);
    }

    public Node scanNameOrKeywordOrMacro() throws LexerException
    {
        int start = offset;
        int startLine = line;
        int startCol = col;

        while (true)
        {
            //文件结束
            if (offset >= text.length())
            {
                break;
//                throw new LexerException("读取identifier失败：超出文本长度", startLine, startCol, start);
            }

            char ch = text.charAt(offset);

            //是否空格
            if (Character.isWhitespace(ch))
            {
                break;
            }
            //是否允许的字符
            else if (isIdentifierChar(ch))
            {
                forward();
            }
            //close分隔符
            else if (Delimiter.isDelimiter(ch))
            {
                break;
            }
            //错误字符（除允许字符外）
            else
            {
                throw new LexerException("读取identifier失败：遇见错误字符", startLine, startCol, start);
            }

        }

        int end = offset;
        String identifier = text.substring(start, end);
        //这里判断是否是关键字，对Identifier进行分类
        if (isKeyword(identifier))
        {
            return new Keyword(identifier, start, end, startLine, startCol);
        }
        else if (isMacro(identifier))
        {
            return new Macro(identifier, start, end, startLine, startCol);
        }
        else
        {
            return new Name(identifier,start,end,startLine,startCol);
        }

    }

    public static boolean isMacro(String identifier)
    {
        switch (identifier)
        {
            case Constants.LET_SYNTAX_KEYWORD:
            case Constants.DEFINE_SYNTAX_KEYWORD:
            case Constants.SYNTAX_RULES_KEYWORD:
                return true;
            default:
                return false;
        }
    }

    public static boolean isKeyword(String identifier)
    {
        switch (identifier)
        {
            case Constants.IF_KEYWORD:
            case Constants.DEF_KEYWORD:
            case Constants.ASSIGN_KEYWORD:
            case Constants.LET_KEYWORD:
            case Constants.BEGIN_KEYWORD:
            case Constants.LAMBDA_KEYWORD:
            case Constants.COND_KEYWORD:
            case Constants.WHEN_KEYWORD:
            case Constants.ELSE_KEYWORD:
            case Constants.CASE_KEYWORD:
            case Constants.LET_STAR_KEYWORD:
            case Constants.QUOTE_KEYWORD:
            case Constants.LETREC_KEYWORD:
            case Constants.DELAY_KEYWORD:
            case Constants.FORCE_KEYWORD:
            case Constants.SET_CAR_KEYWORD:
            case Constants.SET_CDR_KEYWORD:
            case Constants.DOT_SPECIAL_TOKEN:
            case Constants.INT_TYPE:
            case Constants.DOUBLE_TYPE:
                return true;
            default:
                return false;
        }
    }


    public static boolean isNumberChar(char c)
    {
        return Character.isDigit(c) || c == '.' || c=='+' || c== '-';
    }

    public Node scanNumber()
    {
        int start = offset;
        int startLine = line;
        int startCol = col;

        while (offset < text.length() && isNumberChar(text.charAt(offset)))
        {
            forward();
        }

        String value = text.substring(start, offset);
        return new Number(value, start, offset, startLine, startCol);
    }


}
