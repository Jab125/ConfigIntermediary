package com.jab125.configintermediary.mixin.autoconfig;

import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.annotation.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ConfigManager.class)
public interface ConfigManagerAccessor {
    @Accessor
    Config getDefinition();
}
