package com.protechgene.android.bpconnect.ui.profile;

import com.protechgene.android.bpconnect.data.remote.responseModels.cityandstate.StateCityOptions;

public interface ProfileEditFragmentNavigator {


    void handleError(Throwable throwable);

    void onProfileImageUploaded(String imageServerUrl);

    void onProfileUpdate();

    void openDailogOptions(StateCityOptions stateCityOptions);

    void setDefaultAddress(StateCityOptions stateCityOptions);

}
