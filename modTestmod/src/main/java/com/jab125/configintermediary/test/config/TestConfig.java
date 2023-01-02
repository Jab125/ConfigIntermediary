package com.jab125.configintermediary.test.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
@Config(name = "config-intermediary-test-mod")
public class TestConfig implements ConfigData {
    public boolean meowMeowImACow = true;
    public Identifier meow = new Identifier("lol", "lol");
    public String kekw = "kekW";
    public String[] a = new String[]{"a", "A", "AAAA"};
}
