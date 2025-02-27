package com.smart.device.activity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.smart.device.R;
import com.smart.device.adapter.MtrDeviceCommissioningAdapter;
import com.smart.device.databinding.ActivityDeviceAutomaticCommissioningBinding;
import com.smart.device.viewmodel.MtrDeviceCommissioningViewModel;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.matter.model.BatchDeviceBindingStatus;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableNode;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableNodesEvent;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableNodesStatus;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @author edwin
 */
public class DeviceAutomaticCommissioningActivity extends BaseActivity<ActivityDeviceAutomaticCommissioningBinding, MtrDeviceCommissioningViewModel> {

    private MtrDeviceCommissioningAdapter mtrDeviceCommissioningAdapter;

    private BatchDeviceBindingStatus commissioningStatus = new BatchDeviceBindingStatus.Discovering();

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        // initialize recycler view
        mtrDeviceCommissioningAdapter = new MtrDeviceCommissioningAdapter();
        binding.rvMatterDevices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMatterDevices.setAdapter(mtrDeviceCommissioningAdapter);

        // get home identifier
        String homeId = CacheDataManager.getInstance().getCurrentHomeId();
        if(StringUtil.isNotBlank(homeId)){
            mViewModel.requestDeviceDiscovery(homeId);
        }

        // setup the listeners
        setupListeners();

        // setup the observers
        setupObservers();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupObservers() {
        // observe the discoverable devices list.
        mViewModel.getDiscoverableDevicesLiveData().observe(this, discoverableDevices -> {
            // update the adapter's data.
            if(null == discoverableDevices){
                // clear recycler view
                return;
            }

            // set discoverable devices
            mtrDeviceCommissioningAdapter.setDevices(discoverableDevices);
            mtrDeviceCommissioningAdapter.notifyDataSetChanged();

            // start device commissioning
            mViewModel.pairNextDevice();
        });

        // observe the device commissioning status.
        mViewModel.getBindingStatusChangeLiveData().observe(this, position -> {
            // update the device view in the adapter.
            mtrDeviceCommissioningAdapter.notifyItemChanged(position);
        });

        // observe the jobs
        mViewModel.getNextDevicePairingJobLiveData().observe(this, jobId-> {
            // the work must continue
            mViewModel.pairNextDevice();
        });

        // observe batch device commissioning status
        mViewModel.getBatchCommissioningStatus().observe(this, status -> {
            commissioningStatus = status;
            Log.d(TAG, "setupObservers: status =" + new Gson().toJson(status));
            if (status instanceof BatchDeviceBindingStatus.Error) {
                mViewModel.hideLoading();
            } else if(status instanceof BatchDeviceBindingStatus.Discovering){
                mViewModel.showLoading();
                binding.btnConfirm.setVisibility(View.INVISIBLE);
            } else if (status instanceof BatchDeviceBindingStatus.Pairing) {
                mViewModel.hideLoading();
            }else if(status instanceof BatchDeviceBindingStatus.Paired) {
                binding.btnConfirm.setVisibility(View.VISIBLE);
            }else if(status instanceof BatchDeviceBindingStatus.Uploading) {
                mViewModel.showLoading();
            } else if(status instanceof BatchDeviceBindingStatus.Completed) {
                mViewModel.hideLoading();
                AppManager.getInstance().finishActivity(DeviceCommissioningMethodActivity.class);
                this.finish();
            }
        });
    }

    private void setupListeners() {
        // register back pressed
        binding.ivBack.setOnClickListener(v -> {
            if(commissioningStatus instanceof BatchDeviceBindingStatus.Error) {
                onBackPressed();
            } else if (commissioningStatus instanceof BatchDeviceBindingStatus.Paired) {
                if(((BatchDeviceBindingStatus.Paired) commissioningStatus).isSuccessful()){
                    showCancelDialog();
                }else{
                    // ToDo() show try again dialog
                    onBackPressed();
                }
            }else{
                showCancelDialog();
            }
        });

        // register confirm
        binding.btnConfirm.setOnClickListener(v -> {
            if (((BatchDeviceBindingStatus.Paired) commissioningStatus).isSuccessful()) {
                mViewModel.uploadCommissionedDevices(
                        CacheDataManager.getInstance().getCurrentHomeId()
                );
            }else{
                AppManager.getInstance().finishActivity(DeviceCommissioningMethodActivity.class);
                this.finish();
            }
        });
    }

    private void showCancelDialog(){
        DialogOnListener listener = new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                mViewModel.clearDiscoverableDevices();
                onBackPressed();
            }
        };

        DialogUtil.showNormalMsg(
                this,
                getString(R.string.rino_device_matter_cancel_title),
                getString(R.string.rino_device_matter_cancel_hint), listener
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public ActivityDeviceAutomaticCommissioningBinding getBinding(LayoutInflater inflater) {
        return ActivityDeviceAutomaticCommissioningBinding.inflate(inflater);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusNotify(MtrDiscoverableNodesEvent nodesEvent) {
        Log.d(TAG, "onEventBusNotify: devices=" + new Gson().toJson(nodesEvent));
        if(nodesEvent.getStatus().equals(MtrDiscoverableNodesStatus.DISCOVERED)){
            String homeId = CacheDataManager.getInstance().getCurrentHomeId();
            List<MtrDiscoverableNode> nodes = nodesEvent.getNodes();
            mViewModel.processDiscoverableNodes(homeId, nodes);
        }else if(nodesEvent.getStatus().equals(MtrDiscoverableNodesStatus.NOT_DISCOVERED)){
            mViewModel.hideLoading();
            commissioningStatus = new BatchDeviceBindingStatus.Error();
            ToastUtil.showErrorMsg(R.string.rino_device_matter_no_devices_discovered);
        }else if(nodesEvent.getStatus().equals(MtrDiscoverableNodesStatus.ERROR)){
            mViewModel.hideLoading();
            commissioningStatus = new BatchDeviceBindingStatus.Error();
            ToastUtil.showErrorMsg(R.string.rino_device_matter_processing_failed);
        }
    }
}
