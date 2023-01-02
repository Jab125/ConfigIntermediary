package com.jab125.configintermediary.test;

import com.jab125.configintermediary.api.value.Config;
import com.jab125.configintermediary.test.config.OwoConfigTestModel;
import com.terraformersmc.modmenu.config.ModMenuConfig;

import java.util.Arrays;

import static com.jab125.configintermediary.test.ConfigIntermediaryTestMod.LOGGER;

public class ConfigRegistration implements com.jab125.configintermediary.api.entrypoint.ConfigRegistration {
    @Override
    public void onConfigRegistered(Config config) {
        if (config.getId().equals("modmenu")) {
            LOGGER.info(config.toString());
            LOGGER.info("[Mod Menu] compact_list = " + ModMenuConfig.COMPACT_LIST.getValue());
            LOGGER.info("[INTERMEDIARY] compact_list = " + config.get("compact_list").getAs(boolean.class));
            config.get("compact_list").set(true);
            config.save();
            LOGGER.info("[INTERMEDIARY] Saving...");
            LOGGER.info("[Mod Menu] compact_list = " + ModMenuConfig.COMPACT_LIST.getValue());
            LOGGER.info("[INTERMEDIARY] compact_list = " + config.get("compact_list").getAs(boolean.class));
            config.resetToDefault();
            LOGGER.info("[INTERMEDIARY] Resetting...");
            LOGGER.info("[Mod Menu] compact_list = " + ModMenuConfig.COMPACT_LIST.getValue());
            LOGGER.info("[INTERMEDIARY] compact_list = " + config.get("compact_list").getAs(boolean.class));
        } else if (config.getId().equals("owo-config-test")) {
            LOGGER.info("owo config test");
            LOGGER.info(config.toString());
            var de = config.getAs(OwoConfigTestModel.class);
            LOGGER.info(Arrays.toString(de.a));
            LOGGER.info(Arrays.toString(config.get("a").getAs(String[].class)));
            LOGGER.info(config.toString());
            LOGGER.info("[INTERMEDIARY] a[0] = " + config.get("a").getAsArrayConfigValue().get(0));
            LOGGER.info("[OwoConfig] a[0] = " + de.a[0]);
            config.get("a").getAsArrayConfigValue().get(0).set("IDIOT DUDE!!!!");
            LOGGER.info("[INTERMEDIARY] a[0] = " + config.get("a").getAsArrayConfigValue().get(0));
            LOGGER.info("[OwoConfig] a[0] = " + de.a[0]);
            config.save();
            LOGGER.info("[INTERMEDIARY] Saving...");
        }
    }
}
