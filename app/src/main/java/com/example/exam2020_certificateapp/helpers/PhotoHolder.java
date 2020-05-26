package com.example.exam2020_certificateapp.helpers;

import java.util.HashMap;
import java.util.Map;

public class PhotoHolder {

    private static PhotoHolder photoHelperInstance = new PhotoHolder();
    private Map<String, Object> data = new HashMap<>();
    public static PhotoHolder getInstance() {
        return photoHelperInstance;
    }
    public void putExtra(String name, Object object) {
        data.put(name, object);
    }
    public Object getExtra(String name) {
        return data.get(name);
    }
    public boolean hasExtra(String name) {
        return data.containsKey(name);
    }
    public void clear() {
        data.clear();
    }
}
