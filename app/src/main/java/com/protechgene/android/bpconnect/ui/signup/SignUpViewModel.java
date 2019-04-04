package com.protechgene.android.bpconnect.ui.signup;

import android.text.TextUtils;

import com.protechgene.android.bpconnect.BuildConfig;
import com.protechgene.android.bpconnect.Utils.GeneralUtil;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.User;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SignUpViewModel extends BaseViewModel<SignUpNavigator> {


    public SignUpViewModel(Repository repository) {
        super(repository);
    }

    public boolean isEmailAndPasswordValid(String email, String password,String confirmPassword) {
        // validate email and password
        if (TextUtils.isEmpty(email))
            return false;
        if (!GeneralUtil.isValidEmail(email))
            return false;
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword))
            return false;
        if(!password.equals(confirmPassword))
            return false;
        return true;
    }


    public void registerUser(String email, String password)
    {

        disposables.add(getRespository().signUp(BuildConfig.ApiKey,email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {

                        //Save user credentials
                        getRespository().setCurrentUserId(user.getUserId());
                        getRespository().setCurrentUserEmail(user.getEmail());
                        getRespository().setCurrentUserName(user.getUserName());
                        getRespository().setCurrentUserProfilePicUrl(user.getProfilePic());
                        getRespository().setAccessToken(user.getAccessToken());
                        getRespository().setIsLoggedIn(true);

                        getNavigator().openHomeScreen();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //Uncomment below code for actual error handling.
                        getNavigator().handleError(throwable);
                    }
                }));

    }
}
