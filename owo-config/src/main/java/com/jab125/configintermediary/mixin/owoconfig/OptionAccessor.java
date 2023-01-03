package com.jab125.configintermediary.mixin.owoconfig;

import io.wispforest.owo.config.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Option.class)
public interface OptionAccessor {
    @Accessor
    <T> Option.BoundField<T> getBackingField();
}
