package com.protechgene.android.bpconnect.data.local.db;


import android.arch.lifecycle.LiveData;

import com.protechgene.android.bpconnect.data.local.db.models.Word;

import java.util.List;

public class DatabaseHelper implements DatabaseHelperInterface {

    private AppDatabase mAppDatabase;
    public DatabaseHelper(AppDatabase appDatabase) {
        this.mAppDatabase = appDatabase;
    }

    @Override
    public void addNewWord(Word word) {
        mAppDatabase.getWordDAO().addNewWord(word);
    }

    @Override
    public LiveData<List<Word>> getAllWords() {
        return mAppDatabase.getWordDAO().getAllWords();
    }

    @Override
    public void deleteAllWords() {
        mAppDatabase.getWordDAO().deleteAllWords();
    }
}
