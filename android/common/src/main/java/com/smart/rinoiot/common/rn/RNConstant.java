package com.smart.rinoiot.common.rn;

/**
 * @author tw
 * @time 2022/10/17 19:59
 * @description RN常量类
 */
public class RNConstant {
    /**
     * 设备dp数据发生改变时  设备上报开关为关
     */
    public static final String DEVICE_DATA_POINT_UPDATE = "RinoDeviceDataPointUpdate";

    /**
     * 设备属性数据发生改变时  修改了设备名称、设备主动离线等
     */
    public static final String DEVICE_INFO_UPDATE = "RinoDeviceInfoUpdate";

    /**
     * 设备属性数据发生改变时  修改了设备名称、设备主动离线等
     */
    public static final String SHORT_CUT_DATA_NOTIFY = "RinoShortcutDataNotify";

    /**
     * 屏幕方向发生该表时 通知
     */
    public static final String SCREEN_ORIENTATION_CHANGE = "RinoScreenOrientationChange";

    /**
     * APP页面返回面板页面时
     */
    public static final String OPEN_APP_BUSINESS_PAGE_NOTIFY = "RinoNativePageBack";
    /**
     * 创建一键执行 page 路径
     */
    public static final String CREATE_ONE_CLICK_EXECUTION = "create_one_click_execution";

    /**
     * 从面板页面调用openAppBusinessPage这个业务方法时，回到面板必须通知面板
     */
    public static final String PANEL_OPEN_APP_BUSINESS_PAGE_BACK = "panel_open_app_business_page_back";

    /**
     * 从面板页面调用openAppBusinessPage page name
     */
    public static final String PANEL_OPEN_APP_BUSINESS_PAGE_NAME = "panel_open_app_business_page_name";

    /**
     * 常用功能下载成功 如果常用面板展示，下次关闭时，更新资源文件
     */
    public static boolean COMMON_PANEL_UPDATE = false;

    /**
     * mqtt 2.0,设备数据发生变化时，通知面板
     */
    public static final String DEVICE_DATA_POINT_UPDATE_V2 = "RinoDeviceDataPointUpdate_V2";

    /**
     * mqtt 2.0,网关设备绑定网关子设备时，通知面板
     */
    public static final String DEVICE_GATEWAY_BIND_V2 = "RinoDeviceGatewayBind";

    /**
     * 网关子设备解绑 通知面板更新数据
     */
    public static final String GATEWAY_SUB_DEVICE_UNBIND = "jumpToAppPageBack";
}
