package com.protechgene.android.bpconnect.ui.changepassword;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.login.LoginActivity;
import com.protechgene.android.bpconnect.ui.profile.ProfileEditFragment;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragmentNavigator;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragmentViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class ChangePasswordFragment extends BaseFragment implements ChangePasswordNavigator {

    public static final String FRAGMENT_TAG = "ChangePasswordFragment";
    private ChangePasswordViewModel mProfileFragmentViewModel;


    @BindView(R.id.edit_confim_password)
    EditText edit_password_confirm;
    @BindView(R.id.edit_password)
    EditText edit_password;



    @Override
    protected int layoutRes() {
        return R.layout.fragment_change_password;
    }

    @Override
    protected void initialize() {

        mProfileFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(ChangePasswordViewModel.class);
        mProfileFragmentViewModel.setNavigator(this);

        initView();
    }

    private void initView()
    {
        getView().findViewById(R.id.root_header).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView)getView().findViewById(R.id.img_left)).setColorFilter(Color.argb(255, 255, 255, 255));
        ((ImageView)getView().findViewById(R.id.img_right)).setColorFilter(Color.argb(255, 255, 255, 255));
        ((TextView)getView().findViewById(R.id.txt_title)).setVisibility(View.GONE);

    }

    @OnClick(R.id.img_left)
    public void onIconBackClick() {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    @OnClick(R.id.btn_change_password)
    public void onChangePassword(){
        String password = edit_password.getText().toString().trim();
        String confirm_password = edit_password_confirm.getText().toString().trim();


            mProfileFragmentViewModel.changePassword(password,confirm_password);
        Log.d("sohit", "onChangePassword: success ");
    }

    @Override
    public void handleError(Throwable throwable) {
        Log.d("sohit", "handleError: "+throwable.getMessage());
        getBaseActivity().showSnakeBar(throwable.getMessage());
    }

    @Override
    public void navigateToLogin(String msg) {
        Log.d("sohit", "navigateToProfile: ");
        getBaseActivity().showSnakeBar(msg);

        Intent i = new Intent(getBaseActivity(), LoginActivity.class);
        getBaseActivity().finish();
         startActivity(i);

    }

   /* @Override
    public void showProfileDetails() {
        String name = mProfileFragmentViewModel.getUserFirstName();
        if(name==null || name.equalsIgnoreCase("null") || name.isEmpty())
            name = "BPCorrect User";
        else
            name =  mProfileFragmentViewModel.getUserFirstName() +" "+ mProfileFragmentViewModel.getUserLastName();
        text_profile_name.setText(name);
        text_email.setText(mProfileFragmentViewModel.getUserEmail());
        text_dob.setText(mProfileFragmentViewModel.getUserDoB());
        text_address.setText(mProfileFragmentViewModel.getUserAddress());
        text_mobile.setText(mProfileFragmentViewModel.getUserMobile());
        text_about.setText(mProfileFragmentViewModel.getUserAbout());


        //Set Gender
        String gender = mProfileFragmentViewModel.getUserGender();
        if(gender==null || gender.isEmpty() || gender.equalsIgnoreCase("null"))
            gender = "-";

        text_gender_value.setText(gender);

        //Set Height
        String userHeight = mProfileFragmentViewModel.getUserHeight();
        if(userHeight==null || userHeight.isEmpty() || userHeight.equalsIgnoreCase("0"))
            userHeight = "-";
        else
        {
            if(userHeight.contains("feet"))
                userHeight = userHeight.replace("feet","'");
            if(userHeight.contains("inches"))
                userHeight = userHeight.replace("inches","\"");
        }
        text_height_value.setText(userHeight);

        //Set Weight
        String userWeight = mProfileFragmentViewModel.getUserWeight();
        if(userWeight==null || userWeight.isEmpty() || userWeight.equalsIgnoreCase("0"))
            userWeight = "-";
        else
        {
            userWeight = userWeight+" lb";
        }
        text_weight_value.setText(userWeight);

        //Set Age
        String userDoB = mProfileFragmentViewModel.getUserDoB();
        if(userDoB==null || userDoB.isEmpty() || userDoB.equalsIgnoreCase("0"))
            userDoB = "-";
        else
        {
            Date date1= null;
            try {
                date1 = new SimpleDateFormat("mm-dd-yyyy").parse(userDoB);
                userDoB = DateUtils.getAge(date1)+" yr";
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        text_age_value.setText(userDoB);

        String image_url = mProfileFragmentViewModel.getProfilePic();
        if(image_url != null)
            Glide.with(getContext()).load("http://67.211.223.164:8080"+image_url).placeholder(R.drawable.default_pic).into(profile_pic);
    }*/
}
