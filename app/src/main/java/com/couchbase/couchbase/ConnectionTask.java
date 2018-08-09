package com.couchbase.couchbase;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

public class ConnectionTask extends AsyncTask<ArrayList<String>, Integer, ConnectionData> {
    private ConnectionResponse process;
    private String extension;
    private ArrayList<String> clusterServerList;
    private ArrayList<String> loginVars;
    private String requestMethod;
    private FileHelper fileHelper;
    private String[] paramKey;
    private String[] paramValue;
    private String action;
    public ConnectionTask(ConnectionResponse process, String extension, String requestMethod){
        this.process = process;
        this.extension = extension;
        this.loginVars = new ArrayList<>();
        this.requestMethod = requestMethod;
        this.fileHelper = new FileHelper();
    }

    public ConnectionTask(ConnectionResponse process, String extension, String requestMethod, String[] paramKey, String[] paramValue, String action){
        this.process = process;
        this.extension = extension;
        this.loginVars = new ArrayList<>();
        this.requestMethod = requestMethod;
        this.fileHelper = new FileHelper();
        this.paramKey = paramKey;
        this.paramValue = paramValue;
        this.action = action;
    }
    @Override
    protected ConnectionData doInBackground(final ArrayList<String>... params) {
        clusterServerList = params[0];
        for(int i = 1; i < clusterServerList.size(); i++){
            final ArrayList<String> connectionInfo = fileHelper.readLine(clusterServerList.get(i), "/");
            try{
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(connectionInfo.get(2), connectionInfo.get(3).toCharArray());
                    }
                });
                URL url = new URL("http://" + connectionInfo.get(0) + ":" + connectionInfo.get(1) + extension);
                Log.d("URL", url + "");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(requestMethod);
                if(requestMethod == "POST"){
                    Uri.Builder builder = new Uri.Builder();
                    if(action == "ADD"){
                        builder.appendQueryParameter(paramKey[0], paramValue[0]);
                        builder.appendQueryParameter(paramKey[1], connectionInfo.get(2));
                        builder.appendQueryParameter(paramKey[2], connectionInfo.get(3));
                        builder.appendQueryParameter(paramKey[3], paramValue[3]);
                    }
                    else if (action == "REBALANCE"){
                        builder.appendQueryParameter(paramKey[0], paramValue[0]);
                    }
                    String query = builder.build().getEncodedQuery();

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                }
                if(connection.getResponseCode() == 200){
                    ConnectionData data;
                    if(requestMethod == "GET"){
                        InputStream response = connection.getInputStream();
                        BufferedReader bR = new BufferedReader(new InputStreamReader(response));
                        String line;
                        StringBuilder responseStrBuilder = new StringBuilder();
                        while((line =  bR.readLine()) != null){
                            responseStrBuilder.append(line);
                        }
                        response.close();
                        JSONObject result = new JSONObject(responseStrBuilder.toString());
                        data = new ConnectionData(result);
                    }
                    else{
                        data = new ConnectionData("Connected");
                    }
                    connection.disconnect();
                    loginVars = fileHelper.readLine(clusterServerList.get(i), "/");
                    loginVars.add(0,clusterServerList.get(0));
                    Log.d("Loginvars", loginVars + "");
                    return data;
                }
                else{
                    Log.d("response_code", "" + connection.getResponseCode());
                }
            }
            catch(Exception e){
                Log.d("Exception", "" + e);
                e.printStackTrace();
            }
        }
        ConnectionData data = new ConnectionData("Connection Failure");
        return data;
    }

    protected void onPostExecute(ConnectionData result) {
        process.getResponse(result, extension);
    }

    public ArrayList<String> getLoginVars(){
        return loginVars;
    }
}
