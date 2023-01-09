package com.jab125.configintermediary.mixin.modmenu;

import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;

@Mixin(FabricMod.class)
public class FabricModMixin {
    @Shadow
    @Final
    protected ModMetadata metadata;

    @SuppressWarnings("rawtypes")
    @Inject(method = "<init>", at = @At("RETURN"))
    private void configIntermediary$init(ModContainer modContainer, Set modpackMods, CallbackInfo ci) {
        String id = metadata.getId();
        if (id.startsWith("config-intermediary-") && metadata.containsCustomValue("config-intermediary")) {
            if (metadata.getCustomValue("config-intermediary").getType() == CustomValue.CvType.OBJECT) {
                if (metadata.getCustomValue("config-intermediary").getAsObject().containsKey("builtin")) {
                    if (metadata.getCustomValue("config-intermediary").getAsObject().get("builtin").getType() == CustomValue.CvType.BOOLEAN) {
                        if (metadata.getCustomValue("config-intermediary").getAsObject().get("builtin").getAsBoolean()) {
                            fillParentIfEmpty((ModMenuDataAccessor) ((FabricMod) (Object) this).getModMenuData(), "config-intermediary");
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings({"SameParameterValue", "ReplaceSizeZeroCheckWithIsEmpty"})
    private void fillParentIfEmpty(ModMenuDataAccessor accessor, String parent) {
        if (accessor.getParent().isEmpty()) {
            accessor.setParent(Optional.of(parent));
        }
    }
}
