package com.ipr.patterns.abstractfactory;

public class WindowsAbstractImpl implements Abstract {
    @Override
    public Button createButton() {
        return new WindowsButtonImpl();
    }

    @Override
    public CheckBox createCheckBox() {
        return new WindowsCheckBoxImpl();
    }
}
