package com.jza.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType Type;
    private Map<String,String> extent = new HashMap<String,String>();

    public EventModel(EventType eventType) {
        this.Type = eventType;
    }

    public Object getExtent(String key) {
        return extent.get("key");
    }

    public EventModel setExtent(String key, String value) {
        this.extent.put("key",value);
        return this;
    }
}
