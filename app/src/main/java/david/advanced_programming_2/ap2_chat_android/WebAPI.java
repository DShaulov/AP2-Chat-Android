package david.advanced_programming_2.ap2_chat_android;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebAPI {
    @POST("/api/userauth")
    Call<String> authenticateUser(@Query("username") String username, @Query("password")String password);




}
