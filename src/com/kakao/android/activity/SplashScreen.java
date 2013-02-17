package com.kakao.android.activity;

import com.kakao.android.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.ImageView;

import android.view.View;

public class SplashScreen extends Activity
		implements View.OnClickListener	{

protected GoHomeAfterDelay mDelayTask = null;


public void goHome (boolean doFinish)
{
//    final Intent intent = new Intent(this, MainActivity.class);
    final Intent intent = new Intent(this, LoginActivity.class);
    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
    this.startActivity (intent);
    if (doFinish) finish ();
}

/**
 * Handle a click on a view.
 *
 */    

public void onClick(View v) 
{
    if (mDelayTask != null) {
       mDelayTask.cancel (true);
    }
    goHome (true);
}

/**
 * onCreate - called when the activity is first created.
 *
 */

protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash);

    // Set up to go to the home screen if a user touches the image.
    ImageView i1 = (ImageView) findViewById (R.id.splash_image);
    if (i1 != null) i1.setOnClickListener(this);

    // Set up to go to the home screen after a short delay.
    int delay = getResources ().getInteger (R.integer.splash_screen_time);
    mDelayTask = new GoHomeAfterDelay (this);
    mDelayTask.execute (delay);
}


protected void onDestroy ()
{
    if (mDelayTask != null) {
       mDelayTask.cancel (true);
       mDelayTask.disconnect ();
    }
    mDelayTask = null;
    super.onDestroy ();
}

// Internal classes


public class GoHomeAfterDelay extends AsyncTask<Integer, Integer, Integer> 
{
    private boolean mCancelled = false;
    private SplashScreen mActivity = null;


public GoHomeAfterDelay (SplashScreen context)
{
    mActivity = context;
}


public void disconnect ()
{
    mActivity = null;
} // end disconnect



@Override protected Integer doInBackground(Integer... delayValues) 
{
    int delay = delayValues [0];
    try {
        Thread.sleep (delay);
    } catch (InterruptedException ex) {}

    if (isCancelled ()) return 0;
    else return 1;
}

@Override protected void onCancelled ()
{
    mCancelled = true;
}

@Override protected void onPostExecute (Integer moveCount) 
{
    if (! mCancelled) {
       if (mActivity != null) mActivity.goHome (true);
    }
    mActivity.mDelayTask = null;
}

} // end AsyncTask subclass

} // end class
