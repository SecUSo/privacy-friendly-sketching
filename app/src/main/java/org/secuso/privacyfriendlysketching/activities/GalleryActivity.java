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
package org.secuso.privacyfriendlysketching.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlysketching.R;
import org.secuso.privacyfriendlysketching.activities.helper.BaseActivity;
import org.secuso.privacyfriendlysketching.database.RoomHandler;
import org.secuso.privacyfriendlysketching.database.Sketch;

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.SketchViewHolder> {
    final private int[] sketchIds;
    final private RoomHandler roomHandler;

    private static class GetSketchAsyncTask extends AsyncTask<Integer, Void, Pair<Sketch, Bitmap>> {
        final private RoomHandler roomHandler;
        final private SketchViewHolder holder;

        GetSketchAsyncTask(RoomHandler roomHandler, SketchViewHolder holder) {
            this.roomHandler = roomHandler;
            this.holder = holder;
        }

        @Override
        protected Pair<Sketch, Bitmap> doInBackground(Integer... id) {
            Sketch sketch = this.roomHandler.getSketchSync(id[0]);
            if (sketch == null)
                return new Pair<>(null, null);
            Bitmap image = sketch.getFullImage(1024, 1024);
            return new Pair<>(sketch, image);
        }

        @Override
        protected void onPostExecute(Pair<Sketch, Bitmap> data) {
            int sketchId = GalleryActivity.getSketchIdFromView(holder.cardView);
            if (data.first != null && sketchId != data.first.id)
                return;
            if (data.first == null) {
                holder.getTextView().setText(String.format("Error loading sketch id=%d", sketchId));
            } else {
                holder.cardView.setTag(data.first);
                holder.getTextView().setText(data.first.getDescription());
                holder.getImageView().setImageBitmap(data.second);
            }
            holder.cardView.animate().alpha(1);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SketchViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cardView;
        AsyncTask asyncTask;

        SketchViewHolder(CardView v) {
            super(v);
            cardView = v;
        }

        public ImageView getImageView() {
            return (ImageView) this.cardView.findViewById(R.id.image_view);
        }

        TextView getTextView() {
            return ((TextView) this.cardView.findViewById(R.id.info_text));
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    GalleryAdapter(RoomHandler roomHandler, int[] sketchIds) {
        this.roomHandler = roomHandler;
        this.sketchIds = sketchIds;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public SketchViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gallery_entry, parent, false);

        v.setOnLongClickListener((GalleryActivity) parent.getContext());
        return new SketchViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull SketchViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardView.setTag(sketchIds[position]);
        holder.cardView.animate().cancel();
        holder.cardView.setAlpha(0);
        holder.getTextView().setText(null);
        holder.getImageView().setImageBitmap(null);
        if (holder.asyncTask != null && holder.asyncTask.getStatus() == AsyncTask.Status.PENDING)
            holder.asyncTask.cancel(true);
        holder.asyncTask = new GetSketchAsyncTask(this.roomHandler, holder).execute(sketchIds[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.sketchIds.length;
    }
}

public class GalleryActivity extends BaseActivity implements View.OnLongClickListener {
    private RecyclerView recyclerView;

    static int getSketchIdFromView(View v) {
        Object tag = v.getTag();
        if (tag instanceof Sketch)
            return ((Sketch) tag).id;
        else
            return (int) tag;
    }

    static String getSketchDescriptionFromView(View v) {
        Object tag = v.getTag();
        if (tag instanceof Sketch)
            return ((Sketch) tag).getDescription();
        else
            return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager;
        int orientation = getResources().getConfiguration().orientation;
        int amountOfColums;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            amountOfColums = 4;
        } else {
            amountOfColums = 2;
        }
        layoutManager = new GridLayoutManager(this, amountOfColums);
        recyclerView.setLayoutManager(layoutManager);
    }

    private static class GetSketchCountAsyncTask extends AsyncTask<Void, Void, int[]> {
        final private RecyclerView recyclerView;
        final private RoomHandler roomHandler;

        GetSketchCountAsyncTask(RoomHandler roomHandler, RecyclerView recyclerView) {
            this.roomHandler = roomHandler;
            this.recyclerView = recyclerView;
        }

        @Override
        protected int[] doInBackground(Void... params) {
            return this.roomHandler.getSketchIds();
        }

        @Override
        protected void onPostExecute(int[] ids) {
            RecyclerView.Adapter adapter = new GalleryAdapter(roomHandler, ids);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getRoomHandler().deleteSketch(SketchActivity.TEMP_SKETCH_ID);
        GetSketchCountAsyncTask asyncTask = new GetSketchCountAsyncTask(getRoomHandler(), recyclerView);
        asyncTask.execute();
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_gallery;
    }

    @Override
    public boolean onLongClick(View v) {
        deleteSketch(v);
        return true;
    }

    public void deleteSketch(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_delete_message)
                .setMessage(getSketchDescriptionFromView(view));

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            int sketchId = getSketchIdFromView(view);

            public void onClick(DialogInterface dialog, int id) {
                getRoomHandler().deleteSketch(sketchId);
                recreate();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void editSketch(View view) {
        Intent intent = new Intent(this, SketchActivity.class);
        intent.putExtra("sketchId", getSketchIdFromView(view));
        startActivity(intent);
    }

    public void addSketch(View view) {
        Intent intent = new Intent(this, SketchActivity.class);
        startActivity(intent);
    }
}
