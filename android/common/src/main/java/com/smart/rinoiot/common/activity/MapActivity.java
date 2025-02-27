package com.smart.rinoiot.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.lxj.xpopup.util.KeyboardUtils;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.adapter.AddressSearchAdapter;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.databinding.ActivityMapBinding;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.LgUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/15
 */
public class MapActivity extends BaseActivity<ActivityMapBinding, BaseViewModel> {

    private boolean isSearch;
    private AMap aMap;
    private GeocodeSearch geocoderSearch;
    private RegeocodeResult regeocodeResult;
    private AddressSearchAdapter adapter;
    private Inputtips inputTips;
    private Marker marker;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_family_location);
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        binding.mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = binding.mapView.getMap();
        }

        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
        ServiceSettings.getInstance().setLanguage(AppUtil.getLanguage(this).contains("zh") ? AMap.CHINESE : AMap.ENGLISH);
        try {
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    if (i == 1000) {
                        LatLng latLng = new LatLng(regeocodeResult.getRegeocodeQuery().getPoint().getLatitude(), regeocodeResult.getRegeocodeQuery().getPoint().getLongitude());
//                        aMap.clear();
                        if (marker != null) {
                            marker.remove();
                        }
                        MapActivity.this.regeocodeResult = regeocodeResult;
                        String address = regeocodeResult.getRegeocodeAddress().getDistrict() + regeocodeResult.getRegeocodeAddress().getTownship();
                        isSearch = false;
                        binding.etAddress.setText(address);
                        isSearch = true;
                        marker = aMap.addMarker(new MarkerOptions().position(latLng).snippet(address));//.snippet("DefaultMarker")
                        marker.showInfoWindow();
                    } else {
                        LgUtils.e("aMap error code --> " + i);
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                }
            });
        } catch (AMapException e) {
            e.printStackTrace();
        }

        //设置希望展示的地图缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(false);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMapLanguage(AppUtil.getLanguage(this).contains("zh") ? AMap.CHINESE : AMap.ENGLISH);
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (geocoderSearch != null) {
                    geocoderSearch.getFromLocationAsyn(new RegeocodeQuery(new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude), 100, GeocodeSearch.AMAP));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        binding.mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        binding.mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void init() {
        adapter = new AddressSearchAdapter(new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);
        binding.etAddress.setOnTextChange(text -> {
            if (!isSearch) return;

            String searchStr = binding.etAddress.getText().trim();
            if (searchStr.length() > 0) {
                inputTips.setQuery(new InputtipsQuery(searchStr, ""));
                inputTips.requestInputtipsAsyn();
            }
        });

        inputTips = new Inputtips(this, new InputtipsQuery(null, null));
        inputTips.setInputtipsListener((list, i) -> {
            if (i == 1000) {
                if (list.size() > 0) {
                    adapter.setNewInstance(list);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                    LgUtils.e("aMap error code --> " + i);
                }
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                LgUtils.e("aMap error code --> " + i);
            }
        });

        adapter.setOnItemClickListener((adapter, view, position) -> {
            Tip tip = MapActivity.this.adapter.getItem(position);
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude()), 17)));
            binding.recyclerView.setVisibility(View.GONE);
            KeyboardUtils.hideSoftInput(binding.recyclerView);
        });
        binding.tvConfirm.setOnClickListener(v -> selectedLocation());
    }

    private void selectedLocation() {
        String address = "";
        double latitude=0, longitude=0;
        if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
            address = regeocodeResult.getRegeocodeAddress().getDistrict() + regeocodeResult.getRegeocodeAddress().getTownship();
        }
        if (regeocodeResult != null && regeocodeResult.getRegeocodeQuery() != null&&regeocodeResult.getRegeocodeQuery().getPoint()!=null) {
            latitude = regeocodeResult.getRegeocodeQuery().getPoint().getLatitude();
            longitude = regeocodeResult.getRegeocodeQuery().getPoint().getLongitude();
        }
        if (!TextUtils.isEmpty(address) && (latitude != 0 || longitude != 0)) {
            Intent data = new Intent();
            data.putExtra("address",address);
            data.putExtra("latitude",latitude);
            data.putExtra("longitude", longitude);
            setResult(RESULT_OK, data);
        }

        finishThis();
    }

    @Override
    public ActivityMapBinding getBinding(LayoutInflater inflater) {
        return ActivityMapBinding.inflate(inflater);
    }
}
