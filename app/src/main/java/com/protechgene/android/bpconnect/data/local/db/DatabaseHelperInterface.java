package com.protechgene.android.bpconnect.data.local.db;


import android.arch.lifecycle.LiveData;

import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.Word;

import java.util.List;

public interface DatabaseHelperInterface  {

    void addNewHealthRecord(HealthReading healthReading);

    List<HealthReading> getAllRecords();

    void deleteAllHealthRecords();
}
