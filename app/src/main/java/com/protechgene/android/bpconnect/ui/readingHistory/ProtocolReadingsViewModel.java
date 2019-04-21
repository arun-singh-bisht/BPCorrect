package com.protechgene.android.bpconnect.ui.readingHistory;


import android.os.AsyncTask;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.List;


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
                    getNavigator().isProtocolExists(false,null);
                else
                    getNavigator().isProtocolExists(true,allProtocol.get(0));
            }
        });

    }

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
