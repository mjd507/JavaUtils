package com.github.mjd507.util.util;

import java.util.HashMap;


/**
 * Map工具类
 */
public class MapUtil extends HashMap<String, Object> {

    @Override
    public MapUtil put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static MapUtil newMap() {
        return new MapUtil();
    }

}
