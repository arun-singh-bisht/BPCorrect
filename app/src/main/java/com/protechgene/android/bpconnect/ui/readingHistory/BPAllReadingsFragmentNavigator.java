package com.protechgene.android.bpconnect.ui.readingHistory;

import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.ActualValue;

import java.util.List;

public interface BPAllReadingsFragmentNavigator {


    void handleError(Throwable throwable);

    void showReadingData(List<ActualValue> valueList);

}
