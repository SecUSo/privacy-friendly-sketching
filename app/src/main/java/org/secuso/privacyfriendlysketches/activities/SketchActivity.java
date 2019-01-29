/*
 This file is part of Privacy Friendly Sketching.

 Privacy Friendly Sketching is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Sketching is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Sketching. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.privacyfriendlysketches.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.divyanshu.draw.widget.DrawView;
import com.divyanshu.draw.widget.MyPath;
import com.divyanshu.draw.widget.PaintOptions;

import org.secuso.privacyfriendlysketches.R;
import org.secuso.privacyfriendlysketches.activities.helper.BaseActivity;
import org.secuso.privacyfriendlysketches.database.Sketch;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

enum ToolbarMode {
    None,
    Width,
    Color,
    Opacity
}

public class SketchActivity extends BaseActivity {
    static final int NEW_SKETCH_ID = -1;

    private boolean toolbarOpen = false;
    private ToolbarMode toolbarMode = ToolbarMode.None;
    private DrawView drawView;
    private View toolbar;

    private int sketchId = NEW_SKETCH_ID;

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

        Bundle b = getIntent().getExtras();
        if (b != null) {
            sketchId = b.getInt("sketchId", NEW_SKETCH_ID);
            if (sketchId != NEW_SKETCH_ID) {
                Sketch sketch = getRoomHandler().getSketch(sketchId);

                LinkedHashMap<MyPath, PaintOptions> path = sketch.getPaths();
                if (path != null)
                    for (Map.Entry<MyPath, PaintOptions> itr : path.entrySet())
                        drawView.addPath(itr.getKey(), itr.getValue());
                drawView.setBackground(sketch.getBitmap());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (drawView.getMPaths().size() == 0)
            return;

        Sketch sketch = new Sketch(this.drawView.getPaintBackground(), this.drawView.getMPaths(),
                DateFormat.getDateTimeInstance().format(new Date()));
        if (sketchId != NEW_SKETCH_ID) {
            sketch.id = sketchId;
            getRoomHandler().updateSketch(sketch);
        }
        else
            getRoomHandler().insertSketch(sketch);
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
            case R.id.image_draw_undo:
                drawView.undo();
                break;
            case R.id.image_draw_redo:
                drawView.redo();
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
