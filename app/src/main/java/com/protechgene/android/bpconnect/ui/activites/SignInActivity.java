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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.txt_forgot_password:{
                //Start email enter activity
                //startActivityForResult(new Intent(SignInActivity.this,ForgotPasswordActivity.class),1002);
            }
            break;
            case R.id.btn_login:{
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
            break;
        }
    }

    public void goToSignUp()
    {
        //startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
        finish();
    }

    public void login()
    {
        //Get Values

        String username = email.getText().toString();
        String password = pass.getText().toString();

        //Validate Values
        String message = null;
        if(!GeneralUtil.validateEditText(this,R.id.input_name))
            message = "Enter Username.";
        else if(!GeneralUtil.validatePAsswordEditText(this,R.id.input_password))
            message = "Enter valid password.";

        if(message !=null) {
            GeneralUtil.showToast(SignInActivity.this, message);
            return;
        }

       // GeneralUtil.showProgressDialog(this,"Please wait");
    }
}
