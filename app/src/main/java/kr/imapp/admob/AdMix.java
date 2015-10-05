package kr.imapp.admob;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdMix extends AdListener {
	private Activity activity;
	private AdView adView;
	private int layoutId;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(activity, "Looks like using Ad Blocker!", Toast.LENGTH_LONG).show();
		}
	};
	
	private boolean isAdBlockerPresent() {
       BufferedReader in = null;
       try {
    	   FileInputStream fi = new FileInputStream("/etc/hosts");
        	   InputStreamReader ir = new InputStreamReader(fi);
               in = new BufferedReader(ir);
               String line;
 
               while ((line = in.readLine()) != null) {
                  if (line.contains("admob")) {
                  return true;
              }
            }
       } catch (Exception e) {
       } finally {
           if (in != null) {
               try {
                   in.close();
               } catch (IOException e) {
               }
           }
       }
       return false;
    }	
	
	private LinearLayout adLayout; 

	
	public AdMix show(Activity a, int layout, String id) {
		activity = a;
		layoutId = layout;
		
		adView = new AdView(a);
		adView.setAdUnitId(id);
		adView.setAdSize(AdSize.SMART_BANNER);
//		adView.setAdSize(AdSize.BANNER);
		adLayout = (LinearLayout)a.findViewById(layout);
		if ( adLayout == null )
			throw new RuntimeException("ad layout not found");
		
		AdRequest request = new AdRequest.Builder().build();
		adView.setAdListener( new AdListener() {
			@Override
			public void onAdFailedToLoad(int errorCode) {
				if ( isAdBlockerPresent() ) {
					handler.sendEmptyMessage(710523);
					activity.finish();
				}
				super.onAdFailedToLoad(errorCode);
			}
		});
		adView.loadAd(request);

		adLayout.addView(adView);
		
		return this;
	}

	public AdMix show(Activity a, LinearLayout layout, String id) {
		activity = a;

		adView = new AdView(a);
		adView.setAdUnitId(id);
//		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdSize(AdSize.BANNER);
		adLayout = layout;
		if ( adLayout == null )
			throw new RuntimeException("ad layout not found");
		AdRequest request = new AdRequest.Builder().build();
		adView.setAdListener( new AdListener() {
			@Override
			public void onAdFailedToLoad(int errorCode) {
				if ( isAdBlockerPresent() ) {
					handler.sendEmptyMessage(710523);
					activity.finish();
				}
				super.onAdFailedToLoad(errorCode);
			}
		});
		adView.loadAd(request);
		adLayout.addView(adView);
		
		return this;
	}
	
	public void setVisibility(int visibility) {
		if ( adLayout != null )
			adLayout.setVisibility(visibility);
	}
	
	private InterstitialAd interstitial;
	public AdMix show(Activity a, String id) {
		activity = a;

	    // Create the interstitial
	    interstitial = new InterstitialAd(a);
	    interstitial.setAdUnitId(id);

	    // Create ad request
	    AdRequest adRequest = new AdRequest.Builder().build();

	    // Begin loading your interstitial
	    interstitial.loadAd(adRequest);

	    // Set Ad Listener to use the callbacks below
	    interstitial.setAdListener(this);		
		return this;
	}
	public void remove(LinearLayout layout) {
		layout.removeView(adView);
	}
	public void destroy() {
		if ( adView != null ) {
			adView.destroy();
			adView = null;
		}
	}
	public void resume() {
		if ( adView != null ) {
			adView.resume();
		}
	}
	public void pause() {
		if ( adView != null ) {
			adView.pause();
		}
	}
	
	public void onAdLoaded() {
		interstitial.show();
	}
	
}

