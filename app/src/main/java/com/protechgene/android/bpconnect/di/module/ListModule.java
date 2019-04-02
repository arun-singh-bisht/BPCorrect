package com.protechgene.android.bpconnect.di.module;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class ListModule {

   /* public MovieAdapter provideAdapter()
    {
        return new MovieAdapter();
    }*/

    public LinearLayoutManager provideLinearLayoutManager(Context context)
    {
        return new LinearLayoutManager(context);
    }


}
