package com.jab125.configintermediary.mixin.owoconfig;

import io.wispforest.owo.config.ConfigWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConfigWrapper.class)
public interface ConfigWrapperAccessor {
    @Accessor
    <C> C getInstance();

    @Accessor
    <C> void setInstance(C newValue);
}
