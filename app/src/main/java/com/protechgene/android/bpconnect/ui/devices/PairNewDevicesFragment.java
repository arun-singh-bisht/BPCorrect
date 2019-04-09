package com.protechgene.android.bpconnect.ui.devices;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.skyfishjy.library.RippleBackground;

public class PairNewDevicesFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = "PairNewDevicesFragment";
    private DevicesAdapter bpReadingAdapter;
    RippleBackground rippleBackground;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_pair_new_devices;
    }

    @Override
    protected void initialize() {
        initView();
    }


    private void initView()
    {
        TextView txt_title =  getView().findViewById(R.id.txt_title);
        txt_title.setText("Pair New Device");

        rippleBackground=(RippleBackground)getView().findViewById(R.id.content);
        ImageView imageView=(ImageView)getView().findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rippleBackground.stopRippleAnimation();

    }
}
