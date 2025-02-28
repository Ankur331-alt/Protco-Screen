package com.smart.rinoiot.center.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.rino.IPCBusinessPluginManager;
import com.smart.rinoiot.center.activity.IpCameraControlPanelActivity;
import com.smart.rinoiot.center.adapter.IpcCameraAdapter;
import com.smart.rinoiot.center.viewmodel.IpCameraDashboardViewModel;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.ipcimpl.RinoIPCContainerView;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentIpCameraDashboardBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

/**
 * @author edwin
 */
public class IpCameraDashboardFragment extends BaseFragment<FragmentIpCameraDashboardBinding, IpCameraDashboardViewModel> {
    private boolean isVisibleToUser;

    /**
     * The adapter
     */
    private IpcCameraAdapter ipcCameraAdapter;

    /**
     * The list of current cams
     */


    @Override
    public void init() {
        EventBus.getDefault().register(this);

        // init the adapter
//        ipCameraDashboardAdapter = new IpCameraDashboardAdapter(requireContext(), mIpCameras);
        ipcCameraAdapter = new IpcCameraAdapter();

        // initialize the recycler view
        binding.rvCameras.setAdapter(ipcCameraAdapter);
        binding.rvCameras.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        // setup observers
        setupObservers();

        // setup listeners;
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        isFirst = false;
        if (isVisibleToUser) {
            createIpCameraStreams();
        }
    }

    @SuppressWarnings("all")
    private void setupListeners() {
        if (null == ipcCameraAdapter) {
            return;
        }
        ipcCameraAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                RinoIPCContainerView ipcContainerView = view.findViewById(R.id.ipc_cv_video_stream);
                DeviceInfoBean item = (DeviceInfoBean) adapter.getItem(position);
                if (ipcContainerView == null || item == null || TextUtils.isEmpty(item.getId()) || item.isClick())
                    return;
                String params = "{\"che.audio.custom_payload_type\":0}";
                ipcContainerView.fetchToken(item.getId(), item.getName(), params, false, null);
                item.setClick(true);
                adapter.notifyItemChanged(position, item);
            }
        });
        ipcCameraAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                DeviceInfoBean item = (DeviceInfoBean) adapter.getItem(position);
                if (item == null || TextUtils.isEmpty(item.getId())) return;
                if (view.getId() == R.id.ivIpcPause) {
                    IPCBusinessPluginManager.create().snapshot(getContext(), item.getId());
                    item.setClick(false);
                    AppExecutors.getInstance().delayedThread().schedule(new Runnable() {
                        @Override
                        public void run() {
                            IPCBusinessPluginManager.create().unregisterIPCApi(item.getId(), true);
                            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemChanged(position, item);
                                }
                            });
                        }
                    }, 300, TimeUnit.MILLISECONDS);
                } else if (view.getId() == R.id.tvEnterPanel) {//进入面板
                    stopIpCameraStreams();
//                    HomeActivity act = (HomeActivity) getActivity();
//                    act.changeForeground(false);
                    // ToDo() stop the camera feeds
                    Intent intent = new Intent(requireContext(), IpCameraControlPanelActivity.class);
                    intent.putExtra(IpCameraControlPanelActivity.DEVICE_ID_EXTRA_KEY, item.getId());
                    intent.putExtra(IpCameraControlPanelActivity.DEVICE_NAME_EXTRA_KEY, item.getName());
                    startActivity(intent);
                }
            }
        });
//        ipCameraDashboardAdapter.setItemClickListener(new IpCameraDashboardAdapterInterface.ItemClickListener() {
//            @Override
//            public void onClick(RinoIPCContainerView ipcContainerView, DeviceInfoBean deviceInfo) {
//                IPCBusinessPluginManager.create().unregisterAllIPCApi();
//                ipcContainerView.fetchToken(deviceInfo.getId(), deviceInfo.getName(), null, false, null);
//
//            }
//
//            @Override
//            public void onChildClick(int position, DeviceInfoBean deviceInfoBean) {
//                IpCameraDashboardFragment.this.stopIpCameraStreams();
//                HomeActivity act = (HomeActivity) getActivity();
//                act.changeForeground(false);
//                // ToDo() stop the camera feeds
//                Intent intent = new Intent(requireContext(), IpCameraControlPanelActivity.class);
//                intent.putExtra(IpCameraControlPanelActivity.DEVICE_ID_EXTRA_KEY, deviceInfoBean.getId());
//                intent.putExtra(IpCameraControlPanelActivity.DEVICE_NAME_EXTRA_KEY, deviceInfoBean.getName());
//                startActivity(intent);
//            }
//        });
    }

    private boolean isFirst = true;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (!isFirst && isAdded())
                createIpCameraStreams();
        } else {
            stopIpCameraStreams();
        }
    }

    @SuppressWarnings("all")
    private void setupObservers() {
        // observe the ip cameras
        mViewModel.getIpCamerasLiveData().observe(this, ipCameras -> {
            /**
             *  Incremental updates may be required here
             */
            if (ipcCameraAdapter != null)
                ipcCameraAdapter.setList(ipCameras);
        });
    }

    @Override
    public FragmentIpCameraDashboardBinding getBinding(LayoutInflater inflater) {
        return FragmentIpCameraDashboardBinding.inflate(inflater);
    }

    public void createIpCameraStreams() {
        AssetBean assetBean = CacheDataManager.getInstance().getCurrentFamily();
        if (null == assetBean) {
            return;
        }
        // stop any streams that are open.
        stopIpCameraStreams();
        // fetch a new set of cams
        mViewModel.fetchCameras(assetBean.getId());
    }

    @SuppressWarnings("all")
    public void stopIpCameraStreams() {
        // update rcycler view
        // unregister all the cams.
        IPCBusinessPluginManager.create().unregisterAllIPCApi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventBusNotify(IpCameraDashboardEvent event) {
//        if (event.equals(IpCameraDashboardEvent.OPEN_STREAMS)) {
//            createIpCameraStreams();
//            inFocus = true;
//        } else if (event.equals(IpCameraDashboardEvent.CLOSE_STREAMS)) {
//            stopIpCameraStreams();
//            inFocus = false;
//        }
//}

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventBusNotify(DeviceEvent deviceEvent) {
//        if (deviceEvent.getType() != DeviceEvent.Type.CHANGE_FAMILY) {
//             return;
//        }
//
//        if (!FamilyChangeEventTarget.REFRESH_DEVICES.equals(deviceEvent.getObj())){
//            return;
//        }
//
//        if(inFocus){
//            // recreate the streams when the devices have been updated.
//            createIpCameraStreams();
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusNotify(DeviceEvent deviceEvent) {
        if (deviceEvent.getType() == DeviceEvent.Type.CHANGE_FAMILY_NEW) {
            createIpCameraStreams();
        }
    }
}
