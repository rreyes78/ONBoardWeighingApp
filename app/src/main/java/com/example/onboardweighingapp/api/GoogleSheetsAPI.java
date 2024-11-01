package com.example.onboardweighingapp.api;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleSheetsAPI {

    private static final String API_KEY = "AIzaSyA-n1bjc33ifJfqYZXZp3ICWRfnN0T1O9o"; // Replace with your API key
    private static final String SPREADSHEET_ID = "1fbiqeuKFJHd0xR2qI2HixXGYCgwN1suxMI2BXrH4uNg"; // Replace with your spreadsheet ID
    private static final String LOGGER = "ONBOARD_WEIGHING_SCALE!A:E"; // Replace with your desired range
    private static final String  OVERVIEW= "ONBOARD_WEIGHING_SCALE!I3:M3";
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    public void fetchData(final DataCallback callback, boolean overview) {


        String RANGE = overview ? OVERVIEW : LOGGER;
        String url = "https://sheets.googleapis.com/v4/spreadsheets/" + SPREADSHEET_ID + "/values/" + RANGE + "?key=" + API_KEY;

        Log.d("GoogleSheetsAPI", "Fetching data from: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("GoogleSheetsAPI", "Request failed", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    Log.d("GoogleSheetsAPI", "Fetched Data: " + jsonData);

                    // Notify success with the fetched data
                    callback.onSuccess(jsonData);
                } else {
                    // Handle the error
                    Log.e("GoogleSheetsAPI", "Error: " + response.code());
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }


    public interface DataCallback {
        void onSuccess(String data);
        void onFailure(IOException e);
    }


}
