package com.smart.rinoiot.voice.automation;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dsh.openai.home.model.OperationFailedException;
import com.dsh.openai.home.model.automation.Condition;
import com.dsh.openai.home.model.automation.ManagementIntent;
import com.dsh.openai.home.model.automation.MatchType;
import com.dsh.openai.home.model.automation.Task;
import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.scene.api.SceneApiService;
import com.smart.rinoiot.scene.manager.SceneNetworkManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author edwin
 */
public class AutomationManagementUtils {
    private static final String TAG = "AutomationManagementUtils";

    private static final String ID_KEY = "id";
    private static final String ENABLED_KEY = "enabled";

    private static CompletableFuture<Boolean> createScene(SceneBean sceneBean) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        SceneNetworkManager.getInstance().createScene(sceneBean, new CallbackListener<SceneBean>() {
            @Override
            public void onSuccess(SceneBean data) {
                future.complete(true);
            }

            @Override
            public void onError(String code, String error) {
                Log.d(TAG, "onError: Failed to create the automation. Cause=" + error);
                future.complete(false);
            }
        });

        return future;
    }

    /**
     * Deletes an automation
     *
     * @param automationId the automation identifier
     * @return a boolean future
     */
    private static CompletableFuture<Boolean> deleteAutomation(String automationId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        RetrofitUtils.getService(SceneApiService.class).deleteSceneById(
                automationId
        ).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                future.complete(true);
            }

            @Override
            public void onError(String error, String msg) {
                Log.e(TAG, "onError: failed to delete the automation. Cause=" + msg);
                future.complete(false);
            }
        });
        return future;
    }

    /**
     * Enables or disables automations.
     * @param automationId the automation identifier
     * @param status the status
     */
    private static CompletableFuture<Boolean> updateAutomationStatus(
        String automationId, boolean status
    ) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Map<String, Object> map = new HashMap<>(2);
        map.put(ID_KEY, automationId);
        map.put(ENABLED_KEY, status ? 1 : 0);
        RetrofitUtils.getService(SceneApiService.class).changeSceneStatus(
            map
        ).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                future.complete(true);
            }

            @Override
            public void onError(String error, String msg) {
                Log.e(TAG, "onError: Failed to update automation status. Cause=" + msg);
                future.complete(false);
            }
        });

        return future;
    }

    /**
     * Triggers the automations
     *
     * @param automationId the automation identifier
     */
    private static CompletableFuture<Boolean> triggerAutomation(String automationId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        RetrofitUtils.getService(SceneApiService.class).oneKeyExecute(
                automationId
        ).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                future.complete(true);
            }

            @Override
            public void onError(String error, String msg) {
                Log.e(TAG, "onError: Failed to trigger the automation. Cause=" + msg);
                future.complete(false);
            }
        });
        return future;
    }

    /**
     * @param name the suggested name for the automation
     * @param loops the loops
     * @param matchType the match type
     * @param tasks the tasks
     * @param conditions the conditions
     * @return true on success, false otherwise
     */
    public static boolean handleAutomationCreation(
        @NonNull String name, @NonNull String loops, @NonNull MatchType matchType,
        @NonNull List<Task> tasks, @NonNull List<Condition> conditions
    ) {
        String timezoneId = AppUtil.getSystemTimeZone();
        AssetBean home = CacheDataManager.getInstance().getCurrentFamily();
        SceneBean sceneBean = AutomationDataUtils.buildSceneBean(
                name, loops, timezoneId, matchType, tasks, conditions
        );
        sceneBean.setAssetId(home.getId());
        Log.d(TAG, "onCreateAutomation: scene bean=" + new Gson().toJson(sceneBean));
        CompletableFuture<Boolean> future = AutomationManagementUtils.createScene(sceneBean);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.d(TAG, "onCreateAutomation: failed. Cause=" + e.getLocalizedMessage());
            throw new OperationFailedException("Execution failed", e.getCause());
        }
    }

    /**
     * Manages the automation
     *
     * @param intent the management intent
     * @param automationIds the list of automation identifier
     * @return true on success, false or an exception on failure
     */
    public static boolean manageAutomations(String intent, List<String> automationIds) {
        Log.d(TAG, "manageAutomations: intent=" + intent);
        Log.d(TAG, "manageAutomations: automation ids=" + new Gson().toJson(automationIds));
        if(automationIds.isEmpty()){
            throw new UnsupportedOperationException(
                "No valid automation identifiers provided", null
            );
        }

        AtomicInteger counter = new AtomicInteger(0);
        automationIds.forEach(automationId-> {
            try {
                if(ManagementIntent.Trigger.getIntent().contentEquals(intent)){
                    CompletableFuture<Boolean> future = triggerAutomation(automationId);
                    if(future.get()){
                        counter.getAndIncrement();
                    }
                }else if(ManagementIntent.Delete.getIntent().contentEquals(intent)) {
                    CompletableFuture<Boolean> future = deleteAutomation(automationId);
                    if(future.get()) {
                        counter.getAndIncrement();
                    }
                }else if(ManagementIntent.Enable.getIntent().contentEquals(intent)){
                    CompletableFuture<Boolean> future = updateAutomationStatus(
                        automationId, true
                    );
                    if(future.get()){
                        counter.getAndIncrement();
                    }
                }else if(ManagementIntent.Disable.getIntent().contentEquals(intent)){
                    CompletableFuture<Boolean> future = updateAutomationStatus(
                        automationId, false
                    );
                    if(future.get()){
                        counter.getAndIncrement();
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                String errorMsg = "manageAutomations: Failed to " +
                        intent + " automation. Cause=" + e.getLocalizedMessage();
                Log.e(TAG, errorMsg);
            }
        });
        return (counter.get() > 0);
    }
}
