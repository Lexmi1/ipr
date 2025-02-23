package com.ipr.patterns.abstractfactory;

public class MacButtonImpl implements Button {
    @Override
    public void click() {
        System.out.println("I'm a mac button");
    }
}
