package com.protechgene.android.bpconnect.ui.splash;


import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

public class SplashViewModel extends BaseViewModel<SplashNavigator> {

    public SplashViewModel(Repository repository) {
        super(repository);
    }

    public void nextScreen()
    {

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(getRespository().isLoggedIn())
                {
                    getNavigator().goToHomeScreen();
                }else
                {
                    getNavigator().goToLoginScreen();
                }

            }
        },2*1000);

    }
}
