package com.lingdian.injectapi;

import android.app.Activity;
import android.view.View;

import com.xuhuawei.myannotation.interfaces.InterViewInject;
import com.xuhuawei.myannotation.MyConfig;


/**
 * Created by zhy on 16/4/22.
 */
public class ViewInjector
{


    public static void injectView(Activity activity)
    {
        InterViewInject proxyActivity = findProxyActivity(activity);
        proxyActivity.inject(activity, activity);
    }

    public static void injectView(Object object, View view)
    {
        InterViewInject proxyActivity = findProxyActivity(object);
        proxyActivity.inject(object, view);
    }

    private static InterViewInject findProxyActivity(Object activity)
    {
        try
        {
            Class clazz = activity.getClass();
            Class injectorClazz = Class.forName(clazz.getName() + MyConfig.SUFFIX);
            InterViewInject obj=(InterViewInject) injectorClazz.newInstance();
            return obj;
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s , something when compiler.", activity.getClass().getSimpleName() + MyConfig.SUFFIX));
    }
}
