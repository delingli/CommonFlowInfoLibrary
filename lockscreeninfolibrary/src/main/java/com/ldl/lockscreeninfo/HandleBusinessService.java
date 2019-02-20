package com.ldl.lockscreeninfo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.aigestudio.avatar.utils.SecureUtil;
import com.codex.agentcore.AgentMessage;
import com.codex.agentcore.ReceiverRole;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sant.api.APIError;
import com.sant.api.Api;
import com.sant.api.Callback;
import com.sant.api.chafer.CFITNews;
import com.sant.api.chafer.CFItem;
import com.sant.api.common.ADNotifyAlive;
import com.sant.api.common.ADNotifyAliveLinkType;
import com.sant.api.common.TokenType;
import com.sant.api.moives.MVITVideo;
import com.stkj.onekey.presenter.ui.floatscreenon.ActicityScreenon;
import com.stkj.onekey.processor.impl.sp.Spi;
import com.stkj.onekey.ui.OKUI;
import com.stkj.onekey.ui.impl.floatactivity.ScreenOnData;
import com.stkj.onekey.ui.impl.floatactivity.ScreenOnDataInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.stkj.onekey.ui.impl.transferMode.TransferModeActivity.KEY_TFROM;
import static com.stkj.onekey.ui.impl.transferMode.TransferModeActivity.SD;
import static com.stkj.onekey.ui.impl.transferMode.TransferModeActivity.WIRELESS;

/**
 * create by ldl2018/8/17 0017
 * 处理耗时业务
 */

public class HandleBusinessService extends IntentService {
    //处理透传业务
    private static final String PASSTHROUGH_MESSAGE = "com.stkj.onekey.presenter.yunospush.HandleBusinessService.action.passthroughmessage";
    //处理插件Push，wifi变换触发的通知
    private static final String PLUGIN_NOTIFICATION = "com.stkj.onekey.presenter.yunospush.HandleBusinessService.action.pluginnotification";
    //处理插件亮屏触发的通知
    private static final String PLUGIN_SCREEN_ON = "com.stkj.onekey.presenter.yunospush.HandleBusinessService.action.plugin_screen_on_notification";
    public static final String PUSH_INFO = "pushInfo";
    protected static final String KEY_TMODE = "EQ6E5R4A2S1E3Q";
    public static final String TAG = "HandleBusinessService";
    public static final String PLUGIN_NOTIFICATION_INFO = "pluginInfo";
    static boolean isdebug = true;

    public static void setDebug(boolean debug) {
        isdebug = debug;
    }

    public static void startServiceforNewPush(Context context, PushInfo pushInfo) {
        Intent intent = new Intent(context, HandleBusinessService.class);
        intent.putExtra(PUSH_INFO, pushInfo);
        intent.setAction(PASSTHROUGH_MESSAGE);
        context.startService(intent);
    }

    public static void startServiceforWifiChangeNotification(Context context, String data) {
        Intent intent = new Intent(context, HandleBusinessService.class);
        intent.putExtra(PLUGIN_NOTIFICATION_INFO, data);
        intent.setAction(PLUGIN_NOTIFICATION);
        context.startService(intent);
    }

    public static void startServiceforScreenOnNotification(Context context, String data) {
        Intent intent = new Intent(context, HandleBusinessService.class);
        intent.putExtra(PLUGIN_NOTIFICATION_INFO, data);
        intent.setAction(PLUGIN_SCREEN_ON);
        context.startService(intent);
    }

    public HandleBusinessService() {
        super("HandleBusinessService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            switch (intent.getAction()) {
                case PASSTHROUGH_MESSAGE:  //处理透传消息,不显示的情况
                    //网络请求数据，展示在通知栏
                    handlerNotify(intent);
                    break;
                case PLUGIN_NOTIFICATION:   //处理插件发来的通知
                    if (isdebug) {
                        Log.d(TAG, "接到插件传来的通知并开启服务");
                    }
                    handlerNotifyForPlugin(intent);
                    break;
                case PLUGIN_SCREEN_ON:
                    if (isdebug) {
                        Log.d("OK", "接到插件传来的通知并开启服务");
                    }
                    parseJsonForScreenOn(intent);   //处理插件发来的亮屏通知
                    break;
            }
        }

    }

    private void parseJsonForScreenOn(Intent intent) {
        if (!TextUtils.isEmpty(intent.getStringExtra(PLUGIN_NOTIFICATION_INFO))) {
            String d = intent.getStringExtra(PLUGIN_NOTIFICATION_INFO);
            //手动解析数据
            try {
                PluginNotificationScreenOnInfo screenoninfo = new PluginNotificationScreenOnInfo();
                JSONObject jsonObject = new JSONObject(d);
                String source = jsonObject.optString("source");
                String s_rpt = jsonObject.optString("s_rpt");
                String c_rpt = jsonObject.optString("c_rpt");
                screenoninfo.source = source;
                screenoninfo.c_rpt = c_rpt;
                screenoninfo.s_rpt = s_rpt;
                if (isdebug) {
                    Log.d(TAG, "接到插件传来的数据解析成功......");
                }
                handlerNotifyForScreenOn(screenoninfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            handlerNotifyForScreenOn(null);
        }
    }

    /**
     * 亮屏通知
     *
     * @param screenoninfo
     */
    private void handlerNotifyForScreenOn(final PluginNotificationScreenOnInfo screenoninfo) {

     /*    if (TextUtils.equals("KSP", screenoninfo.source)) {
            Log.d(TAG, "KSP视频流类型");
            //todo KSP不予合作了，注释...
            //做请求获取信息数据，展示通知
           Api.movies(getApplicationContext()).fetchMVMovies("24", null, new Callback<List<MVItem>>() {
                @Override
                public void onFinish(boolean isSuccess, List<MVItem> items, APIError error, Object tag) {
                    if (!isSuccess) {
                        Log.d(TAG, "获取视频失败");
                        return;
                    }
                    Random random = new Random();
                    MVItem mvItem = items.get(random.nextInt(items.size()));
                    Log.d(TAG, "视频类型成功");
                    if (mvItem != null && mvItem instanceof MVITVideo) {
                        MVITVideo mvitvideo = (MVITVideo) mvItem;
                        handlerUrl(mvitvideo, screenoninfo);
                    }
                }
            });
        } else*/
        requestJGZ(screenoninfo);


    }

    private void requestJGZ(final PluginNotificationScreenOnInfo screenoninfo) {
        //信息流 todo
        Api.chafer(getApplicationContext()).fetchCFNewses("", "", 1, null, new Callback<List<CFItem>>() {
            @Override
            public void onFinish(boolean isSuccess, List<CFItem> cfItems, APIError apiError, Object o) {
                if (!isSuccess) {
                    Log.e(TAG, "获取信息流失败");
                    return;
                }
                List<ScreenOnDataInfo> list = new ArrayList<>();
                Random random = new Random();
                for (CFItem ci : cfItems) {
                    if (ci != null && ci instanceof CFITNews) {
                        CFITNews cfitnews = (CFITNews) ci;
                        ScreenOnDataInfo screenOnDataInfo = new ScreenOnDataInfo();
                        screenOnDataInfo.setTag("JGZ");
                        screenOnDataInfo.setJgz_href(cfitnews.href);

                        List<String> images = cfitnews.images;
                        if (images == null || images.isEmpty()) {
                            continue;
                        }
                        int i = random.nextInt(images.size());
                        String img = images.get(i);
                        ScreenOnData screenOnData = new ScreenOnData(cfitnews.title, cfitnews.source, img, "JGZ");
                        screenOnDataInfo.setSod(screenOnData);
                        screenOnDataInfo.setMvItem(null);
                        if (null != screenoninfo) {
                            screenOnDataInfo.setS_rpt(screenoninfo.s_rpt);
                            screenOnDataInfo.setC_rpt(screenoninfo.c_rpt);
                        } else {
                            Log.d(TAG, "传进来的上报数据为null，那就不上报......");
                        }

                        list.add(screenOnDataInfo);

                    }

                }
                Intent intent = new Intent(getBaseContext(), ActicityScreenon.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putParcelableArrayListExtra("infoz", (ArrayList<? extends Parcelable>) list);
                startActivity(intent);
                if (isdebug) {
                    Log.d(TAG, "信息类型成功");
                }

            }
        });
    }

    private void handlerUrl(final MVITVideo mVideo, final PluginNotificationScreenOnInfo screenoninfo) {
        String params = mVideo.params.replace("SZST__TIME__SECOND", (System.currentTimeMillis() / 1000) + "");
        String auth = SecureUtil.md5(params + mVideo.secret, false, false);
        String url = mVideo.url + "?" + params + "&auth_code=" + auth;
        Api.movies(getApplicationContext()).fetchMVUrl(url, new Callback<String>() {
            @Override
            public void onFinish(boolean isSuccess, String url, APIError error, Object tag) {
                if (!isSuccess) {
                    Log.e(TAG, "获取视频播放地址失败");
                    return;
                }
                if (isdebug) {
                    Log.d(TAG, "获取到视频播放地址：" + url);
                }
                Intent intent = new Intent(getBaseContext(), ActicityScreenon.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ScreenOnDataInfo screenOnDataInfo = new ScreenOnDataInfo();
                screenOnDataInfo.setTag("KSP");
                screenOnDataInfo.setKsp_url(url);
                screenOnDataInfo.setMvItem(mVideo);
                screenOnDataInfo.setS_rpt(screenoninfo.s_rpt);
                screenOnDataInfo.setC_rpt(screenoninfo.c_rpt);
                ScreenOnData screenOnData = new ScreenOnData(mVideo.name, mVideo.title, mVideo.cover, "KSP");
                screenOnDataInfo.setSod(screenOnData);
                intent.putExtra("info", screenOnDataInfo);
                startActivity(intent);
            }
        });
    }


    private void showNotificationforCFITNews(final Context context, CFITNews cfitnews, PluginNotificationScreenOnInfo screenoninfo) {
        final Random random = new Random();
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder mBuilder;
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("2", getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            mBuilder = new Notification.Builder(context, channel.getId());
        } else {
            mBuilder = new Notification.Builder(context);
        }
        mBuilder.setSmallIcon(R.drawable.ic_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setAutoCancel(true);
        RemoteViews bigremoteView = null;
        RemoteViews generalRemoteView = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bigremoteView = new RemoteViews(getPackageName(), R.layout.lock_frequency);
            bigremoteView.setTextViewText(R.id.tv_title, cfitnews.title);
            bigremoteView.setTextViewText(R.id.tv_desc, cfitnews.source);
            bigremoteView.setImageViewResource(R.id.iv_advert, R.drawable.ic_message);
            mBuilder.setCustomBigContentView(bigremoteView);
        } else {
            generalRemoteView = new RemoteViews(getPackageName(), R.layout.general_notification);
            generalRemoteView.setTextViewText(R.id.tv_title, cfitnews.title);
            generalRemoteView.setTextViewText(R.id.tv_desc, cfitnews.source);
            mBuilder.setSmallIcon(R.drawable.ic_message);
            generalRemoteView.setImageViewResource(R.id.tv_icon, R.drawable.ic_message);
            mBuilder.setContent(generalRemoteView);
        }
        final Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = mBuilder.build();
        } else {
            notification = mBuilder.getNotification();
        }
        Api.common(getApplicationContext()).report(new String[]{screenoninfo.s_rpt}, null, null);//显示上报
        if (isdebug) {
            Log.d(TAG, "信息流类型显示上报成功");
        }
        Intent intent = new Intent("com.stkj.onekey.notify.clicked");
        intent.putExtra("tag", "JGZ");
        intent.putExtra("screenoninfo", screenoninfo);
        intent.putExtra("href", cfitnews.href);
        notification.contentIntent = PendingIntent.getBroadcast(context, random.nextInt(1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        List<String> images = cfitnews.images;
        int i = random.nextInt(images.size());
        String img = images.get(i);
        final RemoteViews finalBigremoteView = bigremoteView;
        final RemoteViews finalGeneralRemoteView = generalRemoteView;
        ImageLoader.getInstance().loadImage(img, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (isdebug) {
                    Log.d(TAG, "传空啊");
                }
                notificationManager.notify(random.nextInt(1000), notification);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        finalBigremoteView.setImageViewBitmap(R.id.iv_advert, loadedImage);
                    } else {
                        finalGeneralRemoteView.setImageViewBitmap(R.id.tv_icon, loadedImage);
                    }

                }
                notificationManager.notify(random.nextInt(1000), notification);
                if (isdebug) {
                    Log.d(TAG, "信息类型结束通知");
                }

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (isdebug) {
                    Log.d(TAG, "取消了");
                }
            }
        });

    }

    private void showNotificationforMVideo(final Context context, final MVITVideo mvitvideo, final String url, PluginNotificationScreenOnInfo screenoninfo) {
        final Random random = new Random();
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder mBuilder;
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("2", getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            mBuilder = new Notification.Builder(context, channel.getId());
        } else {
            mBuilder = new Notification.Builder(context);
        }
        RemoteViews bigremoteView = null;
        RemoteViews generalRemoteView = null;
        mBuilder.setSmallIcon(R.drawable.ic_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bigremoteView = new RemoteViews(getPackageName(), R.layout.lock_frequency);
            bigremoteView.setTextViewText(R.id.tv_title, mvitvideo.name);
            bigremoteView.setTextViewText(R.id.tv_desc, mvitvideo.title);
            bigremoteView.setImageViewResource(R.id.iv_advert, R.drawable.ic_message);
            bigremoteView.setImageViewResource(R.id.iv_advert, R.drawable.ic_message);
            mBuilder.setCustomBigContentView(bigremoteView);
        } else {
            generalRemoteView = new RemoteViews(getPackageName(), R.layout.general_notification);
            generalRemoteView.setTextViewText(R.id.tv_title, mvitvideo.name);
            generalRemoteView.setTextViewText(R.id.tv_desc, mvitvideo.title);
            generalRemoteView.setImageViewResource(R.id.tv_icon, R.drawable.ic_message);
            mBuilder.setContent(generalRemoteView);
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setAutoCancel(true);
        final Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = mBuilder.build();
        } else {
            notification = mBuilder.getNotification();
        }

        Api.common(getApplicationContext()).report(new String[]{screenoninfo.s_rpt}, null, null);
        if (isdebug) {
            Log.d(TAG, "视频类型显示上报成功");
        }
        Intent intent = new Intent("com.stkj.onekey.notify.clicked");
        intent.putExtra("tag", "KSP");
        intent.putExtra("url", url);
        intent.putExtra("screenoninfo", screenoninfo);
        intent.putExtra("mvitvideo", mvitvideo);
        notification.contentIntent = PendingIntent.getBroadcast(context, random.nextInt(1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //不判null
        final RemoteViews finalBigremoteView = bigremoteView;
        final RemoteViews finalGeneralRemoteView = generalRemoteView;
        ImageLoader.getInstance().loadImage(mvitvideo.cover, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                notificationManager.notify(random.nextInt(1000), notification);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        finalBigremoteView.setImageViewBitmap(R.id.iv_advert, loadedImage);
                        finalBigremoteView.setImageViewResource(R.id.iv_bg, R.drawable.movie_play_center);
                    } else {
                        finalGeneralRemoteView.setImageViewBitmap(R.id.tv_icon, loadedImage);
                    }

                }
                notificationManager.notify(random.nextInt(1000), notification);
                if (isdebug) {
                    Log.d(TAG, "视频类型结束通知");
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (isdebug) {
                    Log.d(TAG, "取消了");
                }

            }
        });
    }

    /**
     * 处理插件通知
     *
     * @param intent
     */
    private void handlerNotifyForPlugin(Intent intent) {
        if (!TextUtils.isEmpty(intent.getStringExtra(PLUGIN_NOTIFICATION_INFO))) {
            String d = intent.getStringExtra(PLUGIN_NOTIFICATION_INFO);
            //手动解析数据
            try {
                PluginNotificationInfo pluginnotificationinfo = new PluginNotificationInfo();
                JSONObject jsonObject = new JSONObject(d);
                if (isdebug) {
                    Log.d(TAG, "接到插件传来的数据......" + jsonObject.toString());
                }
                String icon = jsonObject.optString("icon");
                String linkType = jsonObject.optString("linkType");
                String title = jsonObject.optString("title");
                String link = jsonObject.optString("link");
                String notifyId = jsonObject.optString("notifyId");
                String s_rpt = jsonObject.optString("s_rpt");
                String content = jsonObject.optString("content");
                String c_rpt = jsonObject.optString("c_rpt");
                String d_rpt = jsonObject.optString("d_rpt");
                String dc_rpt = jsonObject.optString("dc_rpt");
                String i_rpt = jsonObject.optString("i_rpt");
                String a_rpt = jsonObject.optString("a_rpt");
                String dp_rpt = jsonObject.optString("dp_rpt");

                pluginnotificationinfo.setIcon(icon);
                pluginnotificationinfo.setLinkType(linkType);
                pluginnotificationinfo.setTitle(title);
                pluginnotificationinfo.setLink(link);
                pluginnotificationinfo.setNotifyId(notifyId);
                pluginnotificationinfo.setS_rpt(s_rpt);
                pluginnotificationinfo.setContent(content);
                pluginnotificationinfo.setC_rpt(c_rpt);

                pluginnotificationinfo.setD_rpt(d_rpt);
                pluginnotificationinfo.setDc_rpt(dc_rpt);
                pluginnotificationinfo.setI_rpt(i_rpt);
                pluginnotificationinfo.setA_rpt(a_rpt);
                pluginnotificationinfo.setDp_rpt(dp_rpt);
                if (isdebug) {
                    Log.d(TAG, "接到插件传来的数据解析成功......");
                }
                if (pluginnotificationinfo != null) {
                    if (TextUtils.equals(pluginnotificationinfo.getLinkType(), "apk")) {
                        //调用原来的
                        ADNotifyAlive adnotifyalive = new ADNotifyAlive(ADNotifyAliveLinkType.APK, pluginnotificationinfo.getTitle(), pluginnotificationinfo.getLink(), pluginnotificationinfo.getIcon(), pluginnotificationinfo.getContent());
                        adnotifyalive.rpShow = new String[]{pluginnotificationinfo.getS_rpt()};
                        adnotifyalive.rpClick = new String[]{pluginnotificationinfo.getC_rpt()};
                        adnotifyalive.rpDLStart = new String[]{pluginnotificationinfo.getD_rpt()};
                        adnotifyalive.rpDLFinish = new String[]{pluginnotificationinfo.getDc_rpt()};
                        adnotifyalive.rpInstall = new String[]{pluginnotificationinfo.getI_rpt()};
                        adnotifyalive.rpActivate = new String[]{pluginnotificationinfo.getA_rpt()};
                        notifysfoNoShow(getApplicationContext(), null, adnotifyalive);
                    } else {
                        pluginNotification(getApplicationContext(), pluginnotificationinfo);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void handlerNotify(Intent intent) {
        if (intent.getSerializableExtra(PUSH_INFO) != null) {
            Serializable serializableExtra = intent.getSerializableExtra(PUSH_INFO);
            if (serializableExtra != null && serializableExtra instanceof PushInfo) {
                PushInfo pushInfo = (PushInfo) serializableExtra;
                if (pushInfo != null && pushInfo.getPush() != null) {
                    if (!TextUtils.isEmpty(pushInfo.getPush().getLe())) {           //插件处理
                        handlerPlugin(getApplicationContext(), true);
                    } else {
                        handlerPlugin(getApplicationContext(), false);
                    }
                    if (pushInfo.getPush().isIshandle()) {
                        JSONObject jsonObjects = new JSONObject();
                        try {
                            jsonObjects.put("method", "server");
                            jsonObjects.put("param", "push_handle");
                            new AgentMessage.Builder()
                                    .sendTo(ReceiverRole.SERVER)
                                    .content(jsonObjects.toString()) // 消息内容
                                    .action("yt2") // 消息action, 相当于消息头
                                    .build()
                                    .send(getApplicationContext());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //处理push显示问题
                    requestNet(pushInfo);
                }


            }
        }

    }

    protected static boolean isActive;

    /**
     * 处理插件
     *
     * @param context
     * @param isEnable
     */
    private void handlerPlugin(Context context, boolean isEnable) {
        boolean enable = isEnable;
        isActive = isEnable;
        Spi.getInstance().setLatchEnable(enable);
    }

    public void requestNet(final PushInfo pushInfo) {
        Api.common(getApplicationContext()).fetchNotifyAliveInfo(new Callback<ADNotifyAlive>() {
            @Override
            public void onFinish(boolean sucess, ADNotifyAlive adNotifyAlives, APIError apiError, Object o) {
                if (!sucess || adNotifyAlives == null) {
                    return;
                }
                if (!pushInfo.isShow()) {//false 全部替换
                    notifysfoNoShow(getApplicationContext(), pushInfo, adNotifyAlives);
                } else {
                    //不做替换展示push自己
                    notifyforShow(getApplicationContext(), pushInfo, adNotifyAlives);
                }
            }
        });
    }


    public void notifysfoNoShow(final Context context, final PushInfo pushInfo, final ADNotifyAlive adNotifyAlive) {
        final Random random = new Random();
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        final RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.general_notification);
        remoteView.setTextViewText(R.id.tv_title, adNotifyAlive.title);
        remoteView.setTextViewText(R.id.tv_desc, adNotifyAlive.desc);
        mBuilder.setSmallIcon(R.drawable.ic_message);
        remoteView.setImageViewResource(R.id.tv_icon, R.drawable.ic_message);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);
        mBuilder.setContent(remoteView);
        final Notification notification = mBuilder.build();
        OKUI.source = "PUSH";
        String uuid = PropertyUtils.get("ro.aliyun.clouduuid", null);
        Api.common(context).updatePushMark(null, TokenType.YUNOS, uuid);//uuid上报
        Api.common(getApplicationContext()).report(adNotifyAlive.rpShow, null, null);
        if (adNotifyAlive.linkType == ADNotifyAliveLinkType.APK) {//apk  //下载安装，结束了
            Intent intent = new Intent("com.stkj.onekey.notify.clicked");
            intent.putExtra("adNotifyAlive", adNotifyAlive);
            intent.putExtra("pushInfo", pushInfo);
            notification.contentIntent = PendingIntent.getBroadcast(context, random.nextInt(1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notify(adNotifyAlive, random, notificationManager, remoteView, notification);
        } else if (!adNotifyAlive.visible && adNotifyAlive.linkType == ADNotifyAliveLinkType.DEEPLINK) {
            //直接打开不做通知
            NotifyUtil.openDeepLink(context, adNotifyAlive.link, true);
        } else {   //通知广播做处理,push传进去，数据源传入
            Intent intent = new Intent("com.stkj.onekey.notify.clicked");
            intent.putExtra("adNotifyAlive", adNotifyAlive);
            intent.putExtra("pushInfo", pushInfo);
            notification.contentIntent = PendingIntent.getBroadcast(context, random.nextInt(1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notify(adNotifyAlive, random, notificationManager, remoteView, notification);
        }
    }

    private void notify(ADNotifyAlive adNotifyAlive, final Random random, final NotificationManager notificationManager, final RemoteViews remoteView, final Notification notification) {
        ImageSize imagesize = new ImageSize(48, 48);
        ImageLoader.getInstance().loadImage(adNotifyAlive.icon, imagesize, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                notificationManager.notify(random.nextInt(1000), notification);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    remoteView.setImageViewBitmap(R.id.tv_icon, loadedImage);
                }
                notificationManager.notify(random.nextInt(1000), notification);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                System.out.print("sss");
            }
        });
    }

    //显示push过来的数据不做替换
    public static void notifyforShow(Context context, PushInfo pushInfo, ADNotifyAlive adnotifyalive) {
        if (pushInfo == null || pushInfo.getPush() == null || pushInfo.getPush().getData() == null) {
            return;
        }
        Random random = new Random();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(SDK_INT >= LOLLIPOP ? R.mipmap.ic_yijianhuanji : R.mipmap.ic_yijianhuanji);
//        mBuilder.setPriority(Notification.PRIORITY_HIGH);//添加优先级
        mBuilder.setPriority(NotificationManager.IMPORTANCE_DEFAULT);//添加优先级
        mBuilder.setTicker(pushInfo.getPush().getData().getContent());//状态栏提醒
        mBuilder.setContentTitle(pushInfo.getPush().getData().getTitle());//标题
        mBuilder.setContentText(pushInfo.getPush().getData().getContent());//内容
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();
        OKUI.source = "PUSH";
        String uuid = PropertyUtils.get("ro.aliyun.clouduuid", null);
        Api.common(context).updatePushMark(null, TokenType.YUNOS, uuid);//uuid上报
        String action = pushInfo.getPush().getData().getAction();
        Intent click = new Intent(action);
        switch (action) {
            case "com.stkj.onekey.action.TRANSFER_VIA_WIRELESS":
                click.putExtra(KEY_TMODE, WIRELESS);
                break;
            case "com.stkj.onekey.action.TRANSFER_VIA_SDCARD":
                click.putExtra(KEY_TMODE, SD);
                break;
        }
        click.putExtra(KEY_TFROM, true);
        click.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = new Intent("com.stkj.onekey.notify.clicked");
        intent.putExtra("click", click);
        intent.putExtra("isActive", isActive);
        intent.putExtra("pushInfo", pushInfo);
        intent.putExtra("adNotifyAlive", adnotifyalive);
        if (adnotifyalive != null) {
            Api.common(context).report(adnotifyalive.rpShow, null, null);
            if (adnotifyalive.linkType == ADNotifyAliveLinkType.APK) {//apk  //下载安装，结束了
                CustomDownLoadService.CustomDownloadInfo info = new CustomDownLoadService.CustomDownloadInfo(adnotifyalive.link, null, adnotifyalive.icon, adnotifyalive.rpDLStart, adnotifyalive.rpDLFinish, adnotifyalive.rpInstall, adnotifyalive.rpActivate);
                Intent intents = new Intent(context, CustomDownLoadService.class);
                intents.putExtra(CustomDownLoadService.EXTRA_INFO, info);
                notification.contentIntent = PendingIntent.getService(context, random.nextInt(1000), intents, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationManager.notify(random.nextInt(1000), notification);
                return;
            } else if (adnotifyalive.linkType == ADNotifyAliveLinkType.DEEPLINK && !adnotifyalive.visible) {
                //直接打开不做通知
                NotifyUtil.openDeepLink(context, adnotifyalive.link, true);
                return;
            }
        }
        notification.contentIntent = PendingIntent.getBroadcast(context, random.nextInt(1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager.notify(random.nextInt(1000), notification);
    }


    public void pluginNotification(final Context context, final PluginNotificationInfo plugininfo) {
        if (isdebug) {
            Log.d(TAG, "接到插件传来的通知准备显示");
        }
        final Random random = new Random();
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        final RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.general_notification);
        remoteView.setTextViewText(R.id.tv_title, plugininfo.getTitle());
        remoteView.setTextViewText(R.id.tv_desc, plugininfo.getContent());
        mBuilder.setSmallIcon(R.drawable.ic_message);
        remoteView.setImageViewResource(R.id.tv_icon, R.drawable.ic_message);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);
        mBuilder.setContent(remoteView);
        final Notification notification = mBuilder.build();
        Api.common(getApplicationContext()).report(new String[]{plugininfo.getS_rpt()}, null, null);
        if (isdebug) {
            Log.d(TAG, "接到插件传来的通知显示上报");
        }
        Intent intent = new Intent("com.stkj.onekey.notify.clicked");
        intent.putExtra("pluginnotificationinfo", plugininfo);
        notification.contentIntent = PendingIntent.getBroadcast(context, random.nextInt(1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ImageSize imagesize = new ImageSize(48, 48);
        ImageLoader.getInstance().loadImage(plugininfo.getIcon(), imagesize, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                notificationManager.notify(random.nextInt(1000), notification);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    remoteView.setImageViewBitmap(R.id.tv_icon, loadedImage);
                }
                if (isdebug) {
                    Log.d(TAG, "接到插件传来的通知准备显示成功");
                }
                notificationManager.notify(random.nextInt(1000), notification);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }
}