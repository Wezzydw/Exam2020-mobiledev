package com.example.exam2020_certificateapp.helpers;

import java.util.HashMap;
import java.util.Map;

public class PhotoHolder {

    private static PhotoHolder photoHelperInstance = new PhotoHolder(); // Singleton instance of the class
    private Map<String, Object> data = new HashMap<>(); // Map to hold data

    /**
     * gets the singleton instance of photoholder
     * @return instance of Photoholder
     */
    public static PhotoHolder getInstance() {
        return photoHelperInstance;
    }

    /**
     * Method to store data in a map pair of name and object
     * @param name
     * @param object
     */
    public void putExtra(String name, Object object) {
        data.put(name, object);
    }

    /**
     * gets the object with the matching name
     * @param name
     * @return object
     */
    public Object getExtra(String name) {
        return data.get(name);
    }

    /**
     * checks if the name exists in the map
     * @param name
     * @return true if the name exists
     */
    public boolean hasExtra(String name) {
        return data.containsKey(name);
    }

    /**
     * empties the map for all data
     */
    public void clear() {
        data.clear();
    }
}
