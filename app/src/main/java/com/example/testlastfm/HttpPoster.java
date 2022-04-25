package com.example.testlastfm;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import static com.example.testlastfm.MainActivity.TAG;

import javax.net.ssl.HttpsURLConnection;

public class HttpPoster extends AsyncTask<String, String, String> {
    private static final int TIMEOUT_CONNECT = 15000;
    private static final int TIMEOUT_READ = 15000;
    private static final String REQUEST_METHOD = "POST";
    private final JSONResponse jsonResponse;
    private final String method;
    HttpsURLConnection connection = null;
    BufferedReader reader = null;

    public interface JSONResponse {

        void onGetResponse(String method, String JSON);
    }

    public HttpPoster(JSONResponse jsonResponse, String method) {
        this.jsonResponse = jsonResponse;
        this.method = method;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            //Log.d(TAG, "Start connect...");
            URL url = new URL(params[0]);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(TIMEOUT_CONNECT);
            connection.setReadTimeout(TIMEOUT_READ);
            connection.setRequestMethod(REQUEST_METHOD);
            connection.connect();

            //Log.d(TAG, "Connected!");

            //Получаю код ответа
            int status = connection.getResponseCode();
            if (status == 200) {
                //Log.d(TAG, "Status = Ok!");
                InputStream stream = connection.getInputStream();

                //Прочитаю в буфер все строки из ответа
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\r\n");

                return buffer.toString();
            } else {
                Log.d(TAG, "Error status №" + status);
                return null;
            }
        } catch (IOException e) {
            Log.d(TAG, "IOException: " + e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.d(TAG, "IOException(2): " + e);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        jsonResponse.onGetResponse(this.method, result);
    }
}