package com.protechgene.android.bpconnect.ui.base;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.ui.changepassword.ChangePasswordViewModel;
import com.protechgene.android.bpconnect.ui.devices.DeviceFragmentViewModel;
import com.protechgene.android.bpconnect.ui.devices.DevicesListViewModel;
import com.protechgene.android.bpconnect.ui.devices.PairNewDeviceViewModel;
import com.protechgene.android.bpconnect.ui.devices.PairNewDeviceViewModelBP3N;
import com.protechgene.android.bpconnect.ui.forgotPassword.ForgotPasswordViewModel;
import com.protechgene.android.bpconnect.ui.home.HomeViewModel;
import com.protechgene.android.bpconnect.ui.login.LoginViewModel;
import com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentViewModel;
import com.protechgene.android.bpconnect.ui.profile.ProfileEditFragmentViewModel;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragmentViewModel;
import com.protechgene.android.bpconnect.ui.readingHistory.BpReadingsViewModel;
import com.protechgene.android.bpconnect.ui.readingHistory.ProtocolReadingsViewModel;
import com.protechgene.android.bpconnect.ui.reminder.ReminderViewModel;
import com.protechgene.android.bpconnect.ui.settings.SettingFragmentViewModel;
import com.protechgene.android.bpconnect.ui.signup.SignUpViewModel;
import com.protechgene.android.bpconnect.ui.splash.SplashViewModel;


public class ViewModelFactory implements ViewModelProvider.Factory {


    private static ViewModelFactory viewModelFactory;
    private Repository mRepository;

    private ViewModelFactory(Repository repository) {
        this.mRepository = repository;
    }

    public static ViewModelFactory getInstance(Application application)
    {
        if (viewModelFactory ==null)
        {
            viewModelFactory = new ViewModelFactory(Repository.getInstance(application));
        }
        return viewModelFactory;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SplashViewModel.class)) {
            return (T) new SplashViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(SignUpViewModel.class)) {
            return (T) new SignUpViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(ForgotPasswordViewModel.class)) {
            return (T) new ForgotPasswordViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(ProfileFragmentViewModel.class)) {
            return (T) new ProfileFragmentViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(ProfileEditFragmentViewModel.class)) {
            return (T) new ProfileEditFragmentViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(BpReadingsViewModel.class)) {
            return (T) new BpReadingsViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(PairNewDeviceViewModel.class)) {
            return (T) new PairNewDeviceViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(DeviceFragmentViewModel.class)) {
            return (T) new DeviceFragmentViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(MeasureBPFragmentViewModel.class)) {
            return (T) new MeasureBPFragmentViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(SettingFragmentViewModel.class)) {
            return (T) new SettingFragmentViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(ReminderViewModel.class)) {
            return (T) new ReminderViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(ProtocolReadingsViewModel.class)) {
            return (T) new ProtocolReadingsViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(ChangePasswordViewModel.class)) {
            return (T) new ChangePasswordViewModel(mRepository);
        }else  if (modelClass.isAssignableFrom(PairNewDeviceViewModelBP3N.class)) {
            return (T) new PairNewDeviceViewModelBP3N(mRepository);
        }else  if (modelClass.isAssignableFrom(DevicesListViewModel.class)) {
            return (T) new DevicesListViewModel(mRepository);
        }



        throw new IllegalArgumentException("Unknown class name");
    }
}
