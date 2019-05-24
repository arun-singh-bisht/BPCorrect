package com.protechgene.android.bpconnect.ui.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileFragment extends BaseFragment implements ProfileFragmentNavigator  {

    public static final String FRAGMENT_TAG = "ProfileFragment";
    private ProfileFragmentViewModel mProfileFragmentViewModel;

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.img_right)
    ImageView img_right;

    @BindView(R.id.text_email)
    TextView text_email;
    @BindView(R.id.text_dob)
    TextView text_dob;
    @BindView(R.id.text_address)
    TextView text_address;
    @BindView(R.id.text_mobile)
    TextView text_mobile;
    @BindView(R.id.text_profile_name)
    TextView text_profile_name;
    @BindView(R.id.text_height_value)
    TextView text_height_value;
    @BindView(R.id.text_weight_value)
    TextView text_weight_value;
    @BindView(R.id.text_age_value)
    TextView text_age_value;
    @BindView(R.id.text_gender_value)
    TextView text_gender_value;
    @BindView(R.id.text_about)
    TextView text_about;

    @BindView(R.id.profile_pic_cir_img)
    CircularImageView profile_pic;


    @Override
    protected int layoutRes() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initialize() {

        mProfileFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(ProfileFragmentViewModel.class);
        mProfileFragmentViewModel.setNavigator(this);

        initView();
    }

    private void initView()
    {
        getView().findViewById(R.id.root_header).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView)getView().findViewById(R.id.img_left)).setColorFilter(Color.argb(255, 255, 255, 255));
        ((ImageView)getView().findViewById(R.id.img_right)).setColorFilter(Color.argb(255, 255, 255, 255));
        ((ImageView)getView().findViewById(R.id.img_right)).setImageResource(R.drawable.ic_action_edit);

        txt_title.setText("Profile");
        txt_title.setTextColor(Color.parseColor("#ffffff"));

        img_right.setVisibility(View.VISIBLE);

        showProfileDetails();
    }

    @OnClick(R.id.img_left)
    public void onIconBackClick() {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    @OnClick(R.id.img_right)
    public void onIconEditClick() {
        FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,new ProfileEditFragment(),ProfileEditFragment.FRAGMENT_TAG,"ProfileEditFragmentTransition");
    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void showProfileDetails() {
        String name = mProfileFragmentViewModel.getUserFirstName();
        if(name==null || name.equalsIgnoreCase("null") || name.isEmpty())
            name = "BPCorrect User";
        else
            name =  mProfileFragmentViewModel.getUserFirstName() +" "+ mProfileFragmentViewModel.getUserLastName();
        text_profile_name.setText(name);
        text_email.setText(mProfileFragmentViewModel.getUserEmail());
        text_dob.setText(mProfileFragmentViewModel.getUserDoB());

        String address = mProfileFragmentViewModel.getUserAddress()+" "+mProfileFragmentViewModel.getUserCity()+" "+mProfileFragmentViewModel.getUserState()+" "+mProfileFragmentViewModel.getUserZipcode();
        text_address.setText(address);
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
    }
}
