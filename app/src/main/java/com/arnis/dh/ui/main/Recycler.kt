package com.arnis.dh.ui.main

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arnis.dh.R
import com.arnis.dh.data.Item
import com.arnis.dh.ui.preview.PreviewFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.item_cell.view.*

class ItemAdapter(private val onItemClick: ItemClick) : ListAdapter<Item, ItemViewHolder>(object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.sameEntityAs(newItem)
        override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem.sameContentsAs(newItem)
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(View.inflate(parent.context, R.layout.item_cell, null), onItemClick)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(getItem(position))
}

class ItemViewHolder(view: View, private val onItemClick: ItemClick) : RecyclerView.ViewHolder(view) {

    private var colorAnimator: ValueAnimator? = null

    fun bind(data: Item) {
        loadImage(data.image)
        itemView.cellName.text = data.name
        itemView.cellBrand.text = data.brand
        data.price.let { (currency, current, original) ->
            "$original $currency".let {
                itemView.cellOriginalPrice.text = it
            }
            itemView.cellCurrentPrice.isVisible = if (current != original) {
                val originalPriceText = itemView.cellOriginalPrice.text.toString()
                itemView.cellOriginalPrice.text = originalPriceText.toSpannable().apply {
                    setSpan(StrikethroughSpan(), 0, originalPriceText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                "$current $currency".let {
                    itemView.cellCurrentPrice.text = it
                }
                true
            } else
                false
        }
        itemView.setOnClickListener {
            onItemClick.invoke(data)
            val extras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                FragmentNavigatorExtras(itemView.cellImage to data.image)
            else
                null
            itemView.findNavController().navigate(
                R.id.action_preview,
                null,
                null,
                extras
            )
        }
    }

    private fun setColorFrom(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            if (palette != null) {
                // skip first color as it is mostly the background
                val swatch: Palette.Swatch = palette.swatches.asSequence()
                    .sortedByDescending { it.population }.take(2).last()
                colorAnimator?.cancel()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    colorAnimator = ValueAnimator.ofArgb(Color.TRANSPARENT, swatch.rgb).apply {
                        addUpdateListener { anim ->
                            itemView.setBackgroundColor(anim.animatedValue as Int)
                        }
                        duration = 300
                        interpolator = AccelerateDecelerateInterpolator()
                        start()
                    }
                } else {
                    itemView.setBackgroundColor(swatch.rgb)
                }
                sequenceOf(itemView.cellName, itemView.cellBrand, itemView.cellCurrentPrice, itemView.cellOriginalPrice).forEach {
                    it.setTextColor(swatch.bodyTextColor)
                }
            }
        }
    }

    private fun loadImage(url: String) {
        Glide.with(itemView).asBitmap()
            .apply(RequestOptions.downsampleOf(DownsampleStrategy.CENTER_INSIDE))
            .load(url)
            .transition(BitmapTransitionOptions.withCrossFade())
            .into(object : CustomViewTarget<ImageView, Bitmap>(itemView.cellImage) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    itemView.cellImage.setImageBitmap(resource)
                    setColorFrom(resource)
                }
            })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            itemView.cellImage.transitionName = url
    }
}

typealias ItemClick = (Item) -> Unit