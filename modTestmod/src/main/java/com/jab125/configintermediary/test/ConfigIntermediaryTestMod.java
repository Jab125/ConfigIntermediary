package com.jab125.configintermediary.test;

import com.jab125.configintermediary.api.value.ArrayConfigValue;
import com.jab125.configintermediary.test.config.OwoConfigTest;
import com.jab125.configintermediary.test.config.TestConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.jab125.configintermediary.ConfigIntermediary.configs;

public class ConfigIntermediaryTestMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("config-intermediary-test-mod");
    public static OwoConfigTest OWO_CONFIG;

    @Override
    public void onInitialize() {
        testAutoConfig();
        OWO_CONFIG = OwoConfigTest.createAndLoad();
    }

    private void testAutoConfig() {
        var config = AutoConfig.register(TestConfig.class, Toml4jConfigSerializer::new); // autoconfig
        var intermediary = configs.get("config-intermediary-test-mod"); // intermediary
        intermediary.registerSaveListener(((config1, data) -> {
            LOGGER.info("[INTERMEDIARY] Saved!");
            return ActionResult.PASS;
        }));
        config.registerSaveListener(((configHolder, testConfig) -> {
            LOGGER.info("[AutoConfig] Saved!");
            return ActionResult.PASS;
        }));
        LOGGER.info("[AutoConfig] a = " + Arrays.toString(config.get().a));
        LOGGER.info("[INTERMEDIARY] a = " + Arrays.toString(intermediary.get("a").getAs(String[].class)));
        LOGGER.info("[AutoConfig] a[0] = " + config.get().a[0]);
        LOGGER.info("[INTERMEDIARY] a[0] = " + ((ArrayConfigValue) intermediary.get("a")).get(0));
        ((ArrayConfigValue) intermediary.get("a")).set(0, "BEEP");
        LOGGER.info("[AutoConfig] a[0] = " + config.get().a[0]);
        LOGGER.info("[INTERMEDIARY] a[0] = " + ((ArrayConfigValue) intermediary.get("a")).get(0));
        intermediary.save();
    }
}
