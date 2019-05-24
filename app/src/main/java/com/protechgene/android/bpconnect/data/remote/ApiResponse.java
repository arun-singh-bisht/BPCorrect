package com.protechgene.android.bpconnect.data.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.protechgene.android.bpconnect.data.remote.responseModels.ResponseDataInterface;

import static com.protechgene.android.bpconnect.data.remote.Status.SUCCESS;
import static com.protechgene.android.bpconnect.data.remote.Status.ERROR;
import static com.protechgene.android.bpconnect.data.remote.Status.LOADING;

public class ApiResponse {

    public final Status status;

    @Nullable
    public final ResponseDataInterface responseData;

    @Nullable
    public final Throwable error;

    private ApiResponse(Status status, @Nullable ResponseDataInterface responseData, @Nullable Throwable error) {
        this.status = status;
        this.responseData = responseData;
        this.error = error;
    }

    public static ApiResponse loading() {
        return new ApiResponse(LOADING, null, null);
    }

    public static ApiResponse success(@NonNull ResponseDataInterface data) {
        return new ApiResponse(SUCCESS, data, null);
    }

    public static ApiResponse error(@NonNull Throwable error) {
        return new ApiResponse(ERROR, null, error);
    }

}
