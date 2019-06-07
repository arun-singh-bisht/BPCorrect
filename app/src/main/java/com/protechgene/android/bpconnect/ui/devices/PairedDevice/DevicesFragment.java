package com.protechgene.android.bpconnect.ui.devices.PairedDevice;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.models.BPDeviceModel;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.devices.DeviceTypes.DevicesListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DevicesFragment extends BaseFragment implements DeviceFragmentNavigator {

    public static final String FRAGMENT_TAG = "DevicesFragment";
    private DevicesAdapter bpReadingAdapter;
    private DeviceFragmentViewModel deviceFragmentViewModel;

    @BindView(R.id.txt_title)
    TextView title;
    @BindView(R.id.img_right)
    View img_right;
    @BindView(R.id.text_empty_msg)
    View text_empty_msg;



    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_tutorial;
    }

    @Override
    protected void initialize() {
        deviceFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(DeviceFragmentViewModel.class);
        deviceFragmentViewModel.setNavigator(this);

        initView();
    }

    private void initView()
    {
        title.setText("Devices");
        img_right.setVisibility(View.VISIBLE);
        text_empty_msg.setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bpReadingAdapter = new DevicesAdapter(new ArrayList<BPDeviceModel>());
        recyclerView.setAdapter(bpReadingAdapter);

        //List<BPDeviceModel> modelList = deviceFragmentViewModel.getPairedDeviceList();
        //bpReadingAdapter.setData(modelList);
        deviceFragmentViewModel.findAllPairedDevices(getBaseActivity());

    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    @OnClick(R.id.img_right)
    public void onAddIconClick()
    {
        //FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,new PairNewDevicesFragment(),PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));


        //edit by sohit
       FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,new DevicesListFragment(),DevicesListFragment.FRAGMENT_TAG,"DevicesListFragment");


    }


    // edit by sohit disable function and send to instruction devices list
   /* private void OpenPairNewDevices(){
        GpsUtils gpsUtils = new GpsUtils(getBaseActivity());
        gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                if(isGPSEnable) {
                    openScanningScreen(0);
                    //showDeviceMenu();
                }
            }
        });
    }


    private void openScanningScreen(int forModel)
    {
        PairNewDevicesFragment pairNewDevicesFragment = new PairNewDevicesFragment();
        Bundle bundle = new Bundle();


        if(forModel==0)
        {
            //for A&D Device
            bundle.putString("deviceModel","A&D__651BLE");
            pairNewDevicesFragment.setArguments(bundle);
            FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,pairNewDevicesFragment,PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
        }else if(forModel==1)
        {
            //for iHealth BP3L
            boolean isPass = deviceFragmentViewModel.isAuthorizeForBP3L(getBaseActivity());
            if (isPass) {
                bundle.putString("deviceModel","iHealth_BP3L");
                pairNewDevicesFragment.setArguments(bundle);
                FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,pairNewDevicesFragment,PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
            } else {
                getBaseActivity().showSnakeBar("Not authorized to access this device.");
            }
        }
    }*/

    @Override
    public void handleError(Throwable throwable) {

        getBaseActivity().showSnakeBar(throwable.getMessage());
    }

    @Override
    public void bluetoothNotSupported(String msg) {
        getBaseActivity().showSnakeBar(msg);
        img_right.setVisibility(View.GONE);
    }

    @Override
    public void turningOnBluetooth() {
        showProgress("Turning On bluetooth...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                deviceFragmentViewModel.findAllPairedDevices(getBaseActivity());
            }
        },1500);
    }

    @Override
    public void pairedDevices(List<BPDeviceModel> bpDeviceList) {



        if (bpDeviceList.size()==0)
            text_empty_msg.setVisibility(View.VISIBLE);
        else
             bpReadingAdapter.setData(bpDeviceList);
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GpsUtils.GPS_REQUEST && resultCode==getBaseActivity().RESULT_OK)
        {
            showDeviceMenu();
            //openScanningScreen(0);
        }


        Log.d("onActivityResult",""+requestCode+" "+resultCode);
    }


    private void showDeviceMenu()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Select Model");

        // add a list
        final String[] gender = {"A&D UA-651BLE", "iHealth BP3L"};
        builder.setItems(gender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openScanningScreen(which);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/
}
