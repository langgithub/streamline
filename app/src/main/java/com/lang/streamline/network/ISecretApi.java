package com.lang.streamline.network;




import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ISecretApi {

    @POST("api/v1/target/app/claimSecret")
    Call<NewBaseResponse<String>> queryPlugins(@Body ClaimSecretRequest request);


}