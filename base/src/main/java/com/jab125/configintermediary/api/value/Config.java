package com.jab125.configintermediary.api.value;

import com.jab125.configintermediary.api.event.LoadEvent;
import com.jab125.configintermediary.api.event.SaveEvent;

@SuppressWarnings("unused")
public abstract class Config extends TransitiveConfigValue {
    public abstract void save();

    public abstract void load();

    public abstract void registerSaveListener(SaveEvent save);

    public abstract void registerLoadListener(LoadEvent load);

    public abstract String getId();
}
