package com.company;


import com.company.ast.Node;
import com.company.lexer.Lexer;
import com.company.lexer.LexerException;
import com.company.parser.ParserException;
import com.company.parser.Parser;
import com.company.util.Util;
import com.company.interpret.Environment;
import com.company.value.Value;

import java.util.EventListener;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter
{
    public String filePath;
    public String unifyPath;
    public String text;

    public Interpreter(String filePath)
    {
        this.filePath = filePath;
    }

    /**
     * 解释过程（文件输入）
     */
    public Value interpFile(String filePath)
    {
        Node program;

        //读取文件
        this.unifyPath = Util.unifyPath(filePath);
        this.text = Util.readFile(unifyPath);

        if (text == null)
        {
            Util.abort("读取文件失败" + unifyPath);
        }

        Lexer lexer = new Lexer(text);

        try
        {
            Parser parser = new Parser(lexer);
            program = parser.parse();
            return program.interp(Environment.BuildInitEnv());
        }
        catch (LexerException le)
        {
            Util.abort("词法分析过程出错：" + le);
            return null;
        }
        catch (ParserException pe)
        {
            Util.abort("语法分析过程出错：" + pe);
            return null;
        }
    }

    /**
     * 解释过程（字符串输入）
     */
    public static Value interpString(String text) throws LexerException, ParserException
    {

        if (text == null)
        {
            Util.abort("输入的字符串为空");
        }

        Lexer lexer = new Lexer(text);

        Node program;
        Parser parser = new Parser(lexer);
        program = parser.parse();
        return program.interp(Environment.BuildInitEnv());


    }


    /**
     * 解释过程（命令行输入）
     *
     * @param text
     * @return
     */
    public static Value interpCMD(String text, Environment env)
    {
        if (text == null)
        {
            Util.abort("输入的字符串为空");
            return null;
        }

        Lexer lexer = new Lexer(text);

        Node program;
        Parser parser = new Parser(lexer);
        try
        {
            program = parser.parse();
            return program.interp(env);
        }
        catch (LexerException le)
        {
            Util.abort("词法分析过程出错：" + le);
            return null;
        }
        catch (ParserException pe)
        {
            Util.abort("语法分析过程出错：" + pe);
            return null;
        }
    }

    //只是程序入口，并没有实例化本类
//    public static void main(String[] args)
//    {
//        System.out.println("请输入序号");
//        System.out.println("1.命令行输入程序代码");
//        System.out.println("2.文本输入代码 -文本路径");
//
//        Scanner input = new Scanner(System.in);
//        int i = input.nextInt();
//
//        if (i == 1)
//        {
//            System.out.print("> ");
//            String text = "";
//            Environment env = Environment.BuildInitEnv();
//            while (true)
//            {
//                String tamp = input.next();
//
//                text = text + tamp + " ";
//
//
//                Pattern pattern = Pattern.compile("([\\s]*\\(.*\\)[\\s]*)|^(?!\\().*", Pattern.DOTALL);
//                Matcher matcher = pattern.matcher(text);
//                if (matcher.matches())
//                {
//                    text = "";
//                    Value res = interpCMD(text, env);
//                    if (res == null)
//                    {
//                        System.out.println();
//                        continue;
//                    }
//                    Util.printMsg(res.toString());
//                    System.out.print("> ");
//
//                }
//
//
//            }
//        }
//        else
//        {
//            String filePath;
//            filePath = input.next();
//            Interpreter interpreter = new Interpreter(filePath);
//            Util.printMsg(interpreter.interpFile(filePath).toString());
//        }
//
//        input.close();
//    }

    public static void main(String[] args)
    {
        Interpreter interpreter = new Interpreter(args[0]);
        Util.printMsg(interpreter.interpFile(args[0]).toString());
    }
}
