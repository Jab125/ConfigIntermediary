package com.jab125.configintermediary.mixin.modmenu;

import com.terraformersmc.modmenu.config.option.EnumConfigOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnumConfigOption.class)
public interface EnumConfigOptionAccessor {
    @Accessor
    <E> Class<E> getEnumClass();
}
