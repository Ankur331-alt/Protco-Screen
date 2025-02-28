package com.smart.rinoiot.common;

/**
 * 常量类
 *
 * @className Constant
 * @date: 2020/6/4 9:32 AM
 * @author: xf
 */
public class Constant {
    /**
     * google应用地址
     */
    public static final String APP_GOOGLE_URL = "https://play.google.com/store/apps/details?id=com.smart.rinoiot";

    /**
     * 国内应用地址
     */
    public static final String APP_DOMESTIC_URL = "https://nfc.rinoiot.com/download";

    /**
     * 浏览器标识
     */
    public static final String IS_BROWSER = "isBrowser";
    /**
     * 隐私政策和用户协议域名
     */
    public static String BASE_URL_H5 = "https://iot.rinoiot.com/";

    /**
     *
     */
    public static String API_HOSTNAME = "test.rinoiot.com";

    /**
     * 后台接口域名
     */
    public static String BASE_URL = "https://test.rinoiot.com/api/";
    /**
     * AES 加密 appid
     */
    public static String AES_APPID = "bBcDt1WghhBgWF3M";
    /**
     * AES 加密 secret
     */
    public static String AES_SECRET = "229c1bf1a88a6f19af92bc40505a9392";
    /**
     * 客户端申请的权限范围
     */
    public static String SCOPE = "all";
    /**
     * 授权id
     */
    public static String AUTHORIZATION_ID = "rinoiot-iot-app";
    /**
     * 授权密码
     */
    public static String AUTHORIZATION_PASSWORD = "1234567";

    /**
     * MQTT 服务地址
     */
    public static String MQTT_HOST_URL = "124.222.172.253";

    /**
     * websocket 服务地址
     */
    public static String WEB_SOCKET_HOST_URL = "test.rinoiot.com";

    /**
     * websocket 参数
     */
    public static String WEB_SOCKET_DATA;

    /**
     * MQTT 端口
     */
    public static int MQTT_HOST_PROT = 1883;
    /**
     * MQTT 服务器地址
     */
    public static String MQTT_HOST = "tcp://" + Constant.MQTT_HOST_URL + ":" + Constant.MQTT_HOST_PROT;


    /**
     * client_id  后台每个接口必传字段，放到头部
     */
    public static String CLIENT_ID = "cmlub2lvdC1pb3QtYXBwOm9sQURKQ21XbEZJVlhZMXExbHgwd1VyVWJ6ZTdYd2VS";
    /**
     * 蓝牙传输类型，设置wifi名称和密码，或者BLE配网
     */
    public static String BLUETOOTH_TYPE_FOR_NETWORK_SET = "thing.network.set";
//    /** 蓝牙传输类型，设置wifi名称和密码 */
//    public static String BLUETOOTH_TYPE_FOR_RESPONSE = "thing.network.set.response";
//    /** 蓝牙传输类型，设置wifi名称和密码 */
//    public static String BLUETOOTH_TYPE_FOR_NETWORK_SET = "thing.network.set";
//    /** 蓝牙传输类型，设置wifi名称和密码 */
//    public static String BLUETOOTH_TYPE_FOR_NETWORK_SET = "thing.network.set";


    /**
     * 通讯协议（1：WIFI+BLE，2：蓝牙Mesh（SIG），3：Zigbee，4：PLC，5：传统BLE，6：NB-IOT,7:plc 无线；
     * 8：WIFI；9：RS-485;10:RJ-45）wireless
     */
    public final static String PROTOCOL_TYPE_FOR_WIFI_BLE = "1";
    public final static String PROTOCOL_TYPE_FOR_BLUETOOTH_MESH = "2";
    public final static String PROTOCOL_TYPE_FOR_ZIGBEE = "3";
    public final static String PROTOCOL_TYPE_FOR_PLC = "4";
    public final static String PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH = "5";
    public final static String PROTOCOL_TYPE_FOR_NB_IOT = "6";
    public final static String PROTOCOL_TYPE_FOR_WIRELESS_PLC = "7";
    public final static String PROTOCOL_TYPE_FOR_SINGLE_WIFI = "8";
    public final static String PROTOCOL_TYPE_FOR_RS_485 = "9";
    public final static String PROTOCOL_TYPE_FOR_WIRELESS_RJ_45 = "10";
    public static final String PROTOCOL_TYPE_FOR_MATTER_WIFI = "11";

    public static final String PROTOCOL_TYPE = "protocolType";
    /**
     * 状态栏是否是深色字体
     */
    public static boolean IS_DARK_FONT = true;

    /**
     * 显示加载
     */
    public final static String SHOW_LOADING = "show_loading";
    /**
     * 加载成功
     */
    public final static String LOADING_SUCCESS = "loading_success";
    /**
     * 加载失败
     */
    public final static String LOADING_FAILED = " loading_failed";
    /**
     * 暂无数据
     */
    public final static String EMPTY_DATA = "empty_data";
    /**
     * 有数据状态
     */
    public static final String HAS_DATA = "has_data";

    /**
     * 账号名字key
     */
    public static final String ACCOUNT_NAME = "account_name";
    /**
     * 账号类型key
     */
    public static final String ACCOUNT_TYPE = "account_type";
    /**
     * 账号密码key
     */
    public static final String ACCOUNT_PWD = "account_password";
    /**
     * 城市编码key
     */
    public static final String COUNTRY_REGION = "country_region";
    /**
     * 验证码
     */
    public static final String VALIDATE_CODE = "validate_code";
    /**
     * 国家代码key
     */
    public static final String COUNTRY_CODE = "country_code";
    /**
     * 国家名称key
     */
    public static final String COUNTRY_NAME = "country_name";
    /**
     * 界面标题key
     */
    public static final String ACTIVITY_TITLE = "activity_title";
    /**
     * 界面二级标题key
     */
    public static final String ACTIVITY_SUB_TITLE = "activity_sub_title";
    /**
     * web链接key
     */
    public static final String WEB_URL = "web_url";

    /**
     * 场景类型，一键执行
     */
    public static final int SCENE_TYPE_FOR_MANUAL = 1;
    /**
     * 场景类型，自动化
     */
    public static final int SCENE_TYPE_FOR_AUTO = 2;

    /**
     * 场景子item类型，条件
     */
    public static final int SCENE_CHILDREN_TYPE_FOR_CONDITION = 1;
    /**
     * 场景子item类型，任务
     */
    public static final int SCENE_CHILDREN_TYPE_FOR_TASK = 2;

    /**
     * 生效时间类型，全天
     */
    public static final int SCENE_EFFECTIVE_PERIOD_TYPE_FOR_ALL_DAY = 1;
    /**
     * 生效时间类型，白天
     */
    public static final int SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_DAY = 2;
    /**
     * 生效时间类型，夜晚
     */
    public static final int SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_NIGHT = 3;
    /**
     * 生效时间类型，自定义
     */
    public static final int SCENE_EFFECTIVE_PERIOD_TYPE_FOR_CUSTOM = 4;

    /**
     * 场景配置触发条件，一键执行
     */
    public static final int SCENE_CONDITION_FOR_ONE_KEY = 0;
    /**
     * 场景配置触发条件，气象变化时
     */
    public static final int SCENE_CONDITION_FOR_CHANGE_WEATHER = 1;
    /**
     * 场景配置触发条件，位置变化时
     */
    public static final int SCENE_CONDITION_FOR_CHANGE_LOCATION = 2;
    /**
     * 场景配置触发条件，定时
     */
    public static final int SCENE_CONDITION_FOR_TIMING = 3;
    /**
     * 场景配置触发条件，设备状态变化时
     */
    public static final int SCENE_CONDITION_FOR_DEVICE_STATUS_CHANGE = 4;

    /**
     * 场景执行任务， 操作设备
     */
    public static final int SCENE_TASK_FOR_OPERATE_DEVICE = 1;
    /**
     * 场景执行任务，执行场景
     */
    public static final int SCENE_TASK_FOR_ALREADY_SMART = 2;
    /**
     * 场景执行任务，发送通知
     */
    public static final int SCENE_TASK_FOR_NOTIFY = 3;
    /**
     * 场景执行任务，延时
     */
    public static final int SCENE_TASK_FOR_DELAYED = 4;

    /**
     * 场景执行任务，绑定群组设备
     */
    public static final int SCENE_TASK_FOR_OPERATE_GROUP_DEVICE = 6;

    /**
     * 场景设备列表展示是否显示群组
     */
    public static final String SCENE_SELECT_DEVICE_TYPE = "scene_select_device_type";

    /**
     * 场景选择列表的key
     */
    public static final String SCENE_LIST_SELECT_TYPE = "scene_list_select_type";
    /**
     * 场景选择列表，选择重复的 value
     */
    public static final String SCENE_LIST_SELECT_FOR_WEEK = "scene_list_select_for_week";
    /**
     * 场景选择列表，选择设备的 value
     */
    public static final String SCENE_LIST_SELECT_FOR_DEVICE = "scene_list_select_for_device";
    /**
     * 场景选择列表，选择气象变化时的 value
     */
    public static final String SCENE_LIST_SELECT_FOR_WEATHER = "scene_list_select_for_weather";
    /**
     * 场景选择列表，选择已有智能场景的 value
     */
    public static final String SCENE_LIST_SELECT_FOR_ALREADY_SMART = "scene_list_select_for_already_smart";

    /**
     * 场景配置进度条的key
     */
    public static final String SCENE_SEEK_CONFIG_TYPE = "scene_seek_config_type";
    /**
     * 场景配置进度条，温度的 value
     */
    public static final String SCENE_SEEK_CONFIG_FOR_TEMPERATURE = "temp";
    /**
     * 场景配置进度条，风速的 value
     */
    public static final String SCENE_SEEK_CONFIG_FOR_WIND_SPEED = "windSpeed";

    /**
     * 场景配置单选的key
     */
    public static final String SCENE_SINGLE_CONFIG_TYPE = "scene_single_config_type";
    /**
     * 场景配置单选，湿度的 value
     */
    public static final String SCENE_SINGLE_CONFIG_FOR_HUMIDITY = "humidity";
    /**
     * 场景配置单选，天气的 value
     */
    public static final String SCENE_SINGLE_CONFIG_FOR_WEATHER = "condition";
    /**
     * 场景配置单选，pm2.5的 value
     */
    public static final String SCENE_SINGLE_CONFIG_FOR_PM25 = "pm25";
    /**
     * 场景配置单选，空气质量的 value
     */
    public static final String SCENE_SINGLE_CONFIG_FOR_AIR_QUALITY = "aqi";
    /**
     * 场景配置单选，日落日出的 value
     */
    public static final String SCENE_SINGLE_CONFIG_FOR_SUNSET_SUNRISE = "sunsetrise";
    /**
     * 场景配置单选，设备状态变化的 value
     */
    public static final String SCENE_SINGLE_CONFIG_FOR_DEVICE = "device";

    /**
     * 场景配置多选的key
     */
    public static final String SCENE_MULTIPLE_CONFIG_TYPE = "scene_multiple_config_type";
    /**
     * 场景配置多选，选择一键执行的 value
     */
    public static final String SCENE_MULTIPLE_CONFIG_FOR_MANUAL_SCENE = "scene_multiple_config_for_manual_scene";
    /**
     * 场景配置多选，选择自动化的 value
     */
    public static final String SCENE_MULTIPLE_CONFIG_FOR_AUTO_SCENE = "scene_multiple_config_for_auto_scene";
    /**
     * 场景配置多选，选择提醒方式的 value
     */
    public static final String SCENE_MULTIPLE_CONFIG_FOR_NOTIFY = "scene_multiple_config_for_notify";

    /**
     * Commission result intent extra
     */
    public static final String MATTER_COMMISSION_RESULT_EXTRA = "com.rino.smart.COMMISSION_RESULT";

    /**
     * 验证码类型（1=登录，2=注册,3=重置密码,4=匿名账号升级）
     */
    public static final int SEND_CODE_TYPE_FOR_LOGIN = 1;
    public static final int SEND_CODE_TYPE_FOR_REGISTER = 2;
    public static final int SEND_CODE_TYPE_FOR_RESET_PASSWORD = 3;
    public static final int SEND_CODE_TYPE_FOR_ANONYMOUS = 4;

    /**
     * 选择国家的后续操作（1=登录，2=注册,3=匿名登录）
     */
    public static final int SELECT_COUNTRY_NEXT_TO_LOGIN = 1;
    public static final int SELECT_COUNTRY_NEXT_TO_REGISTER = 2;
    public static final int SELECT_COUNTRY_NEXT_TO_ANONYMOUS = 3;

    /**
     * 登录类型
     * - password  ：密码登录授权模式
     * - refresh_token： refresh_token刷新
     * - authorization_code：授权码模式
     * - anonymous： 匿名游客登录授权方式
     * - sms_code： 短信登录授权方式
     * -umeng_onekey 友盟一键登录
     */
    public static final String GRANT_TYPE_FOR_PASSWORD = "password";

    public static final String GRANT_TYPE_FOR_REFRESH_TOKEN = "refresh_token";

    public static final String GRANT_TYPE_FOR_AUTHORIZATION_CODE = "authorization_code";

    public static final String GRANT_TYPE_FOR_ANONYMOUS = "anonymous";

    public static final String GRANT_TYPE_FOR_SMS_CODE = "sms_code";

    public static final String GRANT_TYPE_FOR_ONE_KEY = "umeng_onekey";

    /**
     * 登录方式（1=验证码登录，2=邮箱登录）
     */
    public static final int LOGIN_TYPE_FOR_PHONE = 1;

    public static final int LOGIN_TYPE_FOR_EMAIL = 2;

    public static final String Home_Data = "home_data";

    public static final String Member_Data = "member_data";

    /**
     * 分页数
     */
    public static final int PAGE_SIZE = 20;
    /**
     * 忽略版本缓存key
     */
    public static final String IGNORE_UPDATE = "ignore_update";

    /**
     * 上次配网wifi的名称
     */
    public static final String BEFORE_WIFI_NAME = "before_wifi_name";
    /**
     * 进入面板保存设备数据 key
     */
    public static final String PANEL_DEVICE_ID = "panel_device_id";

    /**
     * 修改设备名称返回详情页面name key
     */
    public static final String RENAME_CONTENT = "rename_content";

    /**
     * WIFI+蓝牙设备，蓝牙配网成功，进入面板，在进行wifi配网 标识，区分正常配网，公用一个页面 key
     */
    public static final String PANEL_CONFIG_TYPE = "panel_config_type";

    /**
     * WIFI+蓝牙设备，蓝牙配网成功，进入面板，在进行wifi配网 数据 key
     */
    public static final String PANEL_CONFIG_DATA = "panel_config_data";

    /**
     * UUID数据 key
     */
    public static final String ASSETID = "assetId";

    /**
     * devId数据 key
     */
    public static final String DEV_ID = "devId";

    /**
     * 扫一扫绑定设备数据 key
     */
    public static final int SCAN_BIND_DEVICE_CODE = 100;

    /**
     * 进入面板保存设备数据 key
     */
    public static final String PANEL_DEVICE_INFO = "panel_device_info";

    /**
     * 接口token过期，防止接口多并发 标识 key
     */
    public static final String USER_TOKEN_EXPIRED = "user_token_expired";

    /**
     * 扫码传参 key
     */
    public static final String QR_CODE = "qrcode";

    /**
     * Matter on boarding payload. This combines the manual pairing code and the QR code
     */
    public static final String ON_BOARDING_PAYLOAD = "onBoardingPayload";

    /**
     * wifi ssid key
     */
    public static final String WIFI_NAME = "wifiName";

    /**
     * wifi 密码 key
     */
    public static final String WIFI_PWD = "wifiPwd";

    /**
     * 判断是否是自动场景还是一件执行 0: 所有 1：自动化，
     */
    public static final String SCENE_TYPE = "scene_type";

    /**
     * 成员角色（1=拥有者，2=管理员，3=普通成员）
     */
    public static final String MEMBER_ROLE = "member_role";

    /**
     * 动态修改配置
     */
    public static final String PANEL_DEBUG = "panel_debug";//面板是否debug
    public static final String SERVICE_ENVIRONMENT = "service_environment";//正式环境还是测试环境
    /**
     * mqtt地址
     */
    public static final String MQTT_URL_IP = "mqtt_url_ip";

    /**
     * 是否打开摇一摇
     */
    public static String SHAKE_FLAG = "shake_flag";
    /**
     * 注销账号
     */
    public static final String LOGOUT_ACCOUNT = "logout_account";
    /**
     * 当前城市
     */
    public static final String CURRENT_CITY = "current_city";

    /**
     * 修改房间设备数据，传家庭资产 key
     */
    public static final String FAMILY_DATA = "family_data";

    /**
     * 获取物模型接口失败
     */
    public static final String MODE_FAIL = "get_interface_mode_fail";

    /**
     * 蓝牙配网连接失败 key
     */
    public static final String BLE_CONNECT_FAIL = "rino_device_activator_error";

    /**
     * 群组id key
     */
    public static final String GROUP_ID = "group_id";

    /**
     * devId数据 key
     */
    public static final String PRODUCT_ID = "product_id";

    /**
     * 配网指引数据 key
     */
    public static final String GUIDE_DATA = "guide_data";

    /**
     * 数据中心写入到文件中的文件名
     */
    public static final String DATA_CENTER_FILE_NAME = "dataCenter";

    /**
     * alexa 授权回调
     */
    public static final String ALEXA_VERIFY_CALL_BACK = "alexa_verify_call_back";

    public static final String ALEXA_BIND_STATUS = "alexa_bind_status";
    /**
     * 保存中国服务器地址的areaId值,
     */
    public static final String PUSH_STYLE = "push_style";

    /**
     * NFC功能nfcId key
     */
    public static final String NFC_UUID = "nfc_uuid";

    /**
     * NFC功能执行状态 key
     */
    public static final String NFC_EXECUTE_STATUS = "nfc_execute_status";

    /**
     * NFC功能查询数据 key
     */
    public static final String NFC_DATA = "nfc_data";

    /**
     * NFC功能执行失败且数据不存在 key
     */
    public static final String NFC_EXECUTE_FAIL_STATUS = "nfc_execute_fail_status";

    /**
     * NFC功能查询列表 key
     */
    public static final String NFC_UUIDS = "nfc_uuids";

    /**
     * NFC名称 key
     */
    public static final String NFC_NAME = "nfc_name";

    /**
     * NFC 绑定快捷开关异常 key
     */
    public static final String NFC_TYPE_SWITCH = "nfc_type_switch";

    /**
     * NFC 绑定快捷开关异常传值 key
     */
    public static final String NFC_EXECUTE_FAIL = "nfc_execute_fail";

    /**
     * NFC 绑定快捷开关异常传值 key
     */
    public static final String NFC_DEVICE_DATA = "nfc_device_data";

    /**
     * NFC 绑定场景编辑场景详情时，区分nfc绑定场景和场景 传值 key
     * 1:nfc绑定场景；0：默认主场景
     */
    public static final String NFC_SCENE_EDIT_STATUS = "nfc_scene_edit_status";

    /**
     * NFC 绑定场景  0:是创建;1:修改  传值 key
     */
    public static final String NFC_SCENE_TYPE = "nfc_scene_type";

    /**
     * NFC 编辑场景时删除场景逻辑处理 1:nfc删除;0:主场景中删除  传值 key
     */
    public static final String NFC_DELETE_SCENE_TYPE = "nfc_delete_scene_type";

    /**
     * NFC 编辑场景时删除场景逻辑处理  传值 key
     */
    public static final String NFC_DELETE_SCENE_DATA = "nfc_delete_scene_data";

    /**
     * 用户同意隐私协议  传值 key
     */
    public static final String ConsentPrivacy = "ConsentPrivacy";

    /**
     * 一键执行默认图标
     */
    public static String MANUAL_SCENE_STYLE_DEFAULT_ICON_URL = "https://storage-app.rinoiot.com/scene/icon/default-icon.png";

    /**
     * 区分常用功能面板还是正常面板  传值 key true:常用功能面板
     */
    public static final String COMMON_FUNCTION_PANEL = "common_function_panel";

    /**
     * 进入面板防止卡死情况无限点击  传值 key
     */
    public static boolean ADD_PANEL_ENTRANCE = false;

    /**
     * 进入nfc防止卡死情况无限点击  传值 key
     */
    public static boolean ADD_NFC_ENTRANCE = false;

    /**
     * app是否启动  传值 key
     */
    public static final String APP_START_HOME_PAGE = "app_start_home_page";

    /**
     * 当前面板设备id  NFC 1期  传值 key
     */
    public static final String CURRENT_PANEL_DEV_UUID = "current_panel_dev_uuid";

    /**
     * NFC 创建一键执行场景和面板创建一键场景共用一个页面， key
     */
    public static final String NFC_CREATE_SCENE_TYPE = "nfc_create_scene_type";

    /**
     * 不同渠道打包，走不同的逻辑业务
     * 如：华为不能展示第三方语音(Alexa、Google Assistant),默认google play
     */
    public static String CURRENT_CHANNEL = "googlePlay";

    /**
     * 华为渠道
     */
    public static final String HUA_WEI_CHANNEL = "huaweiStore";
    /**
     * google渠道
     */
    public static final String GOOGLE_CHANNEL = "googlePlay";

    /**
     * google 授权绑定未登录时，重新登录成功，直接关掉当前页面
     */
    public static final String GOOGLE_AUTH_USER_NOT_LOGIN = "google_auth_user_not_login";

    public static final String GOOGLE_BIND_STATUS = "google_bind_status";

    /**
     * 第三方语音数据 key
     */
    public static final String VOICE_LIST_DATA = "voice_list_data";

    /**
     * APP进面板容器数据 可以
     */
    public static final String PANEL_DEVICE_BUNDLE_DATA = "deviceBean";
    /**
     * APP进面板容器数据 可以
     */
    public static final String PANEL_DIRNAME_PATH = "rnDirName";

    /**
     * 房间管理 key
     */
    public static final String FAMILY_MANAGER = "family_manager";

    /**
     * 息屏时间key
     */
    public static final String SCREEN_OFF = "screen_off";

    /**
     * 缓存clientId 的key
     */
    public static final String MQTT_CLIENT_ID = "clientId";
}