package com.protechgene.android.bpconnect.ui.readingHistory;

import android.arch.lifecycle.LiveData;

import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.ActualValue;

import java.util.List;

public interface BPAllReadingsFragmentNavigator {


    void handleError(Throwable throwable);

    void showReadingData(List<HealthReading> valueList);


}
