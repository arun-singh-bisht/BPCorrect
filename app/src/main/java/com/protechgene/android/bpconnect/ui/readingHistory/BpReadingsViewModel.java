package com.protechgene.android.bpconnect.ui.readingHistory;


import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.BpReadingsResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class BpReadingsViewModel extends BaseViewModel<BPAllReadingsFragmentNavigator> {


    public BpReadingsViewModel(Repository repository) {
        super(repository);
    }

    public String getUserName()
    {
        return getRespository().getCurrentUserName();
    }

    public String getUserEmail()
    {
        return getRespository().getCurrentUserEmail();
    }

    public String getUserDoB()
    {
        return getRespository().getPatientDOB();
    }

    public String getUserAddress()
    {
        return getRespository().getPatientAddress();
    }

    public String getUserMobile()
    {
        return getRespository().getPatientMobile();
    }


    public void getBpReadings(String firstname, String gender, String dob, String mobile1, String address1)
    {

        String accessToken = getRespository().getAccessToken();
        String currentUserId = getRespository().getCurrentUserId();

        String fromDay = "";
        String toDay = "";
        String noDay = "";

        disposables.add(getRespository().getBpReadings(accessToken, currentUserId, fromDay, toDay, noDay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<BpReadingsResponse>() {
                    @Override
                    public void accept(BpReadingsResponse bpReadingsResponse) throws Exception {

                        //Save user Details
                        getNavigator().showReadingData();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));

    }
}
