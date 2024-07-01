package org.moonstudio.moontest;

import org.moonstudio.moontest.util.ColorUtil;

public enum Messages {
    not_loaded,
    no_permission,
    ;
    public String getPrefix() {
        return Config.config.getString("messages.prefix");
    }

    public String getMessage() {
        return ColorUtil.colorAlternate(getPrefix() + Config.config.getString("messages." + name()));
    }
}
