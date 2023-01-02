package com.jab125.configintermediary.mixin.autoconfig;

import com.jab125.configintermediary.api.entrypoint.ConfigRegistration;
import com.jab125.configintermediary.compat.AutoConfigCompat;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AutoConfig.class, remap = false)
public class AutoConfigMixin {
    @Inject(method = "register", at = @At("RETURN"))
    private static <T extends ConfigData> void create(Class<T> configClass, ConfigSerializer.Factory<T> serializerFactory, CallbackInfoReturnable<ConfigHolder<T>> cir) {
        var l = AutoConfigCompat.register(cir.getReturnValue());
        for (ConfigRegistration entrypoint : FabricLoader.getInstance().getEntrypoints("config-intermediary:registration", ConfigRegistration.class)) {
            entrypoint.onConfigRegistered(l);
        }
    }
}
