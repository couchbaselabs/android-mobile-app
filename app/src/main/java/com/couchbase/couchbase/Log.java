package com.couchbase.couchbase;

import org.json.JSONArray;
import org.json.JSONObject;

public class Log {
    private String event;
    private String time;
    private String node;
    private String module;
    private String type;
    public Log(JSONArray logList, int index){
        try{
            JsonHelper logReader = new JsonHelper((JSONObject) logList.get(index));
            this.event = logReader.getTargetString("text");
            this.time = logReader.getTargetString("serverTime").substring(11, 16) + " on " + logReader.getTargetString("serverTime").substring(0, 10);
            this.node = logReader.getTargetString("node").substring(4);
            this.module = logReader.getTargetString("module");
            this.type = logReader.getTargetString("type");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getNode() {
        return node;
    }

    public String getTime() {
        return time;
    }

    public String getEvent() {
        return event;
    }

    public String getModule() {
        return module;
    }

    public String getType() {
        return type;
    }
}
