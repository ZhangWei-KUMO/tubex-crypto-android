package chat.tubex.analysis;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import chat.tubex.analysis.data.api.NewsApi;
import chat.tubex.analysis.model.NewsResponse;
import chat.tubex.analysis.ui.home.HomeViewModel;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://api-one-wscn.awtmt.com";
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_list);
    }

    // 获取网络新闻数据
    public void fetchData(HomeViewModel.DataFetchCallback callback) {
        isRefreshing.setValue(true);
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NewsApi api = retrofit.create(NewsApi.class);
        Call<NewsResponse> call = api.getNews("global-channel",40,2,"VKikV7rHg+OhU+DW+HiofA==");
        call.enqueue(new Callback<NewsResponse>() {
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                isRefreshing.setValue(false);
                if (response.isSuccessful()) {

                } else {
                    if (callback != null) {
                        callback.onFailure("请求出错：" + response.message());
                    }
                    System.out.println("请求出错：：");
                    System.out.println(response.message());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                isRefreshing.setValue(false);
                // Handle network error
                if (callback != null) {
                    callback.onFailure("请求失败：" + t.getMessage());
                }
                System.out.println("请求失败");
                t.printStackTrace();
            }
        });
    }
}