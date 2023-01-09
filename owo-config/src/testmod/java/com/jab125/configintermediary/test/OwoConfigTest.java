package com.jab125.configintermediary.test;

import com.jab125.configintermediary.test.config.OwoConfigTestConfig;
import net.fabricmc.api.ModInitializer;

public class OwoConfigTest implements ModInitializer {
    @Override
    public void onInitialize() {
        OwoConfigTestConfig.createAndLoad();
    }
}
