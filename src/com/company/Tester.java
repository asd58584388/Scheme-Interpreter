package com.company;

import com.company.interpret.Closure;
import com.company.util.Util;
import com.company.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Tester
{
    String text;

    //当前文本的offset（offset只有在加了锁的readCases会用到，因此不需要再加锁，text也是）
    public int offset = 0;

    //用于标记当前测试的用例序号
    AtomicInteger index=new AtomicInteger(0);
    //或者
    //volatile int index=0;(不安全）

    public Tester(String filePath)
    {
        this.text = Util.readFile(filePath);
    }

    public static void main(String[] args)
    {
        Tester tester = new Tester(args[0]);
        tester.testAll();

    }

    public void testAll()
    {
        Thread test1 = new testThread();
        Thread test2 = new testThread();
        test1.start();
        test2.start();
    }

    private class testThread extends Thread
    {
        //存放一批测试用例
        String[] testCases;

        //每次读取的用例数
        public final int n = 5;

        //用于存放错误信息(用于输出)
        List<String> errors=new ArrayList<>();

        //用于存放警告信息(用于输出)
        List<String> warnings=new ArrayList<>();

        @Override
        public void run()
        {

            //这里不需要同步offset，因为就算得到的offset不同步，readCases必须是同步的，offset不会搞错，没必要加上volatile
            while (offset < text.length())
            {


                testCases = readCases(n);

                for (int i = 0; i < n; i++)
                {
                    //有一个为null，说明后面的都是null
                    if (testCases[i]==null)
                    {
                        break;
                    }

                    //用于记录错误信息
                    index.incrementAndGet();
                    Value value = null;
                    String result= "";

                    try
                    {

                        value = Interpreter.interpString(testCases[i]);

                        //出错情形
                        if (value==null)
                        {
                            result="用例 "+testCases[i]+" 出错：未能得到value     正确答案："+testCases[n+i];
                            errors.add(result);
                        }
                        //答案正确
                        else if (value.toString().equals(testCases[i+n]) || (value instanceof Closure && testCases[i + n].equals("closure")))
                        {
                        }
                        //未定义的结果（只要不报错）
                        else if (testCases[i+n].equals("UNDEFINE"))
                        {
                            result="用例 "+testCases[i]+" 警告：UNDEFINE答案";
                            warnings.add(result);
                        }
                        //得到错误答案
                        else
                        {
                            result="用例 "+testCases[i]+" 出错：得到错误的value "+value.toString()+"    正确答案："+testCases[n+i];
                            errors.add(result);
                        }

                    }
                    catch (Exception e)
                    {
                        String exceptionInfo=e.getMessage();

                        if (testCases[i+n]==null)
                        {
                            break;
                        }

                        //当e是空指针异常时，一般是解释程序的异常
                        if (e instanceof  NullPointerException)
                        {

                            result="用例 "+testCases[i]+" 警告：解释程序错误      正确答案："+testCases[n+i] ;
                            errors.add(result);
                            break;
                        }

                        //遇到预料中的错误
                        if (exceptionInfo.toLowerCase().contains(testCases[i + n]))
                        {
                            result="用例 "+testCases[i]+" 警告：堆栈溢出";
                            warnings.add(result);
                        }
                        //非常规错误
                        else
                        {
                            result="用例 "+testCases[i]+" 出错：得到错误的value "+exceptionInfo+"    正确答案："+testCases[n+i];
                            errors.add(result);
                        }
                    }
                }
            }

            //打印信息
            for (String error : errors)
            {
                System.out.println(getName()+":"+error);
            }
            for (String warning : warnings)
            {
                System.out.println(getName()+":"+warning);
            }
        }
    }

    public synchronized String[] readCases(int n)
    {
        //后面n个元素存放answer
        String[] batch = new String[2 * n];


        //跳过换行符，空格，注释
        skipCSN();

        for (int i = 0; i < n; i++)
        {
            String testCase = "";

            while (offset < text.length() && text.charAt(offset) == '/' && text.charAt(offset + 1) == '/')
            {
                offset += 2;

                int tempOffset = offset;

                // windows回车键是两个字符\r\n
                while (offset < text.length() && text.charAt(offset) != '\r')
                {
                    offset++;
                }

                if (text.charAt(tempOffset) == '=')
                {
                    batch[i + n] = text.substring(tempOffset + 1, offset);
                    break;
                }

                testCase=testCase.concat(text.substring(tempOffset, offset));

                offset+=2;
                skipComments();
                skipSpaces();
            }

            if (offset > text.length())
            {
                break;
            }
            else
            {
                skipCSN();
            }

            batch[i] = testCase;
        }

        return batch;
    }

    public boolean skipNextline()
    {
        boolean found = false;

        while (offset < text.length() && text.charAt(offset) == '\n')
        {
            found = true;
            offset++;
        }
        return found;
    }

    public boolean skipSpaces()
    {
        boolean found = false;
        //利用isWhitespace跳过所有一些无关符号
        while (offset < text.length() && (Character.isWhitespace(text.charAt(offset)) && text.charAt(offset) != '\n'))
        {
            found = true;
            offset++;
        }
        return found;
    }

    public boolean skipComments()
    {
        //标记，方便下面的跳过注释和空格
        boolean found = false;

        if (text.startsWith("//--", offset))
        {
            found = true;
            while (offset < text.length() && text.charAt(offset) != '\n')
            {
                offset++;
            }
            if (offset < text.length())
            {
                offset++;
            }
        }
        return found;
    }

    public void skipCSN()
    {
        while (skipComments() || skipSpaces() || skipNextline())
        {

        }
    }

}
