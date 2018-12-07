package org.secuso.privacyfriendlysketches.activities;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlysketches.R;
import org.secuso.privacyfriendlysketches.activities.helper.BaseActivity;
import org.secuso.privacyfriendlysketches.database.RoomHandler;
import org.secuso.privacyfriendlysketches.database.Sketch;
import org.secuso.privacyfriendlysketches.database.SketchData;

class TestData implements SketchData {
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Bitmap getBitmap() {
        final int SIZE = 64;
        int[] data = new int[SIZE * SIZE];
        for (int x = 0; x < SIZE; ++x)
            for (int y = 0; y < SIZE; ++y)
                data[x + y * SIZE] = (int) (Math.floor(Math.random() * Integer.MAX_VALUE));

        return Bitmap.createBitmap(data, SIZE, SIZE, Bitmap.Config.ARGB_8888);
    }

    @Override
    public String getDescription() {
        return "Test + " + Integer.toString((int) (Math.random() * 10000));
    }
}

class RoomDBTester {

    Application application;

    public RoomDBTester(Application application) {
        this.application = application;
    }

    TestData data = new TestData();

    private Sketch getRandomSketch() {
        Bitmap bmp = data.getBitmap();
        String description = data.getDescription();

        Sketch s = new Sketch(bmp, description);
        return s;
    }

    public void saveRandomSketch() {
        Sketch randomSketch = this.getRandomSketch();

        RoomHandler rh = new RoomHandler(this.application);
        Log.i("ROOM TEST", "Inserting random sketch into db.. (Description = " + randomSketch.description + " )");
        rh.insertSketch(randomSketch);
    }

    public void saveSketch(String description) {
        Sketch randomSketch = this.getRandomSketch();
        Sketch roomTestSketch = randomSketch;
        roomTestSketch.setDescription("Room IS WORKING!");

        RoomHandler rh = new RoomHandler(this.application);


        Log.i("ROOM TEST", "Inserting non-random test sketch into db.. (Description = " + description + ")");
        rh.insertSketch(roomTestSketch);
    }

    public Sketch[] getAllSketches() {
        RoomHandler rh = new RoomHandler(this.application);


        Log.i("ROOM TEST", "Loading all sketches from db..");
        Sketch[] sketches = rh.getAllSketches();

        for (Sketch sketch : sketches) {
            Log.i("ROOM TEST", "DB Entry || ID = " + sketch.getId() + ", Description = " + sketch.description);
        }


        return sketches;
    }
}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.SketchViewHolder> {
    private SketchData[] dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SketchViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;

        public SketchViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(SketchData[] myDataset) {
        dataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SketchViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gallery_entry, parent, false);

        v.setOnLongClickListener((GalleryActivity) parent.getContext());
        SketchViewHolder vh = new SketchViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SketchViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardView.setTag(dataset[position]);
        ((TextView) holder.cardView.getChildAt(1)).setText(dataset[position].getDescription());
        ((ImageView) holder.cardView.getChildAt(0)).setImageBitmap(dataset[position].getBitmap());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.length;
    }
}

public class GalleryActivity extends BaseActivity implements View.OnLongClickListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recycler_view);

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        Sketch[] arr = getRoomHandler().getAllSketches();
        adapter = new MyAdapter(arr);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_gallery;
    }

    @Override
    public boolean onLongClick(View v) { deleteSketch(v); return true; }

    public void deleteSketch(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_delete_message)
                .setMessage(((SketchData) view.getTag()).getDescription());

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            int sketchId = ((SketchData) view.getTag()).getId();

            public void onClick(DialogInterface dialog, int id) {
                getRoomHandler().deleteSketch(sketchId);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void editSketch(View view) {
        Intent intent = new Intent(this, SketchActivity.class);
        SketchData data = (SketchData) view.getTag();
        intent.putExtra("sketchId", data.getId());
        startActivity(intent);
    }

    public void addSketch(View view) {
        Intent intent = new Intent(this, SketchActivity.class);
        startActivity(intent);
    }
}
