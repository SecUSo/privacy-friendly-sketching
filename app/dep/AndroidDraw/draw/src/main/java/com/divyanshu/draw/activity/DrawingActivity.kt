package com.divyanshu.draw.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import android.view.View
import android.widget.SeekBar
import com.divyanshu.draw.R
import com.divyanshu.draw.databinding.ActivityDrawingBinding
import java.io.ByteArrayOutputStream

class DrawingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrawingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater, null, false)

        binding.imageCloseDrawing.setOnClickListener {
            finish()
        }
        binding.imageSendDrawing.setOnClickListener {
            val bStream = ByteArrayOutputStream()
            val bitmap = binding.drawView.getBitmap()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream)
            val byteArray = bStream.toByteArray()
            val returnIntent = Intent()
            returnIntent.putExtra("bitmap", byteArray)
            setResult(Activity.RESULT_OK,returnIntent)
            finish()
        }

        setUpDrawTools()

        colorSelector()

        setPaintAlpha()

        setPaintWidth()
        setContentView(binding.root)
    }

    private fun setUpDrawTools() {
        binding.circleViewOpacity.setCircleRadius(100f)
        binding.imageDrawEraser.setOnClickListener {
            binding.drawView.clearCanvas()
            toggleDrawTools(binding.drawTools,false)
        }
        binding.imageDrawWidth.setOnClickListener {
            if (binding.drawTools.translationY == (56).toPx){
                toggleDrawTools(binding.drawTools,true)
            }else if (binding.drawTools.translationY == (0).toPx && binding.seekBarWidth.visibility == View.VISIBLE){
                toggleDrawTools(binding.drawTools,false)
            }
            binding.circleViewWidth.visibility = View.VISIBLE
            binding.circleViewOpacity.visibility = View.GONE
            binding.seekBarWidth.visibility = View.VISIBLE
            binding.seekBarOpacity.visibility = View.GONE
            binding.drawColorPalette.root.visibility = View.GONE
        }
        binding.imageDrawOpacity.setOnClickListener {
            if (binding.drawTools.translationY == (56).toPx){
                toggleDrawTools(binding.drawTools,true)
            }else if (binding.drawTools.translationY == (0).toPx && binding.seekBarOpacity.visibility == View.VISIBLE){
                toggleDrawTools(binding.drawTools,false)
            }
            binding.circleViewWidth.visibility = View.GONE
            binding.circleViewOpacity.visibility = View.VISIBLE
            binding.seekBarWidth.visibility = View.GONE
            binding.seekBarOpacity.visibility = View.VISIBLE
            binding.drawColorPalette.root.visibility = View.GONE
        }
        binding.imageDrawColor.setOnClickListener {
            if (binding.drawTools.translationY == (56).toPx){
                toggleDrawTools(binding.drawTools,true)
            }else if (binding.drawTools.translationY == (0).toPx && binding.drawColorPalette.root.visibility == View.VISIBLE){
                toggleDrawTools(binding.drawTools,false)
            }
            binding.circleViewWidth.visibility = View.GONE
            binding.circleViewOpacity.visibility = View.GONE
            binding.seekBarWidth.visibility = View.GONE
            binding.seekBarOpacity.visibility = View.GONE
            binding.drawColorPalette.root.visibility = View.VISIBLE
        }
        binding.imageDrawUndo.setOnClickListener {
            binding.drawView.undo()
            toggleDrawTools(binding.drawTools,false)
        }
        binding.imageDrawRedo.setOnClickListener {
            binding.drawView.redo()
            toggleDrawTools(binding.drawTools,false)
        }
    }

    private fun toggleDrawTools(view: View, showView: Boolean = true) {
        if (showView){
            view.animate().translationY((0).toPx)
        }else{
            view.animate().translationY((56).toPx)
        }
    }

    private fun colorSelector() {
        binding.drawColorPalette.imageColorBlack.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_black,null)
            binding.drawView.setColor(color)
            binding.circleViewOpacity.setColor(color)
            binding.circleViewWidth.setColor(color)
            scaleColorView(binding.drawColorPalette.imageColorBlack)
        }
        binding.drawColorPalette.imageColorRed.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_red,null)
            binding.drawView.setColor(color)
            binding.circleViewOpacity.setColor(color)
            binding.circleViewWidth.setColor(color)
            scaleColorView(binding.drawColorPalette.imageColorRed)
        }
        binding.drawColorPalette.imageColorYellow.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_yellow,null)
            binding.drawView.setColor(color)
            binding.circleViewOpacity.setColor(color)
            binding.circleViewWidth.setColor(color)
            scaleColorView(binding.drawColorPalette.imageColorYellow)
        }
        binding.drawColorPalette.imageColorGreen.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_green,null)
            binding.drawView.setColor(color)
            binding.circleViewOpacity.setColor(color)
            binding.circleViewWidth.setColor(color)
            scaleColorView(binding.drawColorPalette.imageColorGreen)
        }
        binding.drawColorPalette.imageColorBlue.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_blue,null)
            binding.drawView.setColor(color)
            binding.circleViewOpacity.setColor(color)
            binding.circleViewWidth.setColor(color)
            scaleColorView(binding.drawColorPalette.imageColorBlue)
        }
        binding.drawColorPalette.imageColorPink.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_pink,null)
            binding.drawView.setColor(color)
            binding.circleViewOpacity.setColor(color)
            binding.circleViewWidth.setColor(color)
            scaleColorView(binding.drawColorPalette.imageColorPink)
        }
        binding.drawColorPalette.imageColorBrown.setOnClickListener {
            val color =  ResourcesCompat.getColor(resources, R.color.color_brown,null)
            binding.drawView.setColor(color)
            binding.circleViewOpacity.setColor(color)
            binding.circleViewWidth.setColor(color)
            scaleColorView(binding.drawColorPalette.imageColorBrown)
        }
    }

    private fun scaleColorView(view: View) {
        //reset scale of all views
        binding.drawColorPalette.imageColorBlack.scaleX = 1f
        binding.drawColorPalette.imageColorBlack.scaleY = 1f

        binding.drawColorPalette.imageColorRed.scaleX = 1f
        binding.drawColorPalette.imageColorRed.scaleY = 1f

        binding.drawColorPalette.imageColorYellow.scaleX = 1f
        binding.drawColorPalette.imageColorYellow.scaleY = 1f

        binding.drawColorPalette.imageColorGreen.scaleX = 1f
        binding.drawColorPalette.imageColorGreen.scaleY = 1f

        binding.drawColorPalette.imageColorBlue.scaleX = 1f
        binding.drawColorPalette.imageColorBlue.scaleY = 1f

        binding.drawColorPalette.imageColorPink.scaleX = 1f
        binding.drawColorPalette.imageColorPink.scaleY = 1f

        binding.drawColorPalette.imageColorBrown.scaleX = 1f
        binding.drawColorPalette.imageColorBrown.scaleY = 1f

        //set scale of selected view
        view.scaleX = 1.5f
        view.scaleY = 1.5f
    }

    private fun setPaintWidth() {
        binding.seekBarWidth.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.drawView.setStrokeWidth(progress.toFloat())
                binding.circleViewWidth.setCircleRadius(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setPaintAlpha() {
        binding.seekBarOpacity.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.drawView.setAlpha(progress)
                binding.circleViewOpacity.setAlpha(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private val Int.toPx: Float
        get() = (this * Resources.getSystem().displayMetrics.density)
}
