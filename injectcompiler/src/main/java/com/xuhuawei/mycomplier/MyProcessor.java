package com.xuhuawei.mycomplier;

import com.google.auto.service.AutoService;
import com.xuhuawei.myannotation.ViewInject;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by lingdian on 2017/11/1.
 */
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    //跟日志相关的辅助类
    private Messager messager;
    //获取元素相关的信息  主要是获取 包名的
    private Elements elementUtils;

    private Map<String, MyProxyInfo> mProxyMap = new HashMap<String, MyProxyInfo>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annoSet = new LinkedHashSet<>();
        annoSet.add(ViewInject.class.getCanonicalName());
        return annoSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        printNote("MyProcessor initialize!");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mProxyMap.clear();

        //遍历类注解元素
        for (Element e : roundEnv.getElementsAnnotatedWith(ViewInject.class)) {
            printNote("name : " + e.getSimpleName() + " value = "
                    + e.getAnnotation(ViewInject.class).value());
            if (e instanceof VariableElement) {
                VariableElement variableElement = (VariableElement) e;
                //代表类 的类型的注解  这里是Activity 或者Adapter
                TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
                //控件所在类的 类的全路径 这里是Activity 或者Adapter的全路径
                String fqClassName = classElement.getQualifiedName().toString();

                MyProxyInfo proxyInfo = mProxyMap.get(fqClassName);
                if (proxyInfo == null) {
                    PackageElement packageElement = elementUtils.getPackageOf(classElement);
                    //注解所在的类的包名
                    String packageName = packageElement.getQualifiedName().toString();
                    proxyInfo = new MyProxyInfo(packageName, classElement);
                    mProxyMap.put(fqClassName, proxyInfo);
                }

                //开始解析注解
                ViewInject injectAnnotation = variableElement.getAnnotation(ViewInject.class);
//              //注解 变量名称
//              String name = variableElement.getSimpleName().toString();
//              //注解 变量的类型
//              String type = variableElement.asType().toString();

                //注解的值
                int id = injectAnnotation.value();
                proxyInfo.setKeyAndValue(id, variableElement);


                String fullName = proxyInfo.getProxyClassFullName();
                printNote(fullName);
            }
        }

        for (String key : mProxyMap.keySet()) {
            MyProxyInfo proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();

                String generateCode = proxyInfo.generateHeadCode();
                printNote(generateCode);

                writer.write(generateCode);
                writer.flush();
                writer.close();
            } catch (IOException e) {

            }

        }
        return true;
    }


    private void printNote(String note) {
        //打印消息需要使用printMessage
        messager.printMessage(Diagnostic.Kind.NOTE, note);
    }
}
