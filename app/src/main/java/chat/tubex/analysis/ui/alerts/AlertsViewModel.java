package chat.tubex.analysis.ui.alerts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import chat.tubex.analysis.data.api.AlertsApi;
import chat.tubex.analysis.model.AlertsResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertsViewModel extends ViewModel {

    private final MutableLiveData<AlertsResponse> alertsResponseLiveData;
    private static final String BASE_URL = "https://www.binance.com";
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);

    public AlertsViewModel() {
        alertsResponseLiveData = new MutableLiveData<>();
        fetchData(null); // Initial fetch
    }
    public LiveData<AlertsResponse> getNewsList() {
        return alertsResponseLiveData;
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

        AlertsApi api = retrofit.create(AlertsApi.class);
        Call<AlertsResponse> call = api.getAlerts(1, 1, 10, 49);
        call.enqueue(new Callback<AlertsResponse>() {
            @Override
            public void onResponse(Call<AlertsResponse> call, Response<AlertsResponse> response) {
                isRefreshing.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    AlertsResponse alertsResponse = response.body();
                    alertsResponseLiveData.setValue(alertsResponse);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure("服务器返回数据为空或者解析失败");
                    }
                }
            }

            @Override
            public void onFailure(Call<AlertsResponse> call, Throwable t) {
                isRefreshing.setValue(false);
                if (callback != null) {
                    callback.onFailure("请求失败：" + t.getMessage());
                }
                t.printStackTrace();
            }
        });
    }
}