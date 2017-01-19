
package com.kerer.callwidgettestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by ivan on 19.01.17.
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
            onOutgoingCallStarted(context);
            lastState = TelephonyManager.CALL_STATE_RINGING;
            return;
        }

        if (callState.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            state = TelephonyManager.CALL_STATE_IDLE;
        } else if (callState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            state = TelephonyManager.CALL_STATE_OFFHOOK;
        } else if(callState.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            state = TelephonyManager.CALL_STATE_RINGING;
        }

        onCallStateChanged(context, state);
    }

    private void onCallStateChanged(Context context, int state){
        switch (state){
            case TelephonyManager.CALL_STATE_RINGING:
                onIncomingCallStarted(context);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                onCallEnded(context);
                break;
        }
        lastState = state;
    }

    protected void onIncomingCallStarted(Context ctx){
        Log.d(TAG, "onIncomingCallStarted");
    }
    protected void onOutgoingCallStarted(Context ctx){
        Log.d(TAG, "onOutgoingCallStarted");
    }
    protected void onCallEnded(Context ctx){
        Log.d(TAG, "onCallEnded");
    }
}
