package com.couchbase.couchbase;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExHelper {

    private FileHelper fileHelper = new FileHelper();
    private Context context;

    public RegExHelper(Context context){
        this.context = context;
    }

    public boolean match(String msg, String Filename){
        ArrayList<ArrayList> regExList = fileHelper.readToFile(context, Filename, ",");
        fileHelper.logFile(context, Filename, "MatchLog");
        Log.d("RegExLst", regExList + "");
        for(ArrayList<String> regEx : regExList){
            Log.d("RegExVal", regEx.get(1) + " / " + msg);
            Pattern pattern = Pattern.compile(regEx.get(1));
            Matcher matcher = pattern.matcher(msg);
            Log.d("Matcher", "" + matcher.matches());
            if(matcher.matches()){
                return true;
            }
        }
        return false;
    }
}