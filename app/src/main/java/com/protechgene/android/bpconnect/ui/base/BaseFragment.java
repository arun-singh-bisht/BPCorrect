package com.protechgene.android.bpconnect.ui.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.GeneralUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder mUnbinder;
    private BaseActivity mActivity;
    private ProgressDialog mProgressDialog;
    private View view;

    protected abstract int layoutRes();
    protected abstract void initialize();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            //activity.onFragmentAttached();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize Progress Dialog to be used by child Fragment
        mProgressDialog = GeneralUtil.getProgressDialog(mActivity, "Loading...");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null) {
            view = inflater.inflate(layoutRes(), container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        initialize();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }

    }


    protected void setTitle(String title) {
        ActionBar supportActionBar = getBaseActivity().getSupportActionBar();
        if(supportActionBar!=null)
            supportActionBar.setTitle(title);
    }
    protected void showProgress(String message) {
        if(message!=null && !message.isEmpty())
            mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }
    protected void hideProgress()
    {
        mProgressDialog.dismiss();
    }

    protected void showToast(String msg) { Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show(); }
    protected void showAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }
}
