package com.example.michaeldunn.webnav;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

  private static final int ANIMATION_DURATION = 400;

  private WebView mWebView;

  private View mTopNav;
  private View mBottomNav;

  private int mTopNavHeight;
  private int mBottonNavHeight;

  private boolean mIsAtTop;
  private boolean mIsAtBottom;

  private boolean mIsTopNavAnimatingIn;
  private boolean mIsTopNavAnimatingOut;
  private boolean mIsBottonNavAnimatingIn;
  private boolean mIsBottonNavAnimatingOut;

  private float mDensity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mDensity = getResources().getDisplayMetrics().density;

    mWebView = (WebView) findViewById(R.id.webview);
    mWebView.loadUrl("file:///android_asset/demo.html");
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setBuiltInZoomControls(true);
    webSettings.setDisplayZoomControls(false);
    mWebView.addJavascriptInterface(this, "AndroidBridge");

    mTopNav = findViewById(R.id.top_nav);
    mTopNav.addOnLayoutChangeListener(mOnLayoutChangeListener);
    mTopNav.animate().setDuration(ANIMATION_DURATION).setInterpolator(new DecelerateInterpolator());

    mBottomNav = findViewById(R.id.bottom_nav);
    mBottomNav.addOnLayoutChangeListener(mOnLayoutChangeListener);
    mBottomNav.animate().setDuration(ANIMATION_DURATION).setInterpolator(new DecelerateInterpolator());
  }

  @JavascriptInterface
  public void reportScrollToTermination(boolean isAtTop, boolean isAtBottom){
    mIsAtTop = isAtTop;
    mIsAtBottom = isAtBottom;
    runOnUiThread(mUpdateDisplayRunnable);
  }

  private AnimatorListenerAdapter mTopNavAnimatorInListener = new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
      mIsTopNavAnimatingIn = false;
    }
  };

  private AnimatorListenerAdapter mTopNavAnimatorOutListener = new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
      mIsTopNavAnimatingOut = false;
    }
  };

  private AnimatorListenerAdapter mBottomNavAnimatorInListener = new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
      mIsBottonNavAnimatingIn = false;
    }
  };

  private AnimatorListenerAdapter mBottomNavAnimatorOutListener = new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
      mIsBottonNavAnimatingOut = false;
    }
  };

  private Runnable mUpdateDisplayRunnable = new Runnable(){
    @Override
    public void run() {
      updateDisplay();
    }
  };

  private void showTopNav(){
    Log.d("DEBUG", "bringing top nav to front");
    // TODO: what if animating out?
    if(!mIsTopNavAnimatingIn) {
      mIsTopNavAnimatingIn = true;
      mTopNav.setVisibility(View.VISIBLE);
      mTopNav.animate().translationY(0).setListener(mTopNavAnimatorInListener);
    }
  }

  private void hideTopNav(){
    if(!mIsTopNavAnimatingOut){
      mIsTopNavAnimatingOut = true;
      mTopNav.animate().translationY(-mTopNavHeight).setListener(mTopNavAnimatorOutListener);
    }
  }

  private void showBottomNav(){
    Log.d("DEBUG", "bringing bottom nav to front");
    if(!mIsBottonNavAnimatingIn) {
      mIsBottonNavAnimatingIn = true;
      mBottomNav.setVisibility(View.VISIBLE);
      mBottomNav.animate().translationY(0).setListener(mBottomNavAnimatorInListener);
    }
  }

  private void hideBottomNav(){
    if(!mIsBottonNavAnimatingOut) {
      mIsBottonNavAnimatingOut = true;
      mBottomNav.animate().translationY(mBottonNavHeight).setListener(mBottomNavAnimatorOutListener);
    }
  }

  private void updateDisplay(){
    if(mIsAtTop){
      showTopNav();
    } else {
      hideTopNav();
    }
    if(mIsAtBottom){
      showBottomNav();
    } else {
      hideBottomNav();
    }
  }

  private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener(){
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
      mTopNavHeight = mTopNav.getHeight();
      mBottonNavHeight = mBottomNav.getHeight();
      mWebView.loadUrl("javascript:definePadding(" + (mTopNavHeight / mDensity) + ", " + (mBottonNavHeight / mDensity) + ")");
    }
  };

}
