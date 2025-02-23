package com.ipr.patterns.abstractfactory;

public class WindowsButtonImpl implements Button {
    @Override
    public void click() {
        System.out.println("Windows button clicked");
    }
}
