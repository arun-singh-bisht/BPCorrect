package com.protechgene.android.bpconnect.ui.profile;

public interface ProfileEditFragmentNavigator {


    void handleError(Throwable throwable);

    void onProfileImageUploaded(String imageServerUrl);

    void onProfileUpdate();



}
