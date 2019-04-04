package com.protechgene.android.bpconnect.ui.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;


import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.ApiResponse;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel<N> extends ViewModel {

    private WeakReference<N> mNavigator;
    private Repository mRepository;

    protected final CompositeDisposable disposables = new CompositeDisposable();
    protected final MutableLiveData<ApiResponse> responseLiveData = new MutableLiveData<>();


    public BaseViewModel(Repository repository) { this.mRepository = repository; }

    public LiveData<ApiResponse> getResponseData() { return responseLiveData; }

    public N getNavigator() {
        return mNavigator.get();
    }

    public void setNavigator(N navigator) {
        this.mNavigator = new WeakReference<>(navigator);
    }

    protected Repository getRespository() { return mRepository; }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
