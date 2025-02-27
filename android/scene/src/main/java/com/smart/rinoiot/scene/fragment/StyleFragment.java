package com.smart.rinoiot.scene.fragment;

import android.view.LayoutInflater;

import androidx.recyclerview.widget.GridLayoutManager;

import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.scene.adapter.ColorSelectAdapter;
import com.smart.rinoiot.scene.adapter.IconSelectAdapter;
import com.smart.rinoiot.scene.bean.ColorBean;
import com.smart.rinoiot.scene.bean.IconItemBean;
import com.smart.rinoiot.scene.databinding.FragmentSceneStyleBinding;
import com.smart.rinoiot.scene.viewmodel.SceneConfigViewModel;

import java.util.ArrayList;
import java.util.List;

public class StyleFragment extends BaseFragment<FragmentSceneStyleBinding, SceneConfigViewModel> {

    private final boolean isColor;
    private String value;

    private final String[] coverIconList = {"https://images.tuyaus.com/smart/rule/glyphicons/click.png","https://images.tuyaus.com/smart/rule/glyphicons/curtain.png","https://images.tuyaus.com/smart/rule/glyphicons/holiday.png","https://images.tuyaus.com/smart/rule/glyphicons/home.png","https://images.tuyaus.com/smart/rule/glyphicons/hourglass.png","https://images.tuyaus.com/smart/rule/glyphicons/information.png","https://images.tuyaus.com/smart/rule/glyphicons/label.png","https://images.tuyaus.com/smart/rule/glyphicons/leaveHome.png","https://images.tuyaus.com/smart/rule/glyphicons/light.png","https://images.tuyaus.com/smart/rule/glyphicons/moon.png","https://images.tuyaus.com/smart/rule/glyphicons/position.png","https://images.tuyaus.com/smart/rule/glyphicons/rain.png","https://images.tuyaus.com/smart/rule/glyphicons/rest.png","https://images.tuyaus.com/smart/rule/glyphicons/shield.png","https://images.tuyaus.com/smart/rule/glyphicons/sun.png","https://images.tuyaus.com/smart/rule/glyphicons/time.png","https://images.tuyaus.com/smart/rule/glyphicons/water.png","https://images.tuyaus.com/smart/rule/glyphicons/work.png"};
    private final String[] colorList = {"#DDDFFC", "#C7F1DF", "#D4DEF4", "#CEF9F2", "#E4E8F8", "#E2C0C0", "#FCF3E7", "#EEECD2", "#F9E9DF", "#FDD0E9", "#DFF6DB", "#D6F5F7"};

    public StyleFragment(boolean isColor) {
        this.isColor = isColor;
    }

    @Override
    public void init() {
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), isColor ? 4 : 6));

        if (isColor) {
            value = mViewModel.getSceneBean().getBgColor();
            List<ColorBean> dataArray = new ArrayList<>();
            ColorBean item;
            for (int i = 0; i < colorList.length; i++) {
                item = new ColorBean();
                item.setColorRes(colorList[i]);
                item.setSelect(i == 0);
                dataArray.add(item);
            }

            ColorSelectAdapter colorSelectAdapter = new ColorSelectAdapter(dataArray);
            binding.recyclerView.setAdapter(colorSelectAdapter);

            colorSelectAdapter.setOnItemClickListener((adapter, view, position) -> {
                List<ColorBean> dataArray1 = colorSelectAdapter.getData();
                for (int i = 0; i < dataArray1.size(); i++) {
                    ColorBean item1 = dataArray1.get(i);
                    item1.setSelect(i == position);
                }
                value = colorSelectAdapter.getData().get(position).getColorRes();
                colorSelectAdapter.notifyDataSetChanged();
            });
        } else {
            value = mViewModel.getSceneBean().getCoverUrl();
            List<IconItemBean> dataArray = new ArrayList<>();
            IconItemBean item;
            for (int i = 0; i < coverIconList.length; i++) {
                item = new IconItemBean();
                item.setIconUrl(coverIconList[i]);
                item.setSelect(i == 0);
                dataArray.add(item);
            }

            IconSelectAdapter iconSelectAdapter = new IconSelectAdapter(dataArray);
            binding.recyclerView.setAdapter(iconSelectAdapter);

            iconSelectAdapter.setOnItemClickListener((adapter, view, position) -> {
                List<IconItemBean> dataArray12 = iconSelectAdapter.getData();
                for (int i = 0; i < dataArray12.size(); i++) {
                    IconItemBean item12 = dataArray12.get(i);
                    item12.setSelect(i == position);
                }
                value = iconSelectAdapter.getData().get(position).getIconUrl();
                iconSelectAdapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public FragmentSceneStyleBinding getBinding(LayoutInflater inflater) {
        return FragmentSceneStyleBinding.inflate(inflater);
    }

    public String getValue() {
        return value;
    }
}
