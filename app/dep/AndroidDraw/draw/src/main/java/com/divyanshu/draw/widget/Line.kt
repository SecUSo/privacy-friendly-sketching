package com.divyanshu.draw.widget

import android.graphics.Path
import java.io.Writer
import java.security.InvalidParameterException

class Line(val x: Float, val y: Float) : Action {
    override fun getTargetX(): Float {
        return x
    }

    override fun getTargetY(): Float {
        return y
    }

    override fun perform(path: Path) {
        path.lineTo(x, y)
    }

    override fun perform(writer: Writer) {
        writer.write("L$x,$y")
    }
}