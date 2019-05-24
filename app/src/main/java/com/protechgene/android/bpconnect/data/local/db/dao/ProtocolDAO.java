package com.protechgene.android.bpconnect.data.local.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;

import java.util.List;

/**
 * Created by Arun.Singh on 9/11/2018.
 */

@Dao
public interface ProtocolDAO {

    @Insert
    void addNewProtocol(ProtocolModel protocolModel);

    @Query("SELECT * FROM protocol_table")
    List<ProtocolModel> getAllProtocol();

    @Query("DELETE FROM protocol_table")
    void deleteAllProtocol();
}
