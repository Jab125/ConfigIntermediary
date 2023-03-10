package com.jab125.configintermediary.compat.integration;

import com.jab125.configintermediary.api.value.Config;
import com.mrcrayfish.configured.api.ConfigType;
import com.mrcrayfish.configured.api.IConfigEntry;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.util.ConfigHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class IntermediaryConfig implements IModConfig {
    private final Config config;
    private final String id;

    public IntermediaryConfig(Config config) {
        this.config = config;
        this.id = config.getId();
    }

    @SuppressWarnings("unused")
    public IntermediaryConfig(Config config, String id) {
        this.config = config;
        this.id = id;
    }

    @Override
    public void update(IConfigEntry iConfigEntry) {
        Set<IConfigValue<?>> changedValues = ConfigHelper.getChangedValues(iConfigEntry);
        if (!changedValues.isEmpty()) {
            for (IConfigValue<?> changedValue : changedValues) {
                if (changedValue instanceof IntermediaryValue<?> intermediaryValue) {
                    intermediaryValue.configValue.set(intermediaryValue.get());
                }
            }
            config.save();
        }
    }

    @Override
    public IConfigEntry getRoot() {
        return new IntermediaryFolderEntry(config);
    }

    @Override
    public ConfigType getType() {
        return switch (config.getConfigType()) {
            case COMMON -> ConfigType.UNIVERSAL;
            case CLIENT -> ConfigType.CLIENT;
            case SERVER -> ConfigType.SERVER;
        };
        //return ConfigType.UNIVERSAL;
    }

    @Override
    public String getFileName() {
        return config.getFileName() != null ? config.getFileName() : FabricLoader.getInstance().getModContainer(getModId()).isPresent() ? FabricLoader.getInstance().getModContainer(getModId()).get().getMetadata().getName() : getModId();
    }

    @Override
    public boolean isChanged() {
        return ConfigHelper.gatherAllConfigValues(this).stream().anyMatch(a -> {
            if (a instanceof IntermediaryValue<?> intermediaryValue) {
                return !(intermediaryValue.configValue.get().equals(intermediaryValue.configValue.getDefaultValue()));
            }
            return false;
        });
    }

    @Override
    public String getModId() {
        return id;
    }

    public String getTranslationKey() {
        return String.format("intermediary.%s", this.id);
    }

    @Override
    public void loadWorldConfig(Path path, Consumer<IModConfig> consumer) {
        // no
    }

    @Override
    public void restoreDefaults() {
        for (IConfigValue<?> gatherAllConfigValue : ConfigHelper.gatherAllConfigValues(this)) {
            if (gatherAllConfigValue instanceof IntermediaryValue<?> intermediaryValue) {
                gatherAllConfigValue.restore();
                intermediaryValue.apply();
            }
        }
        config.save();
    }

    @Override
    public boolean isReadOnly() {
        return config.getConfigType().equals(com.jab125.configintermediary.api.ConfigType.SERVER);
    }
}
