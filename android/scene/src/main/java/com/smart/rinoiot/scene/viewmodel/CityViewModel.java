package com.smart.rinoiot.scene.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.CityHeadBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.PinyinComparator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityViewModel extends SceneViewModel {
    public CityViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    /** 城市列表数据 */
    private final MutableLiveData<List<CityHeadBean>> cityListLiveData = new MutableLiveData<>();
    /** 搜索数据回调 */
    private final MutableLiveData<List<CityHeadBean>> searchData = new MutableLiveData<>();

    /** 索引列表 */
    private final Map<String, Integer> indexMap = new HashMap<>();
    /** 城市列表bean封装 */
    private final List<CityHeadBean> cityList = new ArrayList<>();
    /** 搜索数据列表 */
    private final List<CityHeadBean> searchList = new ArrayList<>();

    public List<CityHeadBean> getCityList() {
        return cityList;
    }

    public Map<String, Integer> getIndexMap() {
        return indexMap;
    }

    public MutableLiveData<List<CityHeadBean>> getCityListLiveData() {
        return cityListLiveData;
    }

    public MutableLiveData<List<CityHeadBean>> getSearchData() {
        return searchData;
    }

    public void getCityData() {
        List<CityBean> cityData = CacheDataManager.getInstance().getCityList();

        Map<String, List<CityBean>> map = new HashMap<>();
        String a_z = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < a_z.length(); i++) {
            List<CityBean> cityBeans = new ArrayList<>();
            map.put(String.valueOf(a_z.charAt(i)), cityBeans);
        }

        for (int i = 0; i < cityData.size(); i++) {
            CityBean bean = cityData.get(i);
            String matchingStr = (bean.getMatchingStr() == null ? new PinyinComparator().getPingYin(bean.getName()).substring(0, 1).toUpperCase() : bean.getMatchingStr().substring(0, 1).toUpperCase());
//            String matchingStr = (bean.getMatchingStr() == null ? bean.getCityNamePy().substring(0, 1).toUpperCase() : bean.getMatchingStr().substring(0, 1).toUpperCase());
            try {
                map.get(matchingStr).add(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int index = 0;
        for (int i = 0; i < a_z.length(); i++) {
            List<CityBean> cityBeanList = map.get(String.valueOf(a_z.charAt(i)));
            if (cityBeanList != null && !cityBeanList.isEmpty()) {
                this.cityList.add(new CityHeadBean(true, String.valueOf(a_z.charAt(i))));
                indexMap.put(String.valueOf(a_z.charAt(i)), index);
                // 排序
                Collections.sort(cityBeanList, new PinyinComparator());
                for (int j = 0; j < cityBeanList.size(); j++) {
                    cityList.add(new CityHeadBean(cityBeanList.get(j)));
                    index += 1;
                }
                index += 1;
            }
        }

        cityListLiveData.postValue(cityList);
    }

    /** 选择国家地区列表搜索方法 */
    public void searchCity(String s) {
        if (TextUtils.isEmpty(s)) {
            searchList.clear();
            cityListLiveData.postValue(cityList);
        } else {
            searchList.clear();
            for (int i = 0; i < cityList.size(); i++) {
                CityBean bean = cityList.get(i).getCityBean();
                if (bean != null) {
                    if ((!TextUtils.isEmpty(bean.getName().toLowerCase()) &&
                            bean.getName().toLowerCase().contains(s.toLowerCase()))) {
//                            (!TextUtils.isEmpty(bean.getCityNamePy()) &&
//                                    bean.getCityNamePy().toLowerCase().contains(s.toLowerCase())) ||
//                            (!TextUtils.isEmpty(bean.getCityNamePyj()) &&
//                                    bean.getCityNamePyj().toLowerCase().contains(s.toLowerCase()))) {
                        searchList.add(cityList.get(i));
                    }
                }
            }
            searchData.postValue(searchList);
        }
    }
}
