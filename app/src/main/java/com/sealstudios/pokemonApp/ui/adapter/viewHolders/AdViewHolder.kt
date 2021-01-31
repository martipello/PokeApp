package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.sealstudios.pokemonApp.databinding.AdLayoutBinding

class AdViewHolder constructor(
    private val binding: AdLayoutBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad : UnifiedNativeAd) = with(binding) {
        val template: TemplateView = this.myTemplate
        val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(
            ColorDrawable(Color.RED)
        ).build()
        template.setStyles(styles)
        template.setNativeAd(ad)
    }

}