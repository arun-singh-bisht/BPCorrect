package com.protechgene.android.bpconnect.data.local.db;



import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;

import java.util.List;

public interface DatabaseHelperInterface  {

    void addNewHealthRecord(HealthReading healthReading);

    List<HealthReading> getAllRecords();

    void deleteAllHealthRecords();

    //Protocol MEthods
    void addNewProtocol(ProtocolModel protocolModel);

    List<ProtocolModel> getAllProtocol();

    void deleteAllProtocol();


}
