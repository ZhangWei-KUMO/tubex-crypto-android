package chat.tubex.analysis.data.api;
import okhttp3.ResponseBody;

//import chat.tubex.analysis.model.KlinesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// 定义一个公共接口
public interface KlinesApi {
    @GET("/fapi/v1/klines")
    Call<ResponseBody> getKlines(@Query("symbol") String symbol, @Query("limit") int limit, @Query("interval") String interval);
    // 定义一个getKlines方法
}