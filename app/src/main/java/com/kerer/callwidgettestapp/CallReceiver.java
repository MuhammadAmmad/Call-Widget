
package com.kerer.callwidgettestapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 *
 */

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!checkPermision(context)) {
            return;
        }

        String callState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        int state = 0;
        //check if call is outgoing
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                String name = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                onOutgoingCallStarted(context, name);
                lastState = TelephonyManager.CALL_STATE_RINGING;
            }
            return;
        }
        String name = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

        state = TelephonyManager.CALL_STATE_RINGING;
        if (callState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            state = TelephonyManager.CALL_STATE_IDLE;
        } else if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

        }

        onCallStateChanged(context, state, name);
    }

    private void onCallStateChanged(Context context, int state, String name) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    onIncomingCallStarted(context, name);
                }
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                if (lastState != TelephonyManager.CALL_STATE_IDLE) {
                    onCallEnded(context);
                }
                break;
        }
        lastState = state;
    }

    protected void onIncomingCallStarted(Context ctx, String name) {
        Log.d(TAG, "onIncomingCallStarted");
        ctx.startService(WidgetService.getIntent(ctx, false, name));

    }

    protected void onOutgoingCallStarted(Context ctx, String name) {
        Log.d(TAG, "onOutgoingCallStarted");

        ctx.startService(WidgetService.getIntent(ctx, false, name));
    }

    protected void onCallEnded(Context ctx) {
        Log.d(TAG, "onCallEnded");
        ctx.startService(WidgetService.getIntent(ctx, true, null));

    }

    private boolean checkPermision(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(context)) {
            context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            showToast(context);
            return false;
        }
        //Check phone state permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            showToast(context);
            return false;
        }

        return true;
    }

    private void showToast(Context context){
        Toast.makeText(context, R.string.no_permissions, Toast.LENGTH_SHORT)
                .show();
    }

}
