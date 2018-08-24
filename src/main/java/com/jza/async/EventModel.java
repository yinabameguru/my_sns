package com.jza.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType Type;
    private Map<String,Object> extent = new HashMap<String,Object>();
    private int actorId;
    private int entityType;
    private int entityId;

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

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }
}
