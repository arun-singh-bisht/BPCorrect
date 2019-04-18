package com.protechgene.android.bpconnect.ui.home;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.app_bar_main;
    }

    @Override
    protected void initialize() {
        FragmentUtil.loadFragment(this,R.id.container_fragment,new HomeFragment(),HomeFragment.FRAGMENT_TAG,null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment frg = getSupportFragmentManager().findFragmentById(R.id.container_fragment);
        if (frg != null) {
            frg.onActivityResult(requestCode, resultCode, data);
        }
    }
}
