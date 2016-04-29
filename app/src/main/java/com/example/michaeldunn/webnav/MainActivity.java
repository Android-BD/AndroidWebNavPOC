package com.example.michaeldunn.webnav;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

  private View mTopNav;
  private View mBottomNav;
  private WebView mWebView;

  private int mTopPadding;
  private int mBottomPadding;

  private boolean mIsAtTop;
  private boolean mIsAtBottom;

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
    mWebView.addJavascriptInterface(this, "AndroidBridge");

    mTopNav = findViewById(R.id.top_nav);
    mTopNav.addOnLayoutChangeListener(mOnLayoutChangeListener);

    mBottomNav = findViewById(R.id.bottom_nav);
    mBottomNav.addOnLayoutChangeListener(mOnLayoutChangeListener);
  }

  @JavascriptInterface
  public void reportScroll(int scrollPosition, int windowHeight, int bodyHeight){
    Log.d("DEBUG", "scrollPosition=" + scrollPosition + ", windowHeight=" + windowHeight + ", bodyHeight=" + bodyHeight);
    mIsAtTop = scrollPosition == 0;
    mIsAtBottom = (windowHeight + scrollPosition) >= bodyHeight;
    runOnUiThread(mUpdateDisplayRunnable);
  }

  private Runnable mUpdateDisplayRunnable = new Runnable(){
    @Override
    public void run() {
      updateDisplay();
    }
  };

  private void updateDisplay(){
    mWebView.bringToFront();
    if(mIsAtTop){
      mTopNav.bringToFront();
      Log.d("DEBUG", "bringing top nav to front");
    }
    if(mIsAtBottom){
      mBottomNav.bringToFront();
      Log.d("DEBUG", "bringing bottom nav to front");
    }
  }

  private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener(){
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
      mTopPadding = mTopNav.getHeight();
      mBottomPadding = mBottomNav.getHeight();
      mWebView.loadUrl("javascript:definePadding(" + (mTopPadding / mDensity) + ", " + (mBottomPadding / mDensity) + ")");
    }
  };

}
