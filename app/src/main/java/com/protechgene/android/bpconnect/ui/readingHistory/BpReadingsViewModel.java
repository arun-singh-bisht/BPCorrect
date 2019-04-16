package com.protechgene.android.bpconnect.ui.readingHistory;


import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.ActualValue;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.BpReadingsResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.Chartdata;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class BpReadingsViewModel extends BaseViewModel<BPAllReadingsFragmentNavigator> {


    public BpReadingsViewModel(Repository repository) {
        super(repository);
    }



   /* public void getBpReadings()
    {

        String accessToken = getRespository().getAccessToken();
        String currentUserId = getRespository().getCurrentUserId();

        currentUserId = "132";
        String fromDay = "1552121902";
        String toDay = "1554800400";
        String noDay = "30";

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
                        List<Chartdata> chartdata = bpReadingsResponse.getData().get(0).getChartdata();

                        List<ActualValue> actualValues = new ArrayList<>();
                        for(int i=0;i<chartdata.size();i++)
                        {
                            actualValues.addAll(chartdata.get(i).getActualValues());
                        }

                        getNavigator().showReadingData(actualValues);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));

    }*/

    public void getBpReadings()
    {
            new AsynDataAccess().execute();
    }

    class AsynDataAccess extends AsyncTask<Void,Void,Void>
    {
        List<HealthReading> allRecords = null;
        @Override
        protected Void doInBackground(Void... voids) {

            allRecords = getRespository().getAllRecords();

            /*try {
                Thread.sleep(3000);
                allRecords = getRespository().getAllRecords();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getNavigator().showReadingData(allRecords);

        }
    }
}
