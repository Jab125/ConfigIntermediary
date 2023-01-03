package com.jab125.configintermediary.mixin.configured;

import com.jab125.configintermediary.config.IntermediaryConfiguredConfig;
import com.mrcrayfish.configured.Config;
import com.mrcrayfish.configured.client.ClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientHandler.class, remap = false)
public class ClientHandlerMixin {
    @Inject(method = "onInitializeClient", at = @At("RETURN"))
    private void configIntermediary$onInitializeClient(CallbackInfo ci) {
        IntermediaryConfiguredConfig.forceConfiguredMenu = Config.CLIENT.forceConfiguredMenu.get();
    }
}
