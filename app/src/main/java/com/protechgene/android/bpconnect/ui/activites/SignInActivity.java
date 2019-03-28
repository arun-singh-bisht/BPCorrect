package com.protechgene.android.bpconnect.ui.activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.GeneralUtil;



public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    EditText email;
    EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_sign_in);
        intiView();
    }

    private void intiView()
    {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.txt_forgot_password).setOnClickListener(this);
        findViewById(R.id.txt_sign_up).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.txt_forgot_password:{
                startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));
            }
            break;
            case R.id.txt_sign_up:{
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
            break;
            case R.id.btn_login:{
                startActivity(new Intent(SignInActivity.this,MainActivity.class));
                finish();
            }
        }
    }

}
