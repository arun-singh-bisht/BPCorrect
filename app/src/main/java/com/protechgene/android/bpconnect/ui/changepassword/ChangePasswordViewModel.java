package com.protechgene.android.bpconnect.ui.changepassword;

import android.os.AsyncTask;
import android.util.Log;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword.ResetPasswordResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragmentNavigator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChangePasswordViewModel extends BaseViewModel<ChangePasswordNavigator> {


    public ChangePasswordViewModel(Repository repository) {
        super(repository);
    }

    public void changePassword(String password,String confirm_password){

        Throwable throwable =null;
        if(password.equals("") || password.isEmpty())
        {
            throwable = new IllegalArgumentException("Enter new password");
            getNavigator().handleError(throwable);
            return;
        }
        if(password.length()<8)
        {
            throwable = new IllegalArgumentException("Enter password min 8 character");
            getNavigator().handleError(throwable);
            return;
        }
        if(!isValidPassword(password))
        {
            throwable = new IllegalArgumentException("Use at least 1 symbols %,1,A,a");
            getNavigator().handleError(throwable);
            return;
        }
        if(confirm_password.equals("") || confirm_password.isEmpty())
        {
            throwable = new IllegalArgumentException("Enter your confirm password");
            getNavigator().handleError(throwable);
            return;
        }
        if(!confirm_password.equals(password))
        {
            throwable = new IllegalArgumentException(" confirm password does not match");
            getNavigator().handleError(throwable);
            return;
        }
              String userid = getRespository().getCurrentUserId();
              String accessToken = getRespository().getAccessToken();

        disposables.add(getRespository().changePassword(password,accessToken,userid)
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
                        Log.d("sohit",message);
                        getNavigator().navigateToLogin(message);
                        logout();
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


    public void logout(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getRespository().clearSharedPref();
                getRespository().deleteAllHealthRecords();
                getRespository().deleteAllProtocol();
            }
        });
    }


}
