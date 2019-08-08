package com.protechgene.android.bpconnect.ui.reminder;


import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.Data;
import java.util.List;

public interface HistoryProtocolsFragmentNavigator {


    void handleError(Throwable throwable);

    void showData(List<Data> dataList);


}
