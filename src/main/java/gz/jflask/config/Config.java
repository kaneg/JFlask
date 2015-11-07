package gz.jflask.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * It was Created by kaneg on 7/2/15.
 */
public class Config {
    private Map<String, String> map = new HashMap<>();

    public void put(String key, String value) {
        map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }

    public String get(String key, String defaultValue) {
        String s = map.get(key);
        return s == null ? defaultValue : s;
    }

    public Collection<String> keys() {
        return map.keySet();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String raw = get(key);
        if (raw != null) {
            return Boolean.parseBoolean(raw);
        }
        return defaultValue;
    }
}
