package com.protechgene.android.bpconnect.ui.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;

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
        String name = mProfileFragmentViewModel.getUserName();
        if(name==null && name.equalsIgnoreCase("null"))
            name = "Update Profile Name";
        text_profile_name.setText(mProfileFragmentViewModel.getUserName());
        text_email.setText(mProfileFragmentViewModel.getUserEmail());
        text_dob.setText(mProfileFragmentViewModel.getUserDoB());
        text_address.setText(mProfileFragmentViewModel.getUserAddress());
        text_mobile.setText(mProfileFragmentViewModel.getUserMobile());
    }
}
