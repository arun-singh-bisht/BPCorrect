package com.protechgene.android.bpconnect.ui.reminder;

import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;

import java.util.List;

public interface ReminderFragmentNavigator {


    void handleError(Throwable throwable);

    void showSearchingProgress();

    void isProtocolExists(boolean status, ProtocolModel protocolModel);

    void invalidTimeSelection(String message);

    void onProtocolCreated( ProtocolModel protocolModel);
}
