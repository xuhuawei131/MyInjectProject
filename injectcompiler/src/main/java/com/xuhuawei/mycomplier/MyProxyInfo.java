package com.xuhuawei.mycomplier;

import com.xuhuawei.myannotation.MyConfig;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * 生成代理类代码的 bean
 * 一个MyProxyInfo就封装了一个Activity或者ViewHolder
 * injectVaries集合 包含了里面的注解，
 * 包含了 注解的值 以及他的变量 变量的类型
 */
public class MyProxyInfo {
    //包名
    public String packageStr;
    //没有包名的类名， 这是我们生成的代理类的名字
    private String proxyClassName;
    //代表类 的类型的注解  这里是Activity 或者Adapter中的viewHolder
    private TypeElement typeElement;
    //记录R.id.值
    private Map<Integer,VariableElement> injectVaries;

    /**
     * @param packageStr
     * @param classElement
     */
    public MyProxyInfo(String packageStr,TypeElement classElement){
        this.packageStr=packageStr;
        this.typeElement=classElement;
        this.injectVaries=new HashMap<>();
        this.proxyClassName = typeElement.getSimpleName() +MyConfig.SUFFIX;
    }

    /**
     * setKeyAndValue把容器里面的变量 都放到map集合里面
     * @param resId view的id值
     * @param variableElement view的变量值
     */
    public void setKeyAndValue(int resId,VariableElement variableElement){
        injectVaries.put(resId,variableElement);
    }

    /**
     * 产生头部的java代码
     *
     * @return
     */
    public String generateHeadCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n");
        builder.append("package ").append(packageStr).append(";\n\n");
        builder.append('\n');

        builder.append("public class ").append(proxyClassName).append(" implements " + MyConfig.PROXY_ITERFACE_FULL + "<" + typeElement.getQualifiedName() + ">");
        builder.append(" {\n");

        generateMethodsCode(builder);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }

    /**
     * 产生具体的方法
     *
     * @param builder
     */
    private void generateMethodsCode(StringBuilder builder) {
        builder.append("@Override\n ");
        builder.append("public void inject(" + typeElement.getQualifiedName() + " host, Object source ) {\n");

        for (int id : injectVaries.keySet()) {
            VariableElement element = injectVaries.get(id);
            //注解 变量名称
            String name = element.getSimpleName().toString();
            //注解 变量的类型
            String type = element.asType().toString();

            builder.append(" if(source instanceof android.app.Activity){\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)source).findViewById( " + id + "));\n");
            builder.append("\n}else{\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.view.View)source).findViewById( " + id + "));\n");
            builder.append("\n};");


        }
        builder.append("  }\n");


    }

    public String getProxyClassFullName() {
        return packageStr + "." + proxyClassName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

}
