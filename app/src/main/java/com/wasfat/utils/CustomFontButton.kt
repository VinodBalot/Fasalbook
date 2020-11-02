package com.wasfat.utils
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.wasfat.R



class CustomFontButton : AppCompatButton {
    private var customFont: String? = null

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        style(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        style(context, attrs)
    }

    private fun style(
        context: Context,
        attrs: AttributeSet?
    ) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomFontButton
        )
        val cf = a.getInteger(R.styleable.CustomFontTextView_CustomFontName, 0)
        val btnFontName: Int
        btnFontName = when (cf) {
            1 -> R.string.ProximaNovaBold
            2 -> R.string.ProximaNovaLight
            3 -> R.string.ProximaNovaReg
            4 -> R.string.ProximaNovaRegIt
            5 -> R.string.ProximaNovaThin
            6 -> R.string.ProximaNovaXbold
            else -> R.string.ProximaNovaReg
        }
        customFont = resources.getString(btnFontName)
        val tf = Typeface.createFromAsset(
            context.assets,
            "$customFont.ttf"
        )
        typeface = tf
        a.recycle()
    }
}
