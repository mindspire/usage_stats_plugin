package com.droidada.plugin;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.LayoutInflater;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Temperature extends CordovaPlugin {

  public static int STOPPED = 0;
  public static int STARTING = 1;
  public static int RUNNING = 2;
  public static int ERROR_FAILED_TO_START = 3;

  public long TIMEOUT = 30000;

  int status;
  long timeStamp;
  long lastAccessTime;
  private UsageStatsManager mUsageStatsManager;
  private PackageManager mPm;

  SensorManager sensorManager;
  Sensor sensor;

  Float temperature;
  private CallbackContext callbackContext;
  
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

  }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      this.callbackContext = callbackContext;

      if (action.equals("getUsage")) {
         this.start();
         return true;
      }

      return false;
    }


  public void start () {
    mUsageStatsManager = (UsageStatsManager) cordova.getActivity().getSystemService(Context.USAGE_STATS_SERVICE);
        mPm = cordova.getActivity().getPackageManager(); 

        ArrayList<String> app_time = new ArrayList<>();

        //Stats from API
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);

        final List<UsageStats> stats =
                mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                        cal.getTimeInMillis(), System.currentTimeMillis());
        if (stats == null) {
            return;
        }

        final int statCount = stats.size();

        for (int i = 0; i < statCount; i++) {
            final android.app.usage.UsageStats pkgStats = stats.get(i);

            // load application labels for each application
            try {
                ApplicationInfo appInfo = mPm.getApplicationInfo(pkgStats.getPackageName(), 0);
                String label = appInfo.loadLabel(mPm).toString();
                app_time.add(label + ": " +  DateUtils.formatElapsedTime(pkgStats.getTotalTimeInForeground() / 1000));


            } catch (PackageManager.NameNotFoundException e) {
                // This package may be gone
            }
        }

        callbackContext.success(app_time.toString());
  }

}
