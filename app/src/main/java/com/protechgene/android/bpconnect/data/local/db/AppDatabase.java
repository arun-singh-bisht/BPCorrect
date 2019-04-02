package com.protechgene.android.bpconnect.data.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.protechgene.android.bpconnect.data.local.db.dao.WordDAO;
import com.protechgene.android.bpconnect.data.local.db.models.Word;


/**
 * Created by Arun.Singh on 9/11/2018.
 */

@Database(entities = {Word.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WordDAO getWordDAO();

}
