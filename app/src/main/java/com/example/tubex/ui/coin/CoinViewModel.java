package com.example.tubex.ui.coin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoinViewModel extends ViewModel {

    private final MutableLiveData<List<TopSearchItem>> topSearchList;
    private static final String BASE_URL = "https://www.binance.com";

    public CoinViewModel() {
        topSearchList = new MutableLiveData<>();
        fetchData();
    }

    public LiveData<List<TopSearchItem>> getTopSearchList() {
        return topSearchList;
    }

    private void fetchData() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BinanceApi api = retrofit.create(BinanceApi.class);
        Call<BinanceResponse> call = api.getTopSearchList();
        call.enqueue(new Callback<BinanceResponse>() {
            @Override
            public void onResponse(Call<BinanceResponse> call, Response<BinanceResponse> response) {
                if (response.isSuccessful()) {
                    BinanceResponse binanceResponse = response.body();
                    if(binanceResponse!=null){
                        List<TopSearchItem> items = binanceResponse.getData();
                        if(items!=null){
                            topSearchList.setValue(items);
                        }
                    }
                } else {
                    // Handle the error
                    System.out.println("请求出错：：");
                    System.out.println(response.message());
                }
            }

            @Override
            public void onFailure(Call<BinanceResponse> call, Throwable t) {
                // Handle network error
                System.out.println("请求失败");
                t.printStackTrace();
            }
        });
    }
}