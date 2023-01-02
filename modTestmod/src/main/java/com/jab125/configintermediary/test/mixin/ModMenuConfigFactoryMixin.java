package com.jab125.configintermediary.test.mixin;

import com.mrcrayfish.configured.integration.ModMenuConfigFactory;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = ModMenuConfigFactory.class, remap = false)
public class ModMenuConfigFactoryMixin {
    @Inject(method = "getProvidedConfigScreenFactories", at = @At("RETURN"))
    private void a(CallbackInfoReturnable<Map<String, ConfigScreenFactory<?>>> cir) {
        cir.getReturnValue().forEach(((s, configScreenFactory) -> {
            System.out.println(s);
            System.out.println(configScreenFactory);
        }));
    }
}
