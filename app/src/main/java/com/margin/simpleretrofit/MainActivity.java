package com.margin.simpleretrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AFDRetrofit afdRetrofit = new AFDRetrofit.Builder().baseUrl("https://restapi.amap.com").build();
        api = afdRetrofit.create(AFDInterface.class);

        findViewById(R.id.btn_main_afdget).setOnClickListener(v -> {
            customerRetrofitGetRequest();
        });
        findViewById(R.id.btn_main_afdpost).setOnClickListener(v -> {
            customerRetrofitPostRequest();

        });

    }



    public void customerRetrofitGetRequest() {
        Call responseBodyCall = api.getWeather("110101", "ae6c53e2186f33bbf240a12d80672d1b");
        responseBodyCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException t) {
                Log.e(TAG, "onFailure: ", t);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {

                    Log.d(TAG, "onResponse: " + body.string());
                }
            }

        });
    }

    private void customerRetrofitPostRequest() {
        Call responseBodyCall = api.postWeather("110101", "ae6c53e2186f33bbf240a12d80672d1b");
        responseBodyCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call,  okhttp3.Response  response) {
                ResponseBody body = response.body();
                if (body != null) {

                    try {
                        Log.d(TAG, "onResponse: " + body.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}