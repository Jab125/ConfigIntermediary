package com.jab125.configintermediary.api.value;

import com.jab125.configintermediary.api.ConfigType;
import com.jab125.configintermediary.api.event.LoadEvent;
import com.jab125.configintermediary.api.event.SaveEvent;

@SuppressWarnings("unused")
public abstract class Config extends TransitiveConfigValue {
    public abstract void save();

    public abstract void load();

    public ConfigType getConfigType() {
        return ConfigType.COMMON;
    }

    /**
     * <p>Used for decorative purposes:</p>
     * <p>
     *     File name: "config/foo.json"<br/>
     *     getFileName(): foo.json
     * </p>
     */
    public abstract String getFileName();

    public abstract void registerSaveListener(SaveEvent save);

    public abstract void registerLoadListener(LoadEvent load);

    public abstract String getId();
}
