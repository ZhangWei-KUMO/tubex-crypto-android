package chat.tubex.analysis.data.api;
import okhttp3.ResponseBody;

//import chat.tubex.analysis.model.KlinesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// 手续费
public interface FundingRateApi {
    @GET("/fapi/v1/fundingRate")
    Call<ResponseBody> getFundingRate(@Query("symbol") String symbol);
}