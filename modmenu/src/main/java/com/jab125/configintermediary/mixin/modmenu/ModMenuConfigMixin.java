package com.jab125.configintermediary.mixin.modmenu;

import com.jab125.configintermediary.api.entrypoint.ConfigRegistration;
import com.jab125.configintermediary.compat.ModMenuCompat;
import com.terraformersmc.modmenu.ModMenu;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModMenu.class, remap = false)
public class ModMenuConfigMixin {
    @Inject(method = "onInitializeClient", at = @At(value = "INVOKE", target = "Lcom/terraformersmc/modmenu/config/ModMenuConfigManager;initializeConfig()V", shift = At.Shift.AFTER))
    private void configIntermediary$clinit(CallbackInfo ci) {
        var l = ModMenuCompat.register();
        for (ConfigRegistration entrypoint : FabricLoader.getInstance().getEntrypoints("config-intermediary:registration", ConfigRegistration.class)) {
            entrypoint.onConfigRegistered(l);
        }
    }
}
