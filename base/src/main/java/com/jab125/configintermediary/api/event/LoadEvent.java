package com.jab125.configintermediary.api.event;

import com.jab125.configintermediary.api.value.Config;
import net.minecraft.util.ActionResult;

public interface LoadEvent {
    ActionResult onLoad(Config config, Object newData);
}
