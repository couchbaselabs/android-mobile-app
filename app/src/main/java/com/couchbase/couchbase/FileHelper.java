package com.couchbase.couchbase;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FileHelper {
    public void writeToFile(Context context, String filename, String data, int mode){
        try{
            FileOutputStream fOut = context.openFileOutput(filename, mode);
            fOut.write((data).getBytes());
            fOut.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<ArrayList> readToFile(Context context, String filename, String delim){
        ArrayList<ArrayList> serverList = new ArrayList<ArrayList>();
        String line;
        try{
            FileInputStream fIn = context.openFileInput(filename);
            BufferedReader bR = new BufferedReader(new InputStreamReader(fIn));
            while((line = bR.readLine()) != null){
                ArrayList<String> connectionInfo = new ArrayList<>();
                StringTokenizer tokenizer = new StringTokenizer(line, delim);
                while(tokenizer.hasMoreTokens()) {
                    connectionInfo.add(tokenizer.nextToken());
                }
                serverList.add(connectionInfo);
            }
            fIn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return serverList;
    }

    public ArrayList<String> readLine(String line, String delim) {
        ArrayList<String> wordList = new ArrayList<>();
        String word = "";
        for (int i = 0; i < line.length(); i++) {
            String chr = (i == line.length() - 1) ? line.substring(i) : line.substring(i, i + 1);
            if (!chr.equals(delim)) {
                word += chr;
            }
            else{
                wordList.add(word);
                word = "";
            }
        }
        return wordList;
    }

    public void removeKey(Context context, String filename, String key){
        ArrayList<String> notKey = new ArrayList<>();
        String line;
        try{
            FileInputStream fIn = context.openFileInput(filename);
            BufferedReader bR = new BufferedReader(new InputStreamReader(fIn));
            while((line = bR.readLine()) != null){
                if(!line.equals(key)){
                    notKey.add(line);
                }
            }
            fIn.close();
            writeToFile(context, filename, "", context.MODE_PRIVATE);
            for(String string : notKey){
                writeToFile(context, filename, string + "\n", context.MODE_APPEND);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean findKey(Context context, String filename, String key){
        String line;
        try{
            FileInputStream fIn = context.openFileInput(filename);
            BufferedReader bR = new BufferedReader(new InputStreamReader(fIn));
            while((line = bR.readLine()) != null){
                if(line.equals(key)){
                    return true;
                }
            }
            fIn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void logFile(Context context, String filename, String logName){
        String line = "";
        String append;
        try{
            FileInputStream fIn = context.openFileInput(filename);
            BufferedReader bR = new BufferedReader(new InputStreamReader(fIn));
            while((append = bR.readLine()) != null){
                line += append + "\n";
            }
            fIn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Log.d(logName, "start\n" + line);
    }

    public boolean findKeyNoWhitespace(Context context, String filename, String key){
        String line = "";
        String append;
        try{
            FileInputStream fIn = context.openFileInput(filename);
            BufferedReader bR = new BufferedReader(new InputStreamReader(fIn));
            while((append = bR.readLine()) != null){
                line += trimWhitespace(append);
            }
            fIn.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String trimKey = trimWhitespace(key);
        if(line.trim().equals(trimKey.trim())){
            return true;
        }
        return false;
    }

    public String trimWhitespace(String append){
        String line = "";
        for(int i = 0; i < append.length(); i++){
            char c = append.charAt(i);
            if(!Character.isWhitespace(c)){
                line += c;
            }
        }
        return line;
    }
}

