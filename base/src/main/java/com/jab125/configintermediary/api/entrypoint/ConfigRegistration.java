package com.jab125.configintermediary.api.entrypoint;

import com.jab125.configintermediary.api.value.Config;

@FunctionalInterface
public interface ConfigRegistration {
    void onConfigRegistered(Config config);
}
