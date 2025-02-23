package com.ipr.patterns.abstractfactory;

public class MacCheckBoxImpl implements CheckBox {
    @Override
    public void check() {
        System.out.println("MAC checkbox");
    }
}
