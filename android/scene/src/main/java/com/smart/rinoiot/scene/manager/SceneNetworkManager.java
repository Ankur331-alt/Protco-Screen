package com.smart.rinoiot.scene.manager;

import android.util.Log;

import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SceneDeviceDpBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.scene.api.SceneApiService;
import com.smart.rinoiot.scene.bean.IconItemBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * @author author
 */
public class SceneNetworkManager {

    private static final String TAG = "SceneNetworkManager";

    private static SceneNetworkManager instance;

    private SceneNetworkManager() {
    }

    public static SceneNetworkManager getInstance() {
        if (instance == null) {
            instance = new SceneNetworkManager();
        }
        return instance;
    }

    /**
     * 查询资产下的所有场景
     * @param assetId the asset identifier
     * @param callback the callback
     */
    public void getAllSceneListAsync(String assetId, CallbackListener<List<SceneBean>> callback) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("assetId", assetId);

        AppExecutors.getInstance().networkIO().execute(()->{
            RetrofitUtils.getService(SceneApiService.class).getAllSceneList(map).enqueue(new BaseRequestListener<List<SceneBean>>() {
                @Override
                public void onResult(List<SceneBean> result) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        CacheDataManager.getInstance().saveAllSceneList(assetId, result);
                        if (callback != null) {
                            callback.onSuccess(result);
                        }
                    });
                }

                @Override
                public void onError(String error, String msg) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        if (callback != null) {
                            callback.onError(error, msg);
                        }
                    });
                }
            });
        });
    }

    /**
     * Fetch the list of scenes.
     * @param homeId home identifier.
     * @return the list of scenes.
     */
    public Observable<List<SceneBean>> getScenes(String homeId) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("assetId", homeId);

        return Observable.create(emitter -> {
            BaseRequestListener<List<SceneBean>> listener = new BaseRequestListener<List<SceneBean>>() {
                @Override
                public void onResult(List<SceneBean> scenes) {
                    if(null != scenes){
                        CacheDataManager.getInstance().saveAllSceneList(homeId, scenes);
                        emitter.onNext(scenes);
                    }else {
                        emitter.onNext(new ArrayList<>());
                    }
                    emitter.onComplete();
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: error= "+ error + " | msg=" + msg);
                    emitter.onError(new Exception(msg));
                }
            };

            RetrofitUtils.getService(SceneApiService.class).getAllSceneList(map).enqueue(listener);
        });
    }

    /**
     * 查询资产下的所有场景
     */
    public void getAllSceneList(String assetId, CallbackListener<List<SceneBean>> callback) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("assetId", assetId);
        RetrofitUtils.getService(SceneApiService.class).getAllSceneList(map).enqueue(new BaseRequestListener<List<SceneBean>>() {
            @Override
            public void onResult(List<SceneBean> result) {
                CacheDataManager.getInstance().saveAllSceneList(assetId, result);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 查询场景图标列表
     */
    public void getSceneIconList(CallbackListener<List<IconItemBean>> callback) {
        RetrofitUtils.getService(SceneApiService.class).getSceneIconList().enqueue(new BaseRequestListener<List<IconItemBean>>() {
            @Override
            public void onResult(List<IconItemBean> result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 新增场景
     */
    public void createScene(SceneBean sceneBean, CallbackListener<SceneBean> callback) {
        RetrofitUtils.getService(SceneApiService.class).createScene(sceneBean).enqueue(new BaseRequestListener<SceneBean>() {
            @Override
            public void onResult(SceneBean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 编辑场景
     */
    public void updateScene(SceneBean sceneBean, CallbackListener<SceneBean> callback) {
        RetrofitUtils.getService(SceneApiService.class).updateScene(sceneBean).enqueue(new BaseRequestListener<SceneBean>() {
            @Override
            public void onResult(SceneBean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 删除场景
     */
    public void deleteSceneById(String sceneId, CallbackListener<Object> callback) {
        RetrofitUtils.getService(SceneApiService.class).deleteSceneById(sceneId).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 启用-禁用场景, 1表示启用
     */
    public void changeSceneStatus(String sceneId, int enabled, CallbackListener<Object> callback) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("id", sceneId);
        map.put("enabled", enabled);
        RetrofitUtils.getService(SceneApiService.class).changeSceneStatus(map).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 启动一键执行
     */
    public void oneKeyExecute(String sceneId, CallbackListener<Object> callback) {
        RetrofitUtils.getService(SceneApiService.class).oneKeyExecute(sceneId).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 获取场景支持的DP点列表
     */
    public void getSceneSupportDpList(String deviceId, CallbackListener<SceneDeviceDpBean> callback) {
        RetrofitUtils.getService(SceneApiService.class).getSceneSupportDpList(deviceId).enqueue(new BaseRequestListener<SceneDeviceDpBean>() {
            @Override
            public void onResult(SceneDeviceDpBean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }
}
