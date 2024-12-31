package chat.tubex.analysis.data.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Header;
import chat.tubex.analysis.model.NewsResponse;
public interface NewsApi {
    @GET("/apiv1/search/live")
    Call<NewsResponse> getNews(@Query("channel") String pair,
                                @Query("limit") int period,
                                @Query("score") int limit,
                                @Header("If-None-Match") String etag

    );
}

