package com.divyanshu.draw.widget

import android.graphics.Color
import java.io.Serializable

data class PaintOptions(var color: Int = Color.BLACK, var strokeWidth: Float = 8f, var alpha: Int = 255): Serializable

