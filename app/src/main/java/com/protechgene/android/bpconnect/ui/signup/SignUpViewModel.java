package com.protechgene.android.bpconnect.ui.signup;

import android.text.TextUtils;

import com.protechgene.android.bpconnect.BuildConfig;
import com.protechgene.android.bpconnect.Utils.GeneralUtil;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword.ResetPasswordResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.SignUp.SignUpResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.User;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SignUpViewModel extends BaseViewModel<SignUpNavigator> {


    public SignUpViewModel(Repository repository) {
        super(repository);
    }


    public void registerUser(String email, String password,String confirmPassword)
    {

        Throwable throwable = null;
        if (TextUtils.isEmpty(email) || !GeneralUtil.isValidEmail(email)) {
            throwable = new IllegalArgumentException("Invalid Email Address");
            getNavigator().handleError(throwable);
            return;
        }else if(TextUtils.isEmpty(password))
        {
            throwable = new IllegalArgumentException("Enter Password");
            getNavigator().handleError(throwable);
            return;
        }if(password.length()<8)
        {
            throwable = new IllegalArgumentException("Enter password min 8 character");
            getNavigator().handleError(throwable);
            return;
        }if(!isValidPassword(password))
        {
            throwable = new IllegalArgumentException("Use at least 1 symbols %,1,A,a");
            getNavigator().handleError(throwable);
            return;
        }
        else if(!password.equals(confirmPassword))
        {
            throwable = new IllegalArgumentException("Confirm Password does not match");
            getNavigator().handleError(throwable);
            return;
        }


        disposables.add(getRespository().signUp(email, password)
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
                        if(message!=null && message.equalsIgnoreCase("user Already Exists"))
                        {
                            Throwable throwable = new IllegalArgumentException(message);
                            getNavigator().handleError(throwable);
                        }else {
                            getNavigator().openLoginScreen();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //Uncomment below code for actual error handling.
                        getNavigator().handleError(throwable);
                    }
                }));

    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
