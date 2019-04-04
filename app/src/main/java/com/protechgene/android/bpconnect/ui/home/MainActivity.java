package com.protechgene.android.bpconnect.ui.home;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.fragments.HomeFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.app_bar_main;
    }

    @Override
    protected void initialize() {
        FragmentUtil.loadFragment(this,R.id.container_fragment,new HomeFragment(),HomeFragment.FRAGMENT_TAG,null);
    }


}
