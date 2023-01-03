package com.jab125.configintermediary.mixin.configured;

import com.jab125.configintermediary.config.IntermediaryConfiguredConfig;
import com.mrcrayfish.configured.integration.ModMenuConfigFactory;
import com.terraformersmc.modmenu.ModMenu;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModMenu.class)
public class ForceConfiguredMenuMixin {

    @Inject(method = "getConfigScreen", at = @At("RETURN"), cancellable = true)
    private static void getConfigScreen(String modid, Screen menuScreen, CallbackInfoReturnable<Screen> cir) {
        if (!IntermediaryConfiguredConfig.forceConfiguredMenu) return;
        var d = new ModMenuConfigFactory().getProvidedConfigScreenFactories();
        if (d.containsKey(modid)) {
            cir.setReturnValue(d.get(modid).create(menuScreen));
        }
    }
}
