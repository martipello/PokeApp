package com.sealstudios.pokemonApp.ads

import android.content.Context
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.sealstudios.pokemonApp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdsManager (
    private val context: Context
) {

    private lateinit var adLoader: AdLoader

    init {
        MobileAds.initialize(context) { }
    }

    suspend fun createAds(listener: UnifiedNativeAd.OnUnifiedNativeAdLoadedListener){
        withContext(Dispatchers.IO) {
            adLoader = AdLoader.Builder(context, context.getString(R.string.AD_MOB_SAMPLE_AD_UNIT_ID))
                .forUnifiedNativeAd(listener)
                .build()
            adLoader.loadAds(AdRequest.Builder().build(), 5)
        }
    }

    fun getAdLoader() : AdLoader {
        return adLoader
    }

}