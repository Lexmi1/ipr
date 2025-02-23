package com.ipr.patterns.abstractfactory;

import java.awt.*;

public class Application {
    private final Button button;
    private final CheckBox checkbox;

    public Application(Abstract factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckBox();
    }

    public void run() {
        button.click();
        checkbox.check();
    }
}

