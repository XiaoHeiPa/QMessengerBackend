package org.qbychat.backend.utils;

import org.qbychat.backend.QMessengerBackendApplication;
import org.qbychat.backend.entity.Config;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class ConfigUtils {
    public Config loadConfig() {
        InputStream inputStream = QMessengerBackendApplication.class.getClassLoader().getResourceAsStream("/config.yml");
        Yaml yaml = new Yaml();
        return yaml.load(inputStream);
    }
}
