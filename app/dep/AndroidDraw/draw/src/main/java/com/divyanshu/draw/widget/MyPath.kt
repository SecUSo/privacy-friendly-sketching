package com.divyanshu.draw.widget

import android.graphics.Path
import android.graphics.RectF
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.*

class MyPath : Path(), Serializable {
    val actions = LinkedList<Action>()

    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()

        val copiedActions = actions.map { it }
        actions.clear()
        copiedActions.forEach {
            it.perform(this)
        }
    }

    override fun reset() {
        actions.clear()
        super.reset()
    }

    override fun moveTo(x: Float, y: Float) {
        actions.add(Move(x, y))
        super.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        actions.add(Line(x, y))
        super.lineTo(x, y)
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        actions.add(Quad(x1, y1, x2, y2))
        super.quadTo(x1, y1, x2, y2)
    }

    fun getBounds(): RectF {
        if (actions.size == 0)
            return RectF()

        val result = RectF(actions[0].getTargetX(), actions[0].getTargetY(), actions[0].getTargetX(), actions[0].getTargetY())
        actions.forEach{ itr ->
            if (result.left > itr.getTargetX())
                result.left = itr.getTargetX()
            if (result.right < itr.getTargetX())
                result.right = itr.getTargetX()
            if (result.top > itr.getTargetY())
                result.top = itr.getTargetY()
            if (result.bottom < itr.getTargetY())
                result.bottom = itr.getTargetY()
        }
        return result
    }
}