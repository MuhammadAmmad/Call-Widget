
package com.kerer.callwidgettestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 *
 */

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;

       @Override
       public void onReceive(Context context, Intent intent) {

           String callState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
           int state = 0;
           //check if call is outgoing
           if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
               String name = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
               onOutgoingCallStarted(context, name);
               lastState = TelephonyManager.CALL_STATE_RINGING;
               return;
           }
           String name = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

           state = TelephonyManager.CALL_STATE_RINGING;
           if (callState.equals(TelephonyManager.EXTRA_STATE_IDLE)){
               state = TelephonyManager.CALL_STATE_IDLE;
           } else if (callState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
               state = TelephonyManager.CALL_STATE_OFFHOOK;
           } else if(callState.equals(TelephonyManager.EXTRA_STATE_RINGING)){

           }

           onCallStateChanged(context, state, name);
       }

    private void onCallStateChanged(Context context, int state, String name) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                if (lastState != TelephonyManager.CALL_STATE_RINGING){
                    onIncomingCallStarted(context, name);
                }
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                if (lastState != TelephonyManager.CALL_STATE_IDLE){
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
}
