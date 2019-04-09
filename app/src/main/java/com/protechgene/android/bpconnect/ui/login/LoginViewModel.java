package com.protechgene.android.bpconnect.ui.login;

import android.text.TextUtils;
import android.util.Log;


import com.protechgene.android.bpconnect.Utils.GeneralUtil;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.oauth.OauthResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel<LoginNavigator> {


    public LoginViewModel(Repository repository) {
        super(repository);
    }

    public void login(final String email, String password)
    {
        Throwable throwable = new IllegalArgumentException("Invalid Email or Password");
        if (TextUtils.isEmpty(email) || !GeneralUtil.isValidEmail(email) || TextUtils.isEmpty(password)) {
            getNavigator().handleError(throwable);
            return;
        }


        disposables.add(getRespository().oauth("password","test",email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<OauthResponse>() {
                    @Override
                    public void accept(OauthResponse oauthResponse) throws Exception {

                        //Save user
                        Repository respository = getRespository();
                        respository.setCurrentUserEmail(email);
                        respository.setAccessToken(oauthResponse.getValue());
                        respository.setCurrentUserId(oauthResponse.getAdditionalInformation().getId()+"");
                        respository.setIsLoggedIn(true);

                        getNavigator().openMainActivity();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));

    }
}