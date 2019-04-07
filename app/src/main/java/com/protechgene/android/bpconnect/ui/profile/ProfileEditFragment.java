package com.protechgene.android.bpconnect.ui.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.DatePickerFragment;
import com.protechgene.android.bpconnect.ui.custom.TimePickerFragment;

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
        TextView txt_title =  getView().findViewById(R.id.txt_title);
        txt_title.setText("Edit Profile");

        getView().findViewById(R.id.img_right).setVisibility(View.VISIBLE);
        ((ImageView)getView().findViewById(R.id.img_right)).setImageResource(R.drawable.ic_action_done);

        String name = mProfileEditFragmentViewModel.getUserName();
        if(name==null && name.equalsIgnoreCase("null"))
            name = "Update Profile Name";
        edit_name.setText(name);
        edit_email.setText(mProfileEditFragmentViewModel.getUserEmail());
        edit_dob.setText(mProfileEditFragmentViewModel.getUserDoB());
        edit_address.setText(mProfileEditFragmentViewModel.getUserAddress());
        edit_mobile.setText(mProfileEditFragmentViewModel.getUserMobile());

    }

    @OnClick(R.id.img_left)
    public void onIconBackClick() {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    @OnClick(R.id.img_right)
    public void onIconDoneClick() {
        showProgress("Updating Profile...");
        mProfileEditFragmentViewModel.updateProfile(edit_name.getText().toString(),"Male",edit_dob.getText().toString(),edit_mobile.getText().toString(),edit_address.getText().toString());
    }

    @Override
    public void onProfileUpdate() {
        hideProgress();
        FragmentUtil.removeFragment(getBaseActivity());
        getBaseActivity().showSnakeBar("Profile Updated");
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

    @Override
    public void onDatePicked(Calendar c) {
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        edit_dob.setText(mYear+"-"+mMonth+"-"+mDay);
    }
}
