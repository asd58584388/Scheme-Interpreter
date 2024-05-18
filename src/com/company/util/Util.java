package com.company.util;


import com.company.ast.Node;
import com.company.value.Value;
import com.company.value.num.DoubleValue;
import com.company.value.num.IntValue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 工具类
 */
public class Util
{

    //nio读取文件，返回字符串
    public static String readFile( String filePath)
    {
        try
        {
            //io
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            return Charset.forName("UTF-8").decode(ByteBuffer.wrap(encoded)).toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void abort(Node loc, String msg) {
        System.err.println(loc.getLineCol() + " " + msg);
        System.err.flush();
        Thread.dumpStack();
//        System.exit(1);
    }

    //在终端输出信息
    public static void printMsg(String msg)
    {
        System.out.println(msg);
    }

    //返回规范路径
    public static  String unifyPath(String filePath)
    {
        File file=new File(filePath);
        try
        {
            return file.getCanonicalPath();
        }
        catch (IOException e)
        {
            abort("获取规范路径失败");
            return null;
        }
    }


    //程序终止处理
    public static void abort(String m)
    {
        System.err.println(m);
        System.err.flush();
        Thread.dumpStack();
//        System.exit(1);
    }



}
