package com.ipr.patterns.singleton;

public class SingletonHandler {
    private SingletonHandler() {
    }

    public static class SingletonHolder {
        private static final SingletonHandler INSTANCE = new SingletonHandler();
    }

    public static SingletonHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
