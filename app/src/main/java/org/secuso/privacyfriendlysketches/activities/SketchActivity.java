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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.divyanshu.draw.widget.DrawView;
import com.divyanshu.draw.widget.MyPath;
import com.divyanshu.draw.widget.PaintOptions;

import org.secuso.privacyfriendlysketches.R;
import org.secuso.privacyfriendlysketches.activities.helper.BaseActivity;
import org.secuso.privacyfriendlysketches.database.Sketch;

import java.io.FileNotFoundException;
import java.io.IOException;
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
    static final int TEMP_SKETCH_ID = -2;

    static final int IMAGE_RESULT_CODE = 1;

    private boolean toolbarOpen = false;
    private ToolbarMode toolbarMode = ToolbarMode.None;
    private DrawView drawView;
    private View toolbar;

    private int sketchId = NEW_SKETCH_ID;
    private int focusedColor = 0;

    private View colorPalette;
    private SeekBar seekBarWidth;
    private SeekBar seekBarOpacity;

    private Sketch sketch = null;
    private AlertDialog backgroundColorSelectDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        drawView = findViewById(R.id.draw_view);
        toolbar = findViewById(R.id.draw_tools);

        colorPalette = findViewById(R.id.draw_color_palette);
        seekBarWidth = findViewById(R.id.seekBar_width);
        seekBarOpacity = findViewById(R.id.seekBar_opacity);

        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawView.setStrokeWidth((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawView.setAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        boolean isTemp = false;
        if (savedInstanceState != null) {
            this.sketchId = savedInstanceState.getInt("sketchId");
            if (sketchId == TEMP_SKETCH_ID) {
                isTemp = true;
                Sketch s = getRoomHandler().getSketch(sketchId);
                if (s != null) {
                    this.sketch = s;
                    LinkedHashMap<MyPath, PaintOptions> path = s.getPaths();
                    if (path != null)
                        for (Map.Entry<MyPath, PaintOptions> itr : path.entrySet())
                            drawView.addPath(itr.getKey(), itr.getValue());
                    drawView.setBackground(s.getBitmap());
                    getRoomHandler().deleteSketch(TEMP_SKETCH_ID);
                }
            }
        }
        if (!isTemp) {
            Bundle b = getIntent().getExtras();
            if (b != null) {
                sketchId = b.getInt("sketchId", NEW_SKETCH_ID);
                if (sketchId != NEW_SKETCH_ID) {
                    Sketch sketch = getRoomHandler().getSketch(sketchId);
                    this.sketch = sketch;
                    LinkedHashMap<MyPath, PaintOptions> path = sketch.getPaths();
                    if (path != null)
                        for (Map.Entry<MyPath, PaintOptions> itr : path.entrySet())
                            drawView.addPath(itr.getKey(), itr.getValue());
                    drawView.setBackground(sketch.getBitmap());
                }
            }


        }

        ImageView iv = findViewById(R.id.image_close_drawing);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SKETCH ACTIVITY", "top left menu clicked");
                final AlertDialog.Builder builder = new AlertDialog.Builder(SketchActivity.this);
                builder.setTitle(R.string.what_would_you_like_to_do)
                        .setItems(new String[]{
                                getResources().getString(R.string.select_background),
                                getResources().getString(R.string.rename_sketch),
                                getResources().getString(R.string.export_sketch)
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Log.i("SKETCH ACTIVITY", "button " + which + " clicked");
                                dialogInterface.dismiss();
                                switch (which) {
                                    case 0: //select background
                                        Log.i("SKETCH ACTIVITY", "renaming..");
                                        final AlertDialog.Builder backgroundBuilder = new AlertDialog.Builder(SketchActivity.this);
                                        backgroundBuilder.setTitle(R.string.select_background);

                                        //create a dialog to select bgColor or bgImage
                                        LinearLayout l = new LinearLayout(SketchActivity.this);
                                        l.setOrientation(LinearLayout.VERTICAL);

                                        View backgroundPalette = LayoutInflater.from(SketchActivity.this).inflate(R.layout.select_background_menu, null);
//
                                        backgroundBuilder.setView(backgroundPalette);
                                        final AlertDialog dialog = backgroundBuilder.create();
                                        SketchActivity.this.backgroundColorSelectDialog = dialog;
                                        dialog.show();
                                        break;
                                    case 1: //rename sketch
                                        Log.i("SKETCH ACTIVITY", "renaming..");
                                        AlertDialog.Builder renameBuilder = new AlertDialog.Builder(SketchActivity.this);
                                        renameBuilder.setTitle(R.string.rename_sketch);

                                        final EditText input = new EditText(SketchActivity.this);
                                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                                        renameBuilder.setView(input);

                                        renameBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String newName = input.getText().toString();
                                                Sketch s = new Sketch(SketchActivity.this.drawView.getPaintBackground(), SketchActivity.this.drawView.getMPaths(), newName);
                                                if (SketchActivity.this.sketchId != SketchActivity.this.NEW_SKETCH_ID) {
                                                    SketchActivity.this.sketch = s;
                                                } else {
                                                    getRoomHandler().insertSketch(s);
                                                }
                                            }
                                        });
                                        renameBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                return;
                                            }
                                        });

                                        renameBuilder.create();
                                        renameBuilder.show();
                                        break;
                                    case 2: //export sketch
                                        break;
                                    default:
                                        return;
                                }

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (drawView.getMPaths().size() == 0)
            return;


        if (sketchId != NEW_SKETCH_ID) {
            Sketch sketch = new Sketch(this.drawView.getPaintBackground(), this.drawView.getMPaths(),
                    this.sketch.description);
            sketch.id = sketchId;
            getRoomHandler().updateSketch(sketch);
        } else {
            Sketch sketch = new Sketch(this.drawView.getPaintBackground(), this.drawView.getMPaths(),
                    DateFormat.getDateTimeInstance().format(new Date()));
            getRoomHandler().insertSketch(sketch);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getRoomHandler().deleteSketch(TEMP_SKETCH_ID);
        super.onSaveInstanceState(outState);

        if (drawView.getMPaths().size() == 0) {
            return;
        }

        Sketch s = new Sketch(this.drawView.getPaintBackground(), this.drawView.getMPaths(), DateFormat.getDateInstance().format(new Date()));
        if (sketchId == TEMP_SKETCH_ID || sketchId == NEW_SKETCH_ID) {
            s.id = TEMP_SKETCH_ID;
            getRoomHandler().insertSketch(s);
            outState.putInt("sketchId", TEMP_SKETCH_ID);
        }

    }


    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_sketch;
    }

//    public void topLeftMenu(View view) {
//        Log.i("SKETCH ACTIVITY", "long clicked");
//        AlertDialog.Builder builder = new AlertDialog.Builder(SketchActivity.this);
//        builder.setTitle(R.string.dialog_delete_message)
//                .setMessage(R.string.what_would_you_like_to_do).setItems(new String[]{
//                getResources().getString(R.string.select_background),
//                getResources().getString(R.string.rename_sketch),
//                getResources().getString(R.string.export_sketch)
//        }, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    public void onClick(View view) {
        ToolbarMode toolbarMode = ToolbarMode.None;

        switch (view.getId()) {
            case R.id.image_draw_eraser:
                clearCanvasDialogue();
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
            default:
                break;
        }

        if (toolbarMode != ToolbarMode.None) {
            if (toolbarMode == this.toolbarMode || !toolbarOpen)
                toggleToolbar();

            setToolbarMode(toolbarMode);
        }
    }

    private void clearCanvasDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_delete_message)
                .setMessage(R.string.dialog_delete_message);

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                drawView.clearCanvas();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSelectBackgroundImage(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_RESULT_CODE);
        if (this.backgroundColorSelectDialog != null) {
            this.backgroundColorSelectDialog.dismiss();
        }
    }

    public void onSelectBackgroundColor(View v) {
        int colorId = 0;
        switch (v.getId()) {
            case R.id.image_color_black:
                colorId = R.color.color_black;
                break;
            case R.id.image_color_blue:
                colorId = R.color.color_blue;
                break;
            case R.id.image_color_brown:
                colorId = R.color.color_brown;
                break;
            case R.id.image_color_green:
                colorId = R.color.color_green;
                break;
            case R.id.image_color_pink:
                colorId = R.color.color_pink;
                break;
            case R.id.image_color_red:
                colorId = R.color.color_red;
                break;
            case R.id.image_color_yellow:
                colorId = R.color.color_yellow;
                break;
        }

        if (colorId != 0) {
            Bitmap b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            b.eraseColor(getResources().getColor(colorId));
            drawView.setBackground(b);
            if (this.backgroundColorSelectDialog != null) {
                this.backgroundColorSelectDialog.dismiss();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                this.drawView.setBackground(bmp);
            } catch (IOException e) {
                Toast.makeText(this, R.string.error_loading_image, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onSelectColor(View view) {
        int colorId = 0;
        int toFocusedColor = view.getId();

        switch (view.getId()) {
            case R.id.image_color_black:
                colorId = R.color.color_black;
                break;
            case R.id.image_color_blue:
                colorId = R.color.color_blue;
                break;
            case R.id.image_color_brown:
                colorId = R.color.color_brown;
                break;
            case R.id.image_color_green:
                colorId = R.color.color_green;
                break;
            case R.id.image_color_pink:
                colorId = R.color.color_pink;
                break;
            case R.id.image_color_red:
                colorId = R.color.color_red;
                break;
            case R.id.image_color_yellow:
                colorId = R.color.color_yellow;
                break;
        }

        if (colorId != 0) {
            changeColorFocus(this.focusedColor, toFocusedColor);
            this.focusedColor = toFocusedColor;
            drawView.setColor(getResources().getColor(colorId));
        }
    }

    private void changeColorFocus(int fromColorId, int toColorId) {
        if (fromColorId == 0) {
            fromColorId = R.id.image_color_black;
        }
        ImageView fromView = findViewById(fromColorId);
        fromView.setScaleX(1f);
        fromView.setScaleY(1f);

        ImageView toView = findViewById(toColorId);
        toView.setScaleY(1.5f);
        toView.setScaleX(1.5f);
    }

    private void setToolbarMode(ToolbarMode mode) {
        if (this.toolbarMode == mode)
            return;

        colorPalette.setVisibility(View.GONE);
        seekBarWidth.setVisibility(View.GONE);
        seekBarOpacity.setVisibility(View.GONE);

        switch (mode) {
            case Color:
                colorPalette.setVisibility(View.VISIBLE);
                break;
            case Width:
                seekBarWidth.setVisibility(View.VISIBLE);
                break;
            case Opacity:
                seekBarOpacity.setVisibility(View.VISIBLE);
                break;
            default:
                throw new IllegalArgumentException();
        }

        this.toolbarMode = mode;
    }

    private void toggleToolbar() {
        toolbar.animate().translationY((toolbarOpen ? 56 : 0) * getResources().getDisplayMetrics().density);
        toolbarOpen = !toolbarOpen;
    }

//        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
//            int sketchId = getSketchIdFromView(view);
//
//            public void onClick(DialogInterface dialog, int id) {
//                getRoomHandler().deleteSketch(sketchId);
//                recreate();
//            }
//        });
    //builder.setNegativeButton(R.string.dialog_cancel, null);

}

