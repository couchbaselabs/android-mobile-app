package com.couchbase.couchbase;

import android.util.JsonReader;

import org.json.JSONObject;

public class ConnectionData {
    String msg;
    JSONObject result;

    public ConnectionData(String msg){
        this.msg = msg;
    }
    public ConnectionData(JSONObject result){
        this.result = result;
    }
    public String getMsg() {
        return msg;
    }
    public JSONObject getResult() {
        return result;
    }
}
