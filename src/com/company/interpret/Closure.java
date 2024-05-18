package com.company.interpret;

import com.company.ast.Lambda;
import com.company.value.Value;

public class Closure extends Value
{
    //闭包内的procedure
    public Lambda fun;
    //闭包内的环境
    public Environment env;

    public Closure(Lambda fun, Environment env)
    {
        this.fun = fun;
        this.env=env;

//        this.env = new Environment();
//        this.env.parent =env.parent;
//        this.env.table=
    }
}
