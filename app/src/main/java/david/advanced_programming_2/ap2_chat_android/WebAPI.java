package david.advanced_programming_2.ap2_chat_android;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebAPI {
    @POST("/userauth")
    Call<ResponseModel> authenticateUser(@Query("username") String username, @Query("password")String password);
    @POST("/userauth/checkexists")
    Call<ResponseModel> checkUserExists(@Query("id") String username);

    @POST("/api/userregister")
    Call<ResponseModel> registerUser(@Query("id") String username, @Query("password")String password,
                            @Query("name") String name, @Query("server") String server, @Query("firebaseToken") String token);

    @GET("/api/contacts")
    Call<List<ContactModel>> getContacts(@Header("Authorization") String token);
    @POST("/api/contacts")
    Call<Void> addContact(@Header("Authorization") String token,@Query("id") String username,
                          @Query("name") String name, @Query("server") String server);

    @POST("/api/invitations")
    Call<Void> inviteContact(@Header("Authorization") String token,@Query("from") String from,
                             @Query("to") String to, @Query("server") String server);


    @GET("/api/contacts/{contactId}/messages")
    Call<List<MessageModel>> getMessages(@Header("Authorization") String token, @Path("contactId") String contactId);

    @POST("/api/contacts/{contactId}/messages")
    Call<ResponseBody> postMessage(@Header("Authorization") String token, @Path("contactId") String contactId,
                                   @Query("content") String content);

    @POST("/api/transfer")
    Call<Void> transferMessage(@Query("from") String from, @Query("to") String to ,@Query("content") String content);



}
