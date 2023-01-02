package com.jab125.configintermediary;

import com.jab125.configintermediary.api.value.Config;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConfigIntermediary implements ModInitializer {
    public static final Logger logger = LoggerFactory.getLogger("config-intermediary");
    public static Map<String, Config> configs = new HashMap<>();

    @Override
    @SuppressWarnings("internal")
    public void onInitialize() {
        logger.info("Initiating some cursed code...");
    }
}
