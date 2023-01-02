package com.jab125.configintermediary.compat.integration;

import com.jab125.configintermediary.api.value.ArrayConfigValue;
import com.jab125.configintermediary.api.value.ObjectConfigValue;
import com.jab125.configintermediary.api.value.TransitiveConfigValue;
import com.mrcrayfish.configured.api.IConfigEntry;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.ValueEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class IntermediaryFolderEntry implements IConfigEntry {
    private final TransitiveConfigValue config;
    private List<IConfigEntry> entries; // lazy loading
    private List<String> path = new ArrayList<>();
    public IntermediaryFolderEntry(TransitiveConfigValue config) {
        this.config = config;
    }

    public IntermediaryFolderEntry(TransitiveConfigValue config, List<String> path) {
        this.config = config;
        this.path = path;
    }
    @Override
    public List<IConfigEntry> getChildren() {
        if (entries == null) {
            ArrayList<IConfigEntry> array = new ArrayList<>();
            config.getAll().forEach((spouse, configValue) -> {
                if (configValue instanceof ObjectConfigValue || configValue instanceof ArrayConfigValue) {
                    if (configValue.get() instanceof List<?>) {
                        array.add(new ValueEntry(new IntermediaryListValue(config, spouse, configValue)));
                    } else if (configValue.get() instanceof Enum<?>) {
                        array.add(new ValueEntry(new IntermediaryEnumValue<>(config, spouse, configValue)));
                    } else {
                        array.add(new ValueEntry(new IntermediaryValue<>(config, spouse, configValue)));
                    }
                } else if (configValue instanceof TransitiveConfigValue transitiveConfigValue) {
                    ArrayList<String> d;
                    (d = new ArrayList<>(path)).add(spouse);
                    array.add(new IntermediaryFolderEntry(transitiveConfigValue, List.copyOf(d)));
                }
            });
            this.entries = List.copyOf(array);
        }
        return entries;
    }

    @Override
    public boolean isRoot() {
        return this.path.isEmpty();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public IConfigValue<?> getValue() {
        return null;
    }

    @Override
    public String getEntryName() {
        return IntermediaryValue.lastValue(this.path, "Root");
    }

    @Override
    public Text getTooltip() {
        return null;
    }

    @Override
    public String getTranslationKey() {
        return null;
    }
}
