package org.secuso.privacyfriendlysketches.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlysketches.R;
import org.secuso.privacyfriendlysketches.activities.helper.BaseActivity;
import org.secuso.privacyfriendlysketches.database.Sketch;
import org.secuso.privacyfriendlysketches.database.SketchDAO;
import org.secuso.privacyfriendlysketches.database.SketchData;
import org.secuso.privacyfriendlysketches.database.SketchingRoomDB;

class TestData implements SketchData {

    @Override
    public Bitmap getSketch() {
        final int SIZE = 64;
        int[] data = new int[SIZE * SIZE];
        for (int x = 0; x < SIZE; ++x)
            for (int y = 0; y < SIZE; ++y)
                data[x + y * SIZE] = (int) (Math.floor(Math.random() * Integer.MAX_VALUE));


        return Bitmap.createBitmap(data, SIZE, SIZE, Bitmap.Config.ALPHA_8);
    }

    @Override
    public String getDescription() {
        return "Test + " + Integer.toString((int) (Math.random() * 10000));
    }
}

class RoomDBTester {

    Context context;

    public RoomDBTester(Context context) {
        this.context = context;
    }

    TestData data = new TestData();

    private Sketch getRandomSketch() {
        Bitmap bmp = data.getSketch();
        String description = data.getDescription();

        Sketch s = new Sketch(bmp, description);
        return s;
    }

    public void saveRandomSketch() {
        Sketch randomSketch = this.getRandomSketch();

        SketchingRoomDB db = SketchingRoomDB.getDatabase(this.context);
        SketchDAO sketchDAO = db.sketchDao();
        Log.i("ROOM TEST", "Inserting random sketch into db.. (Description = " + randomSketch.description + " )");
        sketchDAO.insertSketch(randomSketch);

    }

    public void saveSketch(String description) {
        Sketch randomSketch = this.getRandomSketch();
        Sketch roomTestSketch = new Sketch(randomSketch.getBitmap(), description);

        SketchingRoomDB db = SketchingRoomDB.getDatabase(this.context);
        SketchDAO sketchDAO = db.sketchDao();

        Log.i("ROOM TEST", "Inserting non-random test sketch into db.. (Description = " + description + ")");
        sketchDAO.insertSketch(roomTestSketch);
    }

    public Sketch[] getAllSketches() {
        SketchingRoomDB db = SketchingRoomDB.getDatabase(this.context);
        SketchDAO sketchDAO = db.sketchDao();

        int sketchCount = sketchDAO.getSketchCount();
        Log.i("ROOM TEST", sketchCount + "sketches in DB");

        Log.i("ROOM TEST", "Loading all sketches from db..");
        Sketch[] sketches = sketchDAO.getAllSketches();

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

        SketchViewHolder vh = new SketchViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SketchViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.cardView.getChildAt(0)).setText(dataset[position].getDescription());
        ((ImageView) holder.cardView.getChildAt(1)).setImageBitmap(dataset[position].getSketch());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.length;
    }
}

public class GalleryActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RoomDBTester tester = new RoomDBTester(getApplication());
        tester.saveRandomSketch();
        tester.saveSketch("non random description ROOM works!");
        tester.getAllSketches();

        recyclerView = findViewById(R.id.recycler_view);

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        SketchData[] arr = new SketchData[50];
        for (int i = 0; i < 50; ++i)
            arr[i] = new TestData();
        adapter = new MyAdapter(arr);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_gallery;
    }
}
