package com.smart.device.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.device.R;
import com.smart.device.adapter.MtrDeviceCommissioningAdapter.DeviceViewHolder;
import com.smart.device.databinding.ItemDeviceAutomaticCommissioningBinding;
import com.smart.rinoiot.common.matter.model.DeviceBindingStatus;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableDevice;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author edwin
 */
public class MtrDeviceCommissioningAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private List<MtrDiscoverableDevice> mDevices;

    public MtrDeviceCommissioningAdapter() {
        this.mDevices = new ArrayList<>();
    }

    public void setDevices(List<MtrDiscoverableDevice> mDevices) {
        this.mDevices = mDevices;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DeviceViewHolder(ItemDeviceAutomaticCommissioningBinding.inflate(inflater));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bindData(this.mDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return null == this.mDevices ? 0 : this.mDevices.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {

        ItemDeviceAutomaticCommissioningBinding mBinding;

        public DeviceViewHolder(@NonNull ItemDeviceAutomaticCommissioningBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindData(MtrDiscoverableDevice device) {
            // set device icon
            ImageLoaderUtils.getInstance().bindImageUrl(
                    device.getIcon(), mBinding.ivDeviceIcon
            );

            // set device name
            mBinding.tvDeviceName.setText(device.getName());

            if(device.getStatus() instanceof DeviceBindingStatus.InProgress){
                // show the progress.
                mBinding.sInProgress.setVisibility(View.VISIBLE);
                mBinding.llStatus.setVisibility(View.GONE);
                mBinding.btnTryAgain.setVisibility(View.GONE);
            }else if(device.getStatus() instanceof  DeviceBindingStatus.Failed){
                // show the failure.
                mBinding.sInProgress.setVisibility(View.GONE);
                mBinding.llStatus.setVisibility(View.VISIBLE);
                mBinding.btnTryAgain.setVisibility(View.VISIBLE);

                // set text
                mBinding.tvStatus.setText(R.string.rino_device_matter_pairing_failure);
                mBinding.tvStatus.setTextColor(mBinding.getRoot().getContext().getColor(
                        R.color.cen_information_unread_tip_color
                ));
                mBinding.ivStatus.setImageDrawable(AppCompatResources.getDrawable(
                        mBinding.getRoot().getContext(), R.drawable.ic_delete_error
                ));
            }else if (device.getStatus() instanceof  DeviceBindingStatus.Success){
                // show success.
                mBinding.sInProgress.setVisibility(View.GONE);
                mBinding.llStatus.setVisibility(View.VISIBLE);
                mBinding.btnTryAgain.setVisibility(View.GONE);

                // set text
                mBinding.tvStatus.setText(R.string.rino_device_matter_pairing_success);
                mBinding.tvStatus.setTextColor(mBinding.getRoot().getContext().getColor(
                        R.color.cen_device_config_paired_color
                ));
                mBinding.ivStatus.setImageDrawable(AppCompatResources.getDrawable(
                        mBinding.getRoot().getContext(), R.drawable.ic_tick_success
                ));
            }
        }
    }
}
