package com.protechgene.android.bpconnect.ui.readingHistory;

import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;

import java.util.List;

public interface ProtocolReadingsFragmentNavigator {


    void handleError(Throwable throwable);

    void isProtocolExists(boolean status, ProtocolModel protocolModel);

    void showReadingData(List<HealthReading> valueList);

}
