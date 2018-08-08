package com.jza.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType Type;
    private Map<String,Object> extent = new HashMap<String,Object>();

    public EventModel(EventType eventType) {
        this.Type = eventType;
    }
    public EventModel() {
    }

    public Object get(String key) {
        return extent.get("key");
    }

    public EventModel set(String key, Object value) {
        this.extent.put("key",value);
        return this;
    }

    public EventType getType() {
        return Type;
    }

    public void setType(EventType type) {
        Type = type;
    }

    public Map<String, Object> getExtent() {
        return extent;
    }

    public void setExtent(Map<String, Object> extent) {
        this.extent = extent;
    }
}
