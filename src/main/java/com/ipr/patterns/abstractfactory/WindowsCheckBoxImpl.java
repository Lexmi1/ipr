package com.ipr.patterns.abstractfactory;

public class WindowsCheckBoxImpl implements CheckBox {
    @Override
    public void check() {
        System.out.println("CheckBoxImpl.check()");
    }
}
