package chat.tubex.analysis.data.api;
import okhttp3.ResponseBody;

//import chat.tubex.analysis.model.KlinesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// 永续合约基差
public interface BasisApi {
    @GET("/futures/data/basis")
    Call<ResponseBody> getBasis(@Query("pair") String pair,
                                @Query("period") String period,
                                @Query("limit") int limit,
                                @Query("contractType") int contractType);
}

// contractType=PERPETUAL