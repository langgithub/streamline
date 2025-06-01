package com.lang.streamline.network;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NewRetrofitCallback<T> implements Callback<NewBaseResponse<T>> {
    private static final String TAG = "NewRetrofitCallback";
    private static final int TOKEN_EXPIRED_CODE = 500001;

    @Override
    public void onResponse(Call<NewBaseResponse<T>> call, Response<NewBaseResponse<T>> response) {
       try {
           if (response.isSuccessful()) {
               NewBaseResponse<T> baseResponse = response.body();
               if (baseResponse != null) {
                   if (baseResponse.isSuccess()) {
                       T result = baseResponse.getResult();
                       onSuccess(result);
                   } else {
                       onError(baseResponse.getErrorMsg());
                       if (baseResponse.getErrorCode() == TOKEN_EXPIRED_CODE) {
                           tokenExpired();

                       }
                   }
               } else {
                   onError("Response body is null");
               }
           } else {
               // 处理HTTP错误状态
               handleErrorResponse(response);
           }
       } catch (Exception exception) {
           exception.printStackTrace();
           onError("An error occurred: " + exception.getMessage());
       }
    }

    @Override
    public void onFailure(Call<NewBaseResponse<T>> call, Throwable t) {
        handleFailure(t);
    }

    public abstract void onSuccess(T result);


    public void onError(String message) {
        // 默认实现，可被子类覆盖

    }

    private void handleErrorResponse(Response<NewBaseResponse<T>> response) {
        String errorMessage;
        switch (response.code()) {
            case TOKEN_EXPIRED_CODE:
                errorMessage = "Unauthorized";
                tokenExpired();
                break;
            case 403:
                errorMessage = "Forbidden";
                break;
            case 404:
                errorMessage = "Not Found";
                break;
            case 500:
                errorMessage = "Server Error";
                break;
            default:
                errorMessage = "Unknown Error: " + response.code();
        }
        onError(errorMessage + "," + response.message());
    }

    private void handleFailure(Throwable t) {
        String errorMessage;
        if (t instanceof SocketTimeoutException) {
            errorMessage = "Connection timeout";
        } else if (t instanceof UnknownHostException) {
            errorMessage = "No network connection";
        } else {
            errorMessage = t.getMessage();
        }
        onError(errorMessage);
    }

    private void tokenExpired() {


    }
}
