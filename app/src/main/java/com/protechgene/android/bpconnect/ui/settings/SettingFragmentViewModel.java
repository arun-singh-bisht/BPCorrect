package com.protechgene.android.bpconnect.ui.settings;


import android.os.AsyncTask;
import android.util.Log;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.sharereadingprovider.ShareReading;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragmentNavigator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class SettingFragmentViewModel extends BaseViewModel<SettingFragmentNavigator> {


    public SettingFragmentViewModel(Repository repository) {
        super(repository);
    }

    public void signOut()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getRespository().clearSharedPref();
                getRespository().deleteAllHealthRecords();
                getRespository().deleteAllProtocol();
                getNavigator().onSignOut();
            }
        });

    }

    public void fetchSwitcher(String request_type,Boolean status){
          String userid = getRespository().getCurrentUserId();
          String access_token = getRespository().getAccessToken();
         disposables.add(getRespository().getShareReading(userid,access_token,request_type,status)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .doOnSubscribe(new Consumer<Disposable>() {
                     @Override
                     public void accept(Disposable disposable) throws Exception {

                     }
                 })
                 .subscribe(new Consumer<ShareReading>() {
                     @Override
                     public void accept(ShareReading profileResponse) throws Exception {


                         if(request_type.equals("get")){
                                int status = profileResponse.getData().get(0).getStatus();
                                 Boolean isChecked;
                                 if(status==1){
                                     isChecked = true;
                                 }else {
                                     isChecked = false;
                                 }
                             Log.d("sohit", "accept: isCheaked : "+isChecked);
                                 getNavigator().fetchSwitcher(isChecked);
                         }else{
                             String msg = profileResponse.getData().get(0).getMessage();
                             Log.d("sohit", "accept: msg : "+msg);
                             getNavigator().snackBar(msg);
                         }

                         //Save user Details

                     }
                 }, new Consumer<Throwable>() {
                     @Override
                     public void accept(Throwable throwable) throws Exception {

                         getNavigator().handleError(throwable);
                     }
                 }));
    }

    public String getOrgName(){
       return getRespository().getPrefKeyOrgName();
    }
    public void setOrgName(String orgName){
        getRespository().setPrefKeyOrgName(orgName);
    }
}
