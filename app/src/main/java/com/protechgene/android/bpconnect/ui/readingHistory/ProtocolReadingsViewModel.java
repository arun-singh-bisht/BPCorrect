package com.protechgene.android.bpconnect.ui.readingHistory;


import android.os.AsyncTask;
import android.util.Log;

import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.Data;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.GetProtocolResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ProtocolReadingsViewModel extends BaseViewModel<ProtocolReadingsFragmentNavigator> {


    public ProtocolReadingsViewModel(Repository repository) {
        super(repository);
    }

    public void checkActiveProtocol()
    {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<ProtocolModel> allProtocol = getRespository().getAllProtocol();

                if(allProtocol==null || allProtocol.size()==0)
                {
                    getProtocolFromServer();
                    //getNavigator().isProtocolExists(false,null);
                }
                else
                    getNavigator().isProtocolExists(true,allProtocol.get(0));
            }
        });

    }

    private void getProtocolFromServer()
    {
        String accessToken = getRespository().getAccessToken();
        String userId = getRespository().getCurrentUserId();

        disposables.add(getRespository().getProtocolDetail(accessToken,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<GetProtocolResponse>() {
                    @Override
                    public void accept(GetProtocolResponse protocolResponse) throws Exception {

                        //Throwable throwable = new Throwable("Data Syn to server");
                        //getNavigator().handleError(throwable);
                        if(protocolResponse.getData().get(0).getProtocolId() == null) {

                            getNavigator().isProtocolExists(false,null);
                        }
                        else
                        {
                            Data data = protocolResponse.getData().get(0);

                            String startDate = data.getStartDate();
                            startDate = DateUtils.convertMillisecToDateTime(Long.parseLong(startDate) * 1000,"MMM dd,yyyy");

                            String endDate = data.getEndDate();
                            endDate = DateUtils.convertMillisecToDateTime(Long.parseLong(endDate) * 1000,"MMM dd,yyyy");

                            final ProtocolModel protocolModel = new ProtocolModel(0,startDate,endDate,data.getMorningAlarm(),data.getEveningAlarm(),true);
                            protocolModel.setProtocolCode(data.getProtocolId());


                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //getRespository().addNewProtocol(protocolModel);
                                    //Save new protocol in DB
                                    getRespository().addNewProtocol(protocolModel);
                                    getNavigator().isProtocolExists(true,protocolModel);
                                }
                            });
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));
    }


    public void getBpReadings()
    {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    List<ProtocolModel> allProtocol = getRespository().getAllProtocol();
                    ProtocolModel protocolModel = allProtocol.get(0);

                    List<HealthReading> allRecords = getRespository().getAllRecords();
                    if(allRecords==null || allRecords.size()==0)
                        getNavigator().showReadingData(null);
                    else
                    {
                        int avgSys = 0;
                        int avgDia = 0;
                        int avgpulse = 0;
                        int totalReadings = 28;
                        int readingTakenIdeal = 0;
                        int readingTakenActual = 0;
                        int readingMissed = 0;
                        int count = 0;

                        List<HealthReading> valueList = new ArrayList<>();
                        for(int i =0;i<allRecords.size();i++)
                        {
                            HealthReading healthReading = allRecords.get(i);
                            String protocol_id = healthReading.getProtocol_id();
                            if(protocol_id!=null && !protocol_id.isEmpty() && protocol_id.equalsIgnoreCase(protocolModel.getProtocolCode())) {
                                valueList.add(healthReading);

                                count++;
                                avgSys = avgSys + Integer.parseInt(healthReading.getSystolic());
                                avgDia = avgDia + Integer.parseInt(healthReading.getDiastolic());
                                avgpulse = avgpulse + Integer.parseInt(healthReading.getPulse());
                                readingTakenActual++;
                            }
                        }

                        //Calculate Missed Readings
                        String startDay = protocolModel.getStartDay();
                        String todayDate = DateUtils.getDateString(0,"MMM dd,yyyy");
                        int l = (int)DateUtils.daysDifferenceBetweenDates(startDay, todayDate,"MMM dd,yyyy");

                        readingTakenIdeal = l*4;
                        String currentTime = DateUtils.getDateString(0, "HH:mm");
                        String morningReadingTime = protocolModel.getMorningReadingTime();

                        long compareResult = DateUtils.compareTimeString(currentTime, morningReadingTime, "HH:mm");
                        if(compareResult>0)
                            readingTakenIdeal = readingTakenIdeal+2;

                        String eveningReadingTime = protocolModel.getEveningReadingTime();

                        compareResult = DateUtils.compareTimeString(currentTime, eveningReadingTime, "HH:mm");
                        if(compareResult>0)
                            readingTakenIdeal = readingTakenIdeal+2;


                        Log.d("DaysDifference","DaysDifference "+startDay+" - "+todayDate+" = "+l);
                        Log.d("DaysDifference","readingTakenIdeal "+readingTakenIdeal);

                        readingMissed = readingTakenIdeal - readingTakenActual;

                        Collections.reverse(valueList);
                        getNavigator().showReadingData(valueList);

                        if(count==0)
                            count =1;
                        getNavigator().showSummeyData(avgSys/count,avgDia/count,avgpulse/count,totalReadings,readingTakenActual,readingMissed);

                    }
                }
            });
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
