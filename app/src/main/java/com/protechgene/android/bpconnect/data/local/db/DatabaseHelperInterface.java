package com.protechgene.android.bpconnect.data.local.db;


import android.arch.lifecycle.LiveData;

import com.protechgene.android.bpconnect.data.local.db.models.Word;

import java.util.List;

public interface DatabaseHelperInterface {

    void addNewWord(Word word);

    LiveData<List<Word>> getAllWords();

    void deleteAllWords();
}
