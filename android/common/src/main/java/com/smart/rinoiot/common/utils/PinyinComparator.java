package com.smart.rinoiot.common.utils;

import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.CountryBean;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.Comparator;

public class PinyinComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        String value1 = "";
        String value2 = "";
        if (o1 instanceof CountryBean) {
            CountryBean name1 = (CountryBean) o1;
            CountryBean name2 = (CountryBean) o2;
            value1 = name1.getCountryName();
            value2 = name2.getCountryName();
        } else if (o1 instanceof CityBean) {
            CityBean name1 = (CityBean) o1;
            CityBean name2 = (CityBean) o2;
            value1 = name1.getName();
            value2 = name2.getName();
        }

        String str1 = getPingYin(value1);
        String str2 = getPingYin(value2);

        return str1.compareTo(str2);
    }

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    public String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();// 把字符串转化成字符数组
        String output = "";
        try {
            for (int i = 0; i < input.length; i++) {
                // \\u4E00是unicode编码，判断是不是中文
                if (Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    // 将汉语拼音的全拼存到temp数组
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    try {
                        // 取拼音的首字母
                        output += temp[0].substring(0, 1);
                    } catch (Exception e) {
                        // 取拼音的第一个读音
                        output += temp[0];
                        e.printStackTrace();
                    }
                } else if (input[i] > 'A' && input[i] < 'Z') {
                    // 大写字母转化成小写字母
                    output += Character.toString(input[i]);
                    output = output.toLowerCase();
                } else if (input[i] == ' ' && output.length() > 0) {
                    // 检测到空格就返回
                    return output;
                } else {
                    // 大写字母转化成小写字母
                    output += Character.toString(input[i]);
                }

//                output += java.lang.Character.toString(input[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
