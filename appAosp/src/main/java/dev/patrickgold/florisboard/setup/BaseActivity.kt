//package dev.patrickgold.florisboard.setup
//
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.ads.formats.UnifiedNativeAdView
//import dev.patrickgold.florisboard.R
//import dev.patrickgold.florisboard.ads.NativeAds
//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.layout_native_ad.*
//
// open class BaseActivity : AppCompatActivity() {
//
//
//  fun showNative() {
//   NativeAds.loadNativeAd(this, R.string.native_video_ad) {
//    if (isDestroyed) {
//     it.destroy()
//     return@loadNativeAd
//    }
//    //            currentNativeAd?.destroy()
//    //            currentNativeAd = newNativeAd
//    val adView = layoutInflater
//     .inflate(R.layout.layout_native_ad, unifiedNativeAdView) as UnifiedNativeAdView
//    NativeAds.showNativeAd(it, adView)
//    activiy_ad_frame.removeAllViews()
//    activiy_ad_frame.addView(adView)
//   }
//  }
//
//}