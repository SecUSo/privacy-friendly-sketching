package org.secuso.privacyfriendlysketches.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlysketches.R;
import org.secuso.privacyfriendlysketches.activities.helper.BaseActivity;
import org.secuso.privacyfriendlysketches.database.SketchData;

class TestData implements SketchData {

    @Override
    public Bitmap getSketch() {
        final int SIZE = 64;
        int[] data = new int[SIZE * SIZE];
        for (int x = 0; x < SIZE; ++x)
            for (int y = 0; y < SIZE; ++y)
                data[x + y * SIZE] = (int)(Math.floor(Math.random() * Integer.MAX_VALUE));
        return Bitmap.createBitmap(data, SIZE, SIZE, Bitmap.Config.ALPHA_8);
    }

    @Override
    public String getDescription() {
        return "Test + " + Integer.toString((int)(Math.random() * 10000));
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
        ((TextView)holder.cardView.getChildAt(0)).setText(dataset[position].getDescription());
        ((ImageView)holder.cardView.getChildAt(1)).setImageBitmap(dataset[position].getSketch());
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

        recyclerView = findViewById(R.id.recycler_view);

        // use a linear layout manager
        //@todo GridLayoutManager
        layoutManager = new LinearLayoutManager(this);
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
