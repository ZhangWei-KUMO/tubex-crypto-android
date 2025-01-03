package chat.tubex.analysis.ui.alerts.alerts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import chat.tubex.analysis.data.api.NewsApi;
import chat.tubex.analysis.model.NewsResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertsViewModel extends ViewModel {

    private final MutableLiveData<List<NewsResponse.Item>> newsList;
    private static final String BASE_URL = "https://api-one-wscn.awtmt.com";
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);

    public AlertsViewModel() {
        newsList = new MutableLiveData<>();
        fetchData(null); // Initial fetch
    }

    public LiveData<List<NewsResponse.Item>> getNewsList() {
        return newsList;
    }

    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }


    public interface DataFetchCallback {
        void onSuccess();

        void onFailure(String message);
    }
    public void fetchData(DataFetchCallback callback) {
        isRefreshing.setValue(true);

        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApi api = retrofit.create(NewsApi.class);
        Call<NewsResponse> call = api.getNews("global-channel", 40, 2, "VKikV7rHg+OhU+DW+HiofA==");
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                isRefreshing.setValue(false);
                if (response.isSuccessful() && response.body() != null) {


                    NewsResponse newsResponse = response.body();

                    if (newsResponse.getData() != null && newsResponse.getData().getItems() != null) {
                        List<NewsResponse.Item> items = newsResponse.getData().getItems();
                        newsList.setValue(items);

                        if(callback!= null){
                            callback.onSuccess();
                        }
                        return;
                    }
                }
                if (callback != null) {
                    callback.onFailure("服务器返回数据为空或者解析失败");
                }

            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                isRefreshing.setValue(false);
                if(callback!=null){
                    callback.onFailure("请求失败：" + t.getMessage());
                }
                t.printStackTrace();
            }
        });
    }
}