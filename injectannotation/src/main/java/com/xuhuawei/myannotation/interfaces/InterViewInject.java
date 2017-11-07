package com.xuhuawei.myannotation.interfaces;

/**
 * Created by lingdian on 2017/11/7.
 */

public interface InterViewInject<T> {
    void inject(T t, Object source);
}
