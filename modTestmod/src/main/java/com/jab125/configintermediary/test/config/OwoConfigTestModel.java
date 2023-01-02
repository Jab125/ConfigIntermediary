package com.jab125.configintermediary.test.config;

import io.wispforest.owo.config.annotation.Config;

@SuppressWarnings("unused")
@Config(name = "config-intermediary-owo-config", wrapperName = "OwoConfigTest")
public class OwoConfigTestModel {
    public int anInt = 69;
    public String aString = "1-t5y";
    public String[] a = {"your mom", "lalalalalalala"};
}
