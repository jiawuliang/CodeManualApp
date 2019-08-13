package com.amt.codetipsapp.modes.singletons;

/**
 * Created by liangjw on 2019/8/1.
 */

public class SingletonUtil {

    private static class InnerSingletonUtil {
        private static SingletonUtil instance = new SingletonUtil();
    }

    private SingletonUtil() {
        super();
    }

    public static SingletonUtil getInstance() {
        return InnerSingletonUtil.instance;
    }

}
