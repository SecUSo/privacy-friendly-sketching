package org.secuso.privacyfriendlysketches.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import org.secuso.privacyfriendlysketches.R;
import org.secuso.privacyfriendlysketches.activities.helper.BaseActivity;

import com.divyanshu.draw.widget.DrawView;

enum ToolbarMode {
    None,
    Width,
    Color,
    Opacity
}

public class SketchActivity extends BaseActivity {
    private boolean toolbarOpen = false;
    private ToolbarMode toolbarMode = ToolbarMode.None;
    private DrawView drawView;
    private View toolbar;

    private View colorPalette;
    private SeekBar seekBarWidth;
    private SeekBar seekBarOpacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);

        drawView = findViewById(R.id.draw_view);
        toolbar = findViewById(R.id.draw_tools);

        colorPalette =  findViewById(R.id.draw_color_palette);
        seekBarWidth =  findViewById(R.id.seekBar_width);
        seekBarOpacity =  findViewById(R.id.seekBar_opacity);

        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawView.setStrokeWidth((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawView.setAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_sketch;
    }

    public void onClick(View view) {
        ToolbarMode toolbarMode = ToolbarMode.None;

        switch(view.getId()) {
            case R.id.image_draw_eraser:
                drawView.clearCanvas();
                break;
            case R.id.image_draw_width:
                toolbarMode = ToolbarMode.Width;
                break;
            case R.id.image_draw_opacity:
                toolbarMode = ToolbarMode.Opacity;
                break;
            case R.id.image_draw_color:
                toolbarMode = ToolbarMode.Color;
                break;
            default: break;
        }

        if (toolbarMode != ToolbarMode.None) {
            if (toolbarMode == this.toolbarMode || !toolbarOpen)
                toggleToolbar();

            setToolbarMode(toolbarMode);
        }
    }

    public void onSelectColor(View view) {
        int colorId = 0;

        switch(view.getId()) {
            case R.id.image_color_black: colorId = R.color.color_black; break;
            case R.id.image_color_blue: colorId = R.color.color_blue; break;
            case R.id.image_color_brown: colorId = R.color.color_brown; break;
            case R.id.image_color_green: colorId = R.color.color_green; break;
            case R.id.image_color_pink: colorId = R.color.color_pink; break;
            case R.id.image_color_red: colorId = R.color.color_red; break;
            case R.id.image_color_yellow: colorId = R.color.color_yellow; break;
        }

        if (colorId != 0) {
            drawView.setColor(getResources().getColor(colorId));
        }
    }

    private void setToolbarMode(ToolbarMode mode) {
        if (this.toolbarMode == mode)
            return;

        colorPalette.setVisibility(View.GONE);
        seekBarWidth.setVisibility(View.GONE);
        seekBarOpacity.setVisibility(View.GONE);

        switch (mode) {
            case Color: colorPalette.setVisibility(View.VISIBLE); break;
            case Width: seekBarWidth.setVisibility(View.VISIBLE); break;
            case Opacity: seekBarOpacity.setVisibility(View.VISIBLE); break;
            default: throw new IllegalArgumentException();
        }

        this.toolbarMode = mode;
    }

    private void toggleToolbar() {
        toolbar.animate().translationY((toolbarOpen ? 56 : 0) * getResources().getDisplayMetrics().density);
        toolbarOpen = !toolbarOpen;
    }
}
