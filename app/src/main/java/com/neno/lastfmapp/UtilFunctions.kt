package com.neno.lastfmapp

import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

inline val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Long.formatToTime(timeUnit: TimeUnit): String
{
    return String.format("%d:%02d",
        timeUnit.toMinutes(this),
        timeUnit.toSeconds(this) -
                TimeUnit.MINUTES.toSeconds(timeUnit.toMinutes(this))
    )
}

fun String.toQuery(): String
{
    return this.replace(" ", "+")
}

fun String.isLastFmImage(): Boolean
{
    return this.contains("lastfm.freetls.fastly.net")
}

fun Int.format(): String
{
    return NumberFormat.getNumberInstance().format(this)
}

fun View.setVisible()
{
    visibility = View.VISIBLE
}

fun View.setInvisible()
{
    visibility = View.INVISIBLE
}

fun View.setGone()
{
    visibility = View.GONE
}

fun ImageView.loadImage(imageUrl: String?)
{
    Glide
        .with(this)
        .load(imageUrl)
        .into(this)
}

fun ImageView.loadRoundedImage(
    imageUrl: String?,
    roundedCorners: Int = resources.getInteger(R.integer.rounded_corner_radius)
)
{
    Glide
        .with(this)
        .load(imageUrl)
        .transform(RoundedCorners(roundedCorners))
        .into(this)
}