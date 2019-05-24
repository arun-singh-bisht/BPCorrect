package com.protechgene.android.bpconnect.ui.profile;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bikomobile.multipart.Multipart;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.models.ProfileDetailModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.cityandstate.DataOptions;
import com.protechgene.android.bpconnect.data.remote.responseModels.cityandstate.StateCityOptions;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.DatePickerFragment;
import com.protechgene.android.bpconnect.ui.home.HomeFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class ProfileEditFragment extends BaseFragment implements ProfileEditFragmentNavigator, DatePickerFragment.DatePickedListener {

    public static final String FRAGMENT_TAG = "ProfileEditFragment";
    private ProfileEditFragmentViewModel mProfileEditFragmentViewModel;


    int PICK_IMAGE_REQUEST = 284;
    int PICK_CAMERA_CODE = 954;
    Uri uriImage = null;

    // edit by sohit
    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;
    @BindView(R.id.btn_save)
    TextView btn_save;
    @BindView(R.id.edit_city)
    EditText edit_city;
    @BindView(R.id.edit_zipcode)
    EditText edit_zipcode;
    @BindView(R.id.edit_state)
    EditText edit_state;
    String state_code = null;


    @BindView(R.id.profile_edit_frag_change_pic)
    CircularImageView circularImageView_img;
    @BindView(R.id.edit_first_name)
    EditText edit_first_name;
    @BindView(R.id.edit_last_name)
    EditText edit_last_name;
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
//    @BindView(R.id.edit_about)
//    EditText edit_about;
    @BindView(R.id.camera_icon)
    View camera_icon;


    boolean isProfileComplete = true;
    ImagePicker imagePicker;
    Uri profileImageUri;

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

        getView().findViewById(R.id.img_right).setVisibility(View.GONE);
        ((ImageView)getView().findViewById(R.id.img_right)).setImageResource(R.drawable.ic_action_done);



        String name = mProfileEditFragmentViewModel.getUserFirstName();
        if(name==null || name.equalsIgnoreCase("null"))
            name = "BPCorrect User";
        edit_first_name.setText(name);
        edit_last_name.setText(mProfileEditFragmentViewModel.getUserLastName());
        edit_email.setText(mProfileEditFragmentViewModel.getUserEmail());
        edit_dob.setText(mProfileEditFragmentViewModel.getUserDoB());
        edit_mobile.setText(mProfileEditFragmentViewModel.getUserMobile());
        edit_weight.setText(mProfileEditFragmentViewModel.getUserWeight());
        edit_height.setText(mProfileEditFragmentViewModel.getUserHeight());
        edit_gender.setText(mProfileEditFragmentViewModel.getUserGender());
        edit_city.setText(mProfileEditFragmentViewModel.getUserCity());
        edit_zipcode.setText(mProfileEditFragmentViewModel.getUserZipcode());
        // edit by sohit fetch address code and set name
        edit_address.setText(mProfileEditFragmentViewModel.getUserAddress());
        state_code =  mProfileEditFragmentViewModel.getUserState();
        if(!state_code.equals(""))
            mProfileEditFragmentViewModel.selectCity(getBaseActivity(),true);
        // remove about edit text
       // edit_about.setText(mProfileEditFragmentViewModel.getUserAbout());

        String image_url = mProfileEditFragmentViewModel.getProfileImg();
        if(image_url != null)
            Glide.with(getContext()).load("http://67.211.223.164:8080"+
                    image_url).placeholder(R.drawable.default_pic).into(circularImageView_img);


    }

    @Override
    public void setDefaultAddress(StateCityOptions stateCityOptions) {
        List<DataOptions> data = stateCityOptions.getData();
          for(DataOptions d : data){
              if(d.getCode().equals(state_code)){
                  Log.d("sohit", "setDefaultAddress: "+d.getName());
                  state_code = d.getCode();
                  edit_state.setText(d.getName());
              }
          }
    }

    @OnClick(R.id.img_left)
    public void onIconBackClick() {

        if(isProfileComplete)
            FragmentUtil.removeFragment(getBaseActivity());
        else
            getBaseActivity().finish();
    }

    // right click submit
    /*@OnClick(R.id.img_right)
    public void onIconDoneClick() {
        showProgress("Updating Profile...");
        mProfileEditFragmentViewModel.uploadProfileImage(getBaseActivity(),uriImage);
    }*/

    @OnClick(R.id.btn_save)
    public void onSaveBtn(){
        showProgress("Updating Profile...");
        mProfileEditFragmentViewModel.uploadProfileImage(getBaseActivity(),uriImage);
    }

    // edit by sohit
    @OnClick(R.id.edit_state)
    public void onselectAddress(){
            mProfileEditFragmentViewModel.selectCity(getBaseActivity(),false);
    }

    @Override
    public void openDailogOptions(StateCityOptions stateCityOptions){
        List<DataOptions> dataOptions = stateCityOptions.getData();
         int size = stateCityOptions.getData().size();
        String name[] = new String[size];

        for(int i=0;i<size;i++){
            name[i] = dataOptions.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Select State");

        builder.setItems(name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_state.setText(dataOptions.get(which).getName());
                state_code = dataOptions.get(which).getCode();
                Log.d("sohit", "onClick: "+dataOptions.get(which).getName());
                Log.d("sohit", "onClick: code  "+dataOptions.get(which).getCode());

            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }




    @OnClick(R.id.profile_edit_frag_change_pic)
    public void selectImage(){

        imagePicker = new ImagePicker(getActivity(),
                this,
                imageUri -> {/*on image picked */
                    Log.d("sohit", "selectImage: uri "+imageUri);

                    Glide.with(getContext()).load(imageUri).placeholder(R.drawable.default_pic).into(circularImageView_img);
                 //   circularImageView_img.setImageURI(imageUri);
                    uriImage = imageUri;
                })
                .setWithImageCrop(
                        1 ,
                        1 );
        imagePicker.choosePicture(true /*show camera intents*/);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode, requestCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);
    }

    @Override
    public void onProfileImageUploaded(String imageServerUrl) {
        updateProfile(imageServerUrl);
    }

    void updateProfile(String photo_url){
        Log.d("sohit", "update: "+photo_url);
        try{
            ProfileDetailModel profileDetailModel = new ProfileDetailModel();
            profileDetailModel.setFirstname(edit_first_name.getText().toString().trim());
            profileDetailModel.setLastname(edit_last_name.getText().toString().trim());
            profileDetailModel.setGender(edit_gender.getText().toString().trim());
            profileDetailModel.setCity(edit_city.getText().toString().trim());
            profileDetailModel.setZipcode(edit_zipcode.getText().toString().trim());

            // edit by sohit
            if(mYear != 0 && mMonth != 0 && mDay !=0){
                profileDetailModel.setDob(mYear+"-"+mMonth+"-"+mDay);
            }else
            {
                    profileDetailModel.setDob(convertdob(edit_dob.getText().toString()));
            }
            profileDetailModel.setMobile1(edit_mobile.getText().toString());
            // edit by sohit
            profileDetailModel.setAddress1(edit_address.getText().toString().trim());
            profileDetailModel.setWeight(edit_weight.getText().toString());
            profileDetailModel.setHeight(edit_height.getText().toString());
            profileDetailModel.setZipcode(edit_zipcode.getText().toString().trim());
            profileDetailModel.setCity(edit_city.getText().toString().trim());
            //   profileDetailModel.setAbout(edit_about.getText().toString());
            if(state_code != null)
                profileDetailModel.setState(state_code);
            // edit by sohit else part
            if(photo_url != null)
                profileDetailModel.setPhoto_url(photo_url);
            else
                profileDetailModel.setPhoto_url(mProfileEditFragmentViewModel.getProfileImg());

            mProfileEditFragmentViewModel.updateProfile(profileDetailModel);
        }catch (Exception e){
            handleError(e);
        }
    }

    // edit by sohit
    public String convertdob(String dob) {
        String ar[] = dob.split("-");
        return ar[2]+"-"+ar[0]+"-"+ar[1];
    }

    @Override
    public void onProfileUpdate() {

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });

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
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // edit by sohit yyyy-mm-dd to mm-dd-yyyy
        edit_dob.setText(mMonth+"-"+mDay+"-"+mYear);
    }

    @Override
    public void handleError(Throwable throwable) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                getBaseActivity().showSnakeBar(throwable.getMessage());
            }
        });
    }
}
