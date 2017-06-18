package com.star.tinker;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.dx168.patchsdk.IPatchManager;
import com.dx168.patchsdk.Listener;
import com.dx168.patchsdk.PatchManager;
import com.leon.channel.helper.ChannelReaderUtil;
import com.star.tinker.Log.MyLogImp;
import com.star.tinker.util.SampleApplicationContext;
import com.star.tinker.util.TinkerManager;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

public class BaseApplicationLike extends DefaultApplicationLike {
    private static final String TAG = "Tinker.BaseApplicationLike";

    public BaseApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                               long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        SampleApplicationContext.application = getApplication();
        SampleApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        TinkerManager.setUpgradeRetryEnable(true);

        TinkerInstaller.setLogIml(new MyLogImp());

        TinkerManager.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String appId = "20170618225955472-9663";
        String appSecret = "9ebbf292b46345baaefe4dc68814f000";
        String baseUrl = "http://192.168.1.105:8080/hotfix-apis";
        PatchManager.getInstance().init(getApplication(), baseUrl, appId, appSecret, new IPatchManager() {
            @Override
            public void cleanPatch(Context context) {
                TinkerInstaller.cleanPatch(context);
            }

            @Override
            public void patch(Context context, String patchPath) {
                TinkerInstaller.onReceiveUpgradePatch(context, patchPath);
            }
        });
        PatchManager.getInstance().register(new Listener() {
            @Override
            public void onQuerySuccess(String response) {
                Log.d(TAG, "onQuerySuccess response=" + response);
            }

            @Override
            public void onQueryFailure(Throwable e) {
                Log.d(TAG, "onQueryFailure e=" + e);
            }

            @Override
            public void onDownloadSuccess(String path) {
                Log.d(TAG, "onDownloadSuccess path=" + path);
            }

            @Override
            public void onDownloadFailure(Throwable e) {
                Log.d(TAG, "onDownloadFailure e=" + e);
            }

            @Override
            public void onPatchSuccess() {
                Log.d(TAG, "onPatchSuccess");
            }

            @Override
            public void onPatchFailure() {
                Log.d(TAG, "onPatchFailure");
            }

            @Override
            public void onLoadSuccess() {
                Log.d(TAG, "onLoadSuccess");
            }

            @Override
            public void onLoadFailure() {
                Log.d(TAG, "onLoadFailure");
            }
        });
        PatchManager.getInstance().setTag(getChannel());
        PatchManager.getInstance().setChannel(getChannel());
        PatchManager.getInstance().queryAndPatch();
    }

    public String getChannel() {
        String channel;
        channel = ChannelReaderUtil.getChannel(getApplication());
        if (TextUtils.isEmpty(channel)) {
            return "R360";
        }
        return channel;
    }
}
