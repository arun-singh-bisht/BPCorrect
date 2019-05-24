package com.protechgene.android.bpconnect.ui.readingHistory;


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

   public void getBpReadings()
    {

        if(!getRespository().isHistoryDataSync())
        {
            String accessToken = getRespository().getAccessToken();
            String currentUserId = getRespository().getCurrentUserId();
            String fromDay = "1546300800"; // 1 January 2019 00:00:00
            String toDay = (System.currentTimeMillis() + (5*60*60*1000))/1000 +"";
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

                            //Save InDB
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    List<HealthReading> healthReadingList = new ArrayList<>();
                                    for(int i=0;i<actualValues.size();i++)
                                    {
                                        ActualValue actualValue = actualValues.get(i);
                                        //HealthReading healthReading = new HealthReading(0,actualValue.getSBP(),actualValue.getDBP(),"0",actualValue.getTimestamp(),true,false,"");

                                        HealthReading healthReading = new HealthReading();
                                        healthReading.setSystolic(actualValue.getSBP());
                                        healthReading.setDiastolic(actualValue.getDBP());
                                        healthReading.setLogTime((Long.parseLong(actualValue.getTimestamp())*1000)+"");
                                        healthReading.setPulse(actualValue.getPULSE());
                                        healthReading.setSync(true);
                                        healthReading.setIs_abberant(actualValue.getIs_abberant());
                                        healthReading.setProtocol_id(actualValue.getProtocol_id());

                                        getRespository().addNewHealthRecord(healthReading);

                                        healthReadingList.add(healthReading);
                                    }

                                    getNavigator().showReadingData(healthReadingList);
                                    getRespository().setHistoryDataSyncStatus(true);
                                }
                            });


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            getNavigator().handleError(throwable);
                        }
                    }));
        }else
        {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<HealthReading> allRecords = getRespository().getAllRecords();
                    getNavigator().showReadingData(allRecords);
                }
            });

        }
    }

}
