package com.kerer.callwidgettestapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by ivan on 19.01.17.
 */

public class WidgetService extends Service {

    private static final String EXTRA_IS_CALL_FINISH = "isCallFinish";
    private static final String EXTRA_NAME_CALLER = "name";

    private WindowManager mWindowManager;
    private View mFloatingView;
    private TextView mShowCallTv;
    private TextView mCallDetailTv;

    private Animation mShowHideAnimation;

    public static final Intent getIntent(Context context, boolean isCallFinish, String callerName) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(EXTRA_IS_CALL_FINISH, isCallFinish);
        intent.putExtra(EXTRA_NAME_CALLER, callerName);

        return intent;
    }

    public WidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean isCallended = intent.getBooleanExtra(EXTRA_IS_CALL_FINISH, true);

        if (isCallended) {
            if (mFloatingView != null) {
                mWindowManager.removeView(mFloatingView);
            }
            stopService(new Intent(this, WidgetService.class));
        } else {
            String callerName = intent.getStringExtra(EXTRA_NAME_CALLER);
            drowWidget(callerName);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void drowWidget(String callerName) {
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.call_widget, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT

        );

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.RIGHT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        mCallDetailTv = (TextView) mFloatingView.findViewById(R.id.testTv2);
        mShowCallTv = (TextView) mFloatingView.findViewById(R.id.testTv);

        mCallDetailTv.setText(getString(R.string.call_detail_description, callerName));

        mFloatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideCallDetail();
            }
        });
    }

    private void showHideCallDetail() {
        if (mCallDetailTv.getVisibility() == View.VISIBLE) {
            mShowHideAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.hide_call_detail_anim);
            mCallDetailTv.setVisibility(View.GONE);
            mShowCallTv.setText(getString(R.string.show));
        } else {
            mShowHideAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.show_call_detail_anim);
            mCallDetailTv.setVisibility(View.VISIBLE);
            mShowCallTv.setText(R.string.hide);
        }
        mFloatingView.startAnimation(mShowHideAnimation);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
