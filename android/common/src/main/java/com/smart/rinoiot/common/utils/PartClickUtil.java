package com.smart.rinoiot.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.activity.WebViewActivity;
import com.smart.rinoiot.common.listener.PartTextClickListener;

/**
 * 部分文字点击处理
 * * @ProjectName: OemApp
 *
 * @Package: com.znkit.smart.mifei.utils
 * @ClassName: PartClickUtil
 * @Author: xf
 * @CreateDate: 2020/5/18 11:53 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/18 11:53 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PartClickUtil {
    public static SpannableStringBuilder getClick(String startText, String endText, String clickText, int clickTextColor, PartTextClickListener onClick) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append(startText);
        style.append(clickText);
        style.append(endText);

        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (onClick != null) {
                    onClick.onClick(widget);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(onClick == null ? null : clickableSpan, startText.length(), startText.length() + clickText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(clickTextColor);
        style.setSpan(foregroundColorSpan, startText.length(), startText.length() + clickText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new StyleSpan(Typeface.BOLD), startText.length(), startText.length() + clickText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return style;
    }

    public static SpannableStringBuilder getClick2(String startText, String endText, String clickText, String clickText2, int clickTextColor, int clickTextColor2, PartTextClickListener onClick) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append(startText);
        style.append(clickText);
        style.append(clickText2);
        style.append(endText);

        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (onClick != null) {
                    onClick.onClick(widget);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };


        int click2Length = startText.length() + clickText.length() + clickText2.length();
        style.setSpan(clickableSpan, startText.length() + clickText.length(), click2Length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(clickTextColor);
        ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(clickTextColor2);
        style.setSpan(foregroundColorSpan, startText.length(), startText.length() + clickText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        style.setSpan(foregroundColorSpan2, startText.length() + clickText.length(), click2Length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        style.setSpan(new StyleSpan(Typeface.BOLD), startText.length() + clickText.length(), click2Length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return style;
    }

//    /**
//     * 用户协议和隐私政策
//     */
//    public static SpannableStringBuilder getPrivacyText(Context context, String startText, String endText, String protocol, String policy, int clickTextColor) {
//        SpannableStringBuilder style = new SpannableStringBuilder();
//        //设置文字
//        style.append(startText);
//        style.append(protocol);
//        style.append(endText);
//        style.append(policy);
//
//
//        //设置部分文字点击事件
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//
//                String  protocol_url= !TextUtils.isEmpty( Constant.PROTOCOL_URL) ?  Constant.PROTOCOL_URL  :  context.getResources().getString(R.string.protocol_html)  ;
//                L.e("=========>>>",protocol_url);
//                RouterManager.getInstance().build(Router.WEB)
//                        .putExtra(Constant.TITLE, protocol)
//                        .putExtra(Constant.URL, protocol_url).start();
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                ds.setUnderlineText(false);
//            }
//        };
//
//        style.setSpan(clickableSpan, startText.length(), startText.length() + protocol.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        //设置部分文字颜色
//        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(clickTextColor);
//        style.setSpan(foregroundColorSpan, startText.length(), startText.length() + protocol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        style.setSpan(new UnderlineSpan(), startText.length(), startText.length() + protocol.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        style.setSpan(new StyleSpan(Typeface.BOLD), startText.length(), startText.length() + protocol.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        //设置部分文字点击事件
//        ClickableSpan clickableSpan1 = new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                String  privacy_url= !TextUtils.isEmpty( Constant.PROTOCOL_URL) ?  Constant.PRIVACY_URL  :  context.getResources().getString(R.string.privacy_html)  ;
//                L.e("=========>>>",privacy_url);
//                RouterManager.getInstance().build(Router.WEB)
//                        .putExtra(Constant.TITLE, policy)
//                        .putExtra(Constant.URL, privacy_url).start();
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                ds.setUnderlineText(false);
//            }
//        };
//
//        int startLength = startText.length() + protocol.length() + endText.length();
//        style.setSpan(clickableSpan1, startLength, style.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        //设置部分文字颜色
//        ForegroundColorSpan foregroundColorSpan1 = new ForegroundColorSpan(clickTextColor);
//        style.setSpan(foregroundColorSpan1, startLength, style.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        style.setSpan(new UnderlineSpan(), startLength, style.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        style.setSpan(new UnderlineSpan(), startLength, style.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        style.setSpan(new StyleSpan(Typeface.BOLD), startLength, style.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        return style;
//    }

    /**
     * 用户协议和隐私政策
     */
    public static SpannableStringBuilder getPrivacyText(Context context, String normalText, String[] highlightText, String[] targetHttp, int clickTextColor) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append(normalText);
        for (String text : highlightText) {
            style.append(" ");
            style.append(text);
        }

        int start;
        int end ;
        for (int i = 0; i < targetHttp.length; i++) {
            String title = highlightText[i];
            String httpUrl = targetHttp[i];
            //设置部分文字点击事件
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    context.startActivity(new Intent(context, WebViewActivity.class)
                            .putExtra(Constant.ACTIVITY_TITLE, title)
                            .putExtra(Constant.WEB_URL, httpUrl));
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            };

            start = style.toString().indexOf(title);
            end = start + title.length();
            // 设置点击部分
            style.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            //设置部分文字颜色
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(clickTextColor);
            style.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            style.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            style.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return style;
    }

    /**
     * 一键登录未勾选用户协议和隐私政策时
     */
    public static SpannableStringBuilder getPrivacyTextVerify(Context context, String normalText, String[] highlightText, String[] targetHttp, int clickTextColor) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append(normalText);
        for (String text : highlightText) {
            style.append(" ");
            style.append(text);
        }

        int start ;
        int end ;
        for (int i = 0; i < targetHttp.length; i++) {
            String title = highlightText[i];
            String httpUrl = targetHttp[i];
            //设置部分文字点击事件
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    context.startActivity(new Intent(context, WebViewActivity.class)
                            .putExtra(Constant.ACTIVITY_TITLE, title)
                            .putExtra(Constant.WEB_URL, httpUrl));
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            };

            start = style.toString().indexOf(title);
            end = start + title.length();
            // 设置点击部分
            style.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            //设置部分文字颜色
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(clickTextColor);
            style.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            style.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return style;
    }

    //设置字体大小
    public static Spannable stringSizeAndColor(String str, int textSize, int start, int end, int type) {
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, type);
        return spannable;
    }

    /**
     * 修改文本颜色
     */
    public static Spannable stringColor(Context mContext, String str, int color, int start, int end, int typeColor) {
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(color)), start, end, typeColor);
        return spannable;
    }

    /**
     * 修改文本颜色+字体大小
     */
    public static Spannable stringSizeAndColor(Context mContext, String str, int color, int textSize, int start, int end, int type) {
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, type);
        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(color)), start, end, type);
        return spannable;
    }

    /**
     * 修改文本颜色+字体大小+粗细
     */
    public static Spannable stringSizeAndColorBlod(Context mContext, String str, int color, int textSize, int start, int end, int type) {
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, type);
        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(color)), start, end, type);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }


    /**
     * 用户协议和隐私政策 中控屏
     */
    public static SpannableStringBuilder getPrivacyCenText(Context context, String normalText, String[] highlightText, String[] targetHttp, int clickTextColor) {
        String strNormal = normalText;
        SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        for (String text:highlightText) {
            normalText=normalText.replaceFirst("%1s", text);
        }
        style.append(normalText);
        int start;
        int end ;
        for (int i = 0; i < targetHttp.length; i++) {
            String title = highlightText[i];
            String httpUrl = targetHttp[i];
            //设置部分文字点击事件
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    context.startActivity(new Intent(context, WebViewActivity.class)
                            .putExtra(Constant.ACTIVITY_TITLE, title)
                            .putExtra(Constant.WEB_URL, httpUrl));
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            };

            start = strNormal.indexOf("%1s");
            strNormal=strNormal.replaceFirst("%1s", title);
            end = start + title.length();
            // 设置点击部分
            style.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            //设置部分文字颜色
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(clickTextColor);
            style.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            style.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            style.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return style;
    }
}
