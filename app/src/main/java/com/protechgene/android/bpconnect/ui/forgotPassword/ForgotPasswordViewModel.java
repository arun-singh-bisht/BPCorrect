package com.protechgene.android.bpconnect.ui.forgotPassword;

import android.text.TextUtils;
import android.util.Log;

import com.protechgene.android.bpconnect.Utils.GeneralUtil;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword.ResetPasswordResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ForgotPasswordViewModel extends BaseViewModel<ForgotPasswordNavigator> {


    public ForgotPasswordViewModel(Repository repository) {
        super(repository);
    }

    public void resetPassword(String email)
    {
        if (TextUtils.isEmpty(email) || !GeneralUtil.isValidEmail(email))
        {
            Throwable throwable = new IllegalArgumentException("Invalid Email Address");
            getNavigator().handleError(throwable);
            return;
        }

        disposables.add(getRespository().resetPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<ResetPasswordResponse>() {
                    @Override
                    public void accept(ResetPasswordResponse resetPasswordResponse) throws Exception {

                        String message = resetPasswordResponse.getData().get(0).getMessage();
                        Log.d("resetPassword",message);
                        getNavigator().redirectToLoginPage();
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
