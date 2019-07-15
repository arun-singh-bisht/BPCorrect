package com.protechgene.android.bpconnect.ui.reminder;



import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.Data;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.GetProtocolResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class HistoryProtocolsViewModel extends BaseViewModel<HistoryProtocolsFragmentNavigator> {


    public HistoryProtocolsViewModel(Repository repository) {
        super(repository);
    }

    public void getHistoryProtocols() {


        String accessToken = getRespository().getAccessToken();
        String currentUserId = getRespository().getCurrentUserId();
        String fromDay = "1546300800"; // 1 January 2019 00:00:00
        String toDay = (System.currentTimeMillis() + (5 * 60 * 60 * 1000)) / 1000 + "";
        String noDay = "30";

        disposables.add(getRespository().getHistoryProtocol(accessToken, currentUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<GetProtocolResponse>() {
                    @Override
                    public void accept(GetProtocolResponse protocolResponse) throws Exception {

                        //Save user Details
                        if (protocolResponse.getValid()) {
                            List<Data> data = protocolResponse.getData();
                            getNavigator().showData(data);
                        } else {
                            getNavigator().handleError(new Throwable("Opps! Some error occured. Pleaase try again."));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        throwable.printStackTrace();
                        getNavigator().handleError(throwable);
                    }
                }));
    }

}
