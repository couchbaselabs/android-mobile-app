package com.couchbase.couchbase;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonHelper {
    private JSONObject result;
    public JsonHelper(JSONObject result){
        this.result = result;
    }

    public String getTargetString(String target){
        try{
            return result.getString(target);
        }
        catch (Exception e){
            return null;
        }
    }
    public int getTargetInt(String target){
        try{
            return result.getInt(target);
        }
        catch (Exception e){
            return 0;
        }
    }
    public long getTargetLong(String target){
        try{
            return result.getLong(target);
        }
        catch (Exception e){
            return 0;
        }
    }
    public double getTargetDouble(String target){
        try{
            return result.getDouble(target);
        }
        catch (Exception e){
            return 0;
        }
    }
    public JSONObject getTargetObject(String target){
        try{
            return result.getJSONObject(target);
        }
        catch (Exception e){
            return null;
        }
    }
    public JSONArray getTargetArray(String target){
        try{
            return result.getJSONArray(target);
        }
        catch (Exception e){
            return null;
        }
    }
}
