package chat.tubex.analysis.ui.home;

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

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<TopSearchItem>> topSearchList;
    private static final String BASE_URL = "https://www.binance.com";
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);

    public interface DataFetchCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public HomeViewModel() {
        topSearchList = new MutableLiveData<>();
        fetchData(null); // Initial fetch
    }

    public LiveData<List<TopSearchItem>> getTopSearchList() {
        return topSearchList;
    }
    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }
    public void fetchData(DataFetchCallback callback) {
        isRefreshing.setValue(true);
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
                isRefreshing.setValue(false);
                if (response.isSuccessful()) {
                    BinanceResponse binanceResponse = response.body();
                    if(binanceResponse!=null){
                        List<TopSearchItem> items = binanceResponse.getData();
                        if(items!=null){
                            topSearchList.setValue(items);
                            if (callback != null) {
                                callback.onSuccess();
                            }
                            return;
                        }
                    }
                    if (callback != null) {
                        callback.onFailure("服务器返回数据为空");
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure("请求出错：" + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<BinanceResponse> call, Throwable t) {
                isRefreshing.setValue(false);
                // Handle network error
                if (callback != null) {
                    callback.onFailure("请求失败：" + t.getMessage());
                }
                t.printStackTrace();
            }
        });
    }
}