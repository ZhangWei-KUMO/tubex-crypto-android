package chat.tubex.analysis.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;
import chat.tubex.analysis.model.TopTakerPosition;
// 顶级做市商持仓方向
public interface TopTakerPositionApi {
    @GET("/futures/data/topLongShortPositionRatio")
    Call<List<TopTakerPosition>> getTopTakerPosition(@Query("symbol") String symbol,
                                                     @Query("period") String period,
                                                     @Query("limit") int limit);
}