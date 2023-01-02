package com.jab125.configintermediary.mixin.owoconfig;

import com.jab125.configintermediary.api.entrypoint.ConfigRegistration;
import com.jab125.configintermediary.compat.OwoConfigCompat;
import io.wispforest.owo.config.ConfigWrapper;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(ConfigWrapper.class)
public class ConfigWrapperMixin {
    @Unique
    ConfigWrapper<?> whateverThisIs = ((ConfigWrapper)(Object)this);
    @Inject(method = "<init>", at = @At("RETURN"))
    public void configIntermediary$init(Class clazz, CallbackInfo ci) {
        var l = OwoConfigCompat.register(whateverThisIs, clazz);
        for (ConfigRegistration entrypoint : FabricLoader.getInstance().getEntrypoints("config-intermediary:registration", ConfigRegistration.class)) {
            entrypoint.onConfigRegistered(l);
        }
    }
}
