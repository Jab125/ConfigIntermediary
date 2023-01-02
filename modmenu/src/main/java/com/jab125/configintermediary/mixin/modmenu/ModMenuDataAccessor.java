package com.jab125.configintermediary.mixin.modmenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(targets = "com.terraformersmc.modmenu.util.mod.fabric.FabricMod$ModMenuData")
public interface ModMenuDataAccessor {
    @Accessor
    Optional<String> getParent();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Accessor
    void setParent(Optional<String> parent);
}
