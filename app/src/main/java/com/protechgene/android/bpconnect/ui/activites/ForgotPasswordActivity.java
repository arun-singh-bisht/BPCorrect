package com.protechgene.android.bpconnect.ui.activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.protechgene.android.bpconnect.R;


public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener  {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_forgot_password);

        initViews();
    }

    private void initViews()
    {
        findViewById(R.id.image_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.image_back:{
                finish();
            }
            break;
        }
    }

}
