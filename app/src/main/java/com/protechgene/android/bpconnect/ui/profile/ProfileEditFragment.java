package com.protechgene.android.bpconnect.ui.profile;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.models.ProfileDetailModel;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.DatePickerFragment;
import com.protechgene.android.bpconnect.ui.home.HomeFragment;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileEditFragment extends BaseFragment implements ProfileEditFragmentNavigator, DatePickerFragment.DatePickedListener {

    public static final String FRAGMENT_TAG = "ProfileEditFragment";
    private ProfileEditFragmentViewModel mProfileEditFragmentViewModel;

    @BindView(R.id.edit_name)
    EditText edit_name;
    @BindView(R.id.edit_email)
    EditText edit_email;
    @BindView(R.id.edit_dob)
    EditText edit_dob;
    @BindView(R.id.edit_address)
    EditText edit_address;
    @BindView(R.id.edit_mobile)
    EditText edit_mobile;
    @BindView(R.id.edit_weight)
    EditText edit_weight;
    @BindView(R.id.edit_height)
    EditText edit_height;
    @BindView(R.id.edit_gender)
    EditText edit_gender;
    @BindView(R.id.edit_about)
    EditText edit_about;


    boolean isProfileComplete = true;
    int PICK_IMAGE_REQUEST = 284;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_edit_profile;
    }

    @Override
    protected void initialize() {
        mProfileEditFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(ProfileEditFragmentViewModel.class);
        mProfileEditFragmentViewModel.setNavigator(this);

        initView();
    }

    private void initView()
    {

        Bundle args = getArguments();
        if(args!=null)
           isProfileComplete = args.getBoolean("isProfileComplete");

        if(!isProfileComplete)
        {
            TextView txt_title =  getView().findViewById(R.id.txt_title);
            txt_title.setText("Create Profile");

            ((ImageView)getView().findViewById(R.id.img_left)).setImageResource(R.drawable.ic_action_close);

        }else
        {
            TextView txt_title =  getView().findViewById(R.id.txt_title);
            txt_title.setText("Edit Profile");

            ((ImageView)getView().findViewById(R.id.img_left)).setImageResource(R.drawable.ic_action_back);
        }

        getView().findViewById(R.id.img_right).setVisibility(View.VISIBLE);
        ((ImageView)getView().findViewById(R.id.img_right)).setImageResource(R.drawable.ic_action_done);



        String name = mProfileEditFragmentViewModel.getUserName();
        if(name==null || name.equalsIgnoreCase("null"))
            name = "BPConnect User";
        edit_name.setText(name);
        edit_email.setText(mProfileEditFragmentViewModel.getUserEmail());
        edit_dob.setText(mProfileEditFragmentViewModel.getUserDoB());
        edit_address.setText(mProfileEditFragmentViewModel.getUserAddress());
        edit_mobile.setText(mProfileEditFragmentViewModel.getUserMobile());
        edit_weight.setText(mProfileEditFragmentViewModel.getUserWeight());
        edit_height.setText(mProfileEditFragmentViewModel.getUserHeight());
        edit_gender.setText(mProfileEditFragmentViewModel.getUserGender());
        edit_about.setText(mProfileEditFragmentViewModel.getUserAbout());
    }

    @OnClick(R.id.img_left)
    public void onIconBackClick() {

        if(isProfileComplete)
            FragmentUtil.removeFragment(getBaseActivity());
        else
            getBaseActivity().finish();
    }

    @OnClick(R.id.img_right)
    public void onIconDoneClick() {
        showProgress("Updating Profile...");

        ProfileDetailModel profileDetailModel = new ProfileDetailModel();
        profileDetailModel.setFirstname(edit_name.getText().toString());
        profileDetailModel.setGender(edit_gender.getText().toString());
        profileDetailModel.setDob(edit_dob.getText().toString());
        profileDetailModel.setMobile1(edit_mobile.getText().toString());
        profileDetailModel.setAddress1(edit_address.getText().toString());
        profileDetailModel.setWeight(edit_weight.getText().toString());
        profileDetailModel.setHeight(edit_height.getText().toString());
        profileDetailModel.setAbout(edit_about.getText().toString());
        mProfileEditFragmentViewModel.updateProfile(profileDetailModel);
    }

    @OnClick(R.id.profile_edit_frag_change_pic)
    public void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onProfileUpdate() {
        hideProgress();
        if(isProfileComplete)
        {
            FragmentUtil.removeFragment(getBaseActivity());
            getBaseActivity().showSnakeBar("Profile Updated");
        }else
        {
            HomeFragment homeFragment = new HomeFragment();
            Bundle args = new Bundle();
            args.putBoolean("isNewUser",true);
            homeFragment.setArguments(args);
            FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,homeFragment, HomeFragment.FRAGMENT_TAG,null);
        }

    }

    @Override
    public void handleError(Throwable throwable) {
        hideProgress();
        getBaseActivity().showSnakeBar(throwable.getMessage());
    }

    @OnClick(R.id.edit_dob)
    public void onDoBClick()
    {
        DatePickerFragment newFragmentNight = new DatePickerFragment(this,"Date Of Birth");
        newFragmentNight.show(getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.edit_height)
    public void onHeightClick()
    {
        //DatePickerFragment newFragmentNight = new DatePickerFragment(this,"Date Of Birth");
        //newFragmentNight.show(getFragmentManager(), "datePicker");

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Select Height");

        // add a list
        String[] height = new String[48];
        int minHeight = 3;
        for(int i=1;i<=48;i++)
        {
            if(i%12==0) {
                minHeight++;
                height[i-1] =  minHeight +" feet ";
            }else {
                height[i - 1] = minHeight + " feet " + (i % 12) + " inches";
            }

        }
        String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
        builder.setItems(height, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_height.setText(height[which]);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.edit_gender)
    public void onGenderClick()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Select Gender");

        // add a list
        String[] gender = {"Male", "Female"};
        builder.setItems(gender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_gender.setText(gender[which]);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onDatePicked(Calendar c) {
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        edit_dob.setText(mYear+"-"+mMonth+"-"+mDay);
    }


}
