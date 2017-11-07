package com.xuhuawei.myannotation;

import com.xuhuawei.myannotation.interfaces.InterViewInject;

public class MyConfig {
    //代理类的名字
    public static final String PROXY = "ProxyViewInject";
    //我们生成的代理 必须实现相同的接口，这是接口的名称（没有包名）
    public static final String PROXY_ITERFACE = InterViewInject.class.getSimpleName();
    //我们生成的代理 必须实现相同的接口，这是接口的全路径
    public static final String PROXY_ITERFACE_FULL = InterViewInject.class.getName();
    //生成的代理类名称后缀
    public static final String SUFFIX = "$$"+PROXY;

}
