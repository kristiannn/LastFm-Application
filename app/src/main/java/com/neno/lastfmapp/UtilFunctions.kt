package com.neno.lastfmapp

import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

inline val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline val Long.msToTime: String
    get()
    {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes)

        return String.format("$minutes minutes, $seconds seconds")
    }

inline val String.toQuery: String
    get()
    {
        return this.replace(" ", "+")
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

fun <T> MutableLiveData<T>.modifyValue(transform: T.() -> T)
{
    this.value = this.value?.run(transform)
}