package com.ipr.patterns.abstractfactory;

public class AbstractFactoryExample {
    public static void main(String[] args) {
        Abstract factory;

        // Подбираем фабрику в зависимости от ОС
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            factory = new WindowsAbstractImpl();
        } else {
            factory = new MacAbstractImpl();
        }

        Application app = new Application(factory);
        factory.createButton().click();
        app.run();
    }
}
