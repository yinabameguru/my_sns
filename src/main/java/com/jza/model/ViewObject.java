package com.jza.model;

import java.util.HashMap;

public class ViewObject {
    HashMap<String,Object> map = new HashMap<String, Object>();

    public Object set(String k,Object v){
        return map.put(k, v);
    }
    public Object get(String k){
        return map.get(k);
    }
}
