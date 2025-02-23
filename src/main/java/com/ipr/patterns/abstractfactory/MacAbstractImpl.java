package com.ipr.patterns.abstractfactory;

public class MacAbstractImpl implements Abstract {
    @Override
    public Button createButton() {
        return new MacButtonImpl();
    }

    @Override
    public CheckBox createCheckBox() {
        return new MacCheckBoxImpl();
    }
}
