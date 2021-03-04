package com.paykun.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import static android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
import static android.content.pm.PackageManager.MATCH_ALL;

public class PaykunIntentHelper {
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static JSONArray getUpiIntentsDataInJsonArray(final Context context) {
        //noinspection CatchMayIgnoreException
        try {
            final List<ResolveInfo> resolveInfoList = getListOfAppsWhichHandleDeepLink(context);
            if (resolveInfoList.size() > 0) {
                final JSONArray jsonArray = new JSONArray();
                for (final ResolveInfo resolveInfo : resolveInfoList) {
                    jsonArray.put((Object) getIntentDataInJson(context, resolveInfo));
                }

                return jsonArray;
            }
        }catch (Exception ex)
        {
            if(ex.getLocalizedMessage()!=null)
            Log.e("Exception",ex.getLocalizedMessage());
        }
        return null;
    }

    private static String getAppNameOfResolveInfo(final ResolveInfo resolveInfo, final Context context) throws Exception {
        try {
            final PackageManager pm = context.getPackageManager();
            @SuppressLint("WrongConstant") final ApplicationInfo applicationInfo = pm.getApplicationInfo(resolveInfo.activityInfo.packageName, FLAG_UPDATED_SYSTEM_APP);
            final int stringId = applicationInfo.labelRes;
            final Resources resources = pm.getResourcesForApplication(applicationInfo);
            return (stringId == 0) ? applicationInfo.nonLocalizedLabel.toString() : resources.getString(stringId);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    private static JSONObject getIntentDataInJson(final Context context, final ResolveInfo resolveInfo) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("package_name", (Object)resolveInfo.activityInfo.packageName);
            final String appName = getAppNameOfResolveInfo(resolveInfo, context);
            jsonObject.put("app_name", (Object)appName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"WrongConstant", "QueryPermissionsNeeded"})
    private static List<ResolveInfo> getListOfAppsWhichHandleDeepLink(final Context context) {
        final Intent intent = new Intent();
        intent.setData(Uri.parse("upi://pay"));
        return (List<ResolveInfo>)context.getPackageManager().queryIntentActivities(intent, MATCH_ALL);
    }
    private static String getBase64FromResource(final Resources resources, final int recourceId) {
        final Bitmap bitmap = BitmapFactory.decodeResource(resources, recourceId);
        if (bitmap != null) {
            String base64 = "data:image/png;base64,";
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, (OutputStream)byteArrayOutputStream);
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64 += Base64.encodeToString(byteArray, 2);
            return base64;
        }
        return null;
    }


}
