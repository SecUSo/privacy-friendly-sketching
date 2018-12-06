package org.secuso.privacyfriendlysketches.database;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by enyone on 12/6/18.
 */

public class RoomHandler {

    private SketchDAO sketchDAO;

    public RoomHandler(Application application) {
        SketchingRoomDB db = SketchingRoomDB.getDatabase(application);
        this.sketchDAO = db.sketchDao();
    }

    public void insertSketch(Sketch... sketches) {

        InsertAsyncTask iat = new InsertAsyncTask(this.sketchDAO);
        iat.execute(sketches);
    }

    //TODO
    public Sketch[] getAllSketches() {

        return this.sketchDAO.getAllSketches();
    }


    private static class InsertAsyncTask extends AsyncTask<Sketch, Long, Integer> {

        private SketchDAO sketchDAO;

        public InsertAsyncTask(SketchDAO sketchDAO) {
            this.sketchDAO = sketchDAO;
        }

        @Override
        protected Integer doInBackground(Sketch... sketches) {
            this.sketchDAO.insertSketch(sketches);
            return null;
        }
    }
//
//    private static class GetAllAsyncTask extends AsyncTask<Sketch, Long, Sketch[]> {
//
//        private SketchDAO sketchDAO;
//
//        public GetAllAsyncTask(SketchDAO sketchDAO) {
//            this.sketchDAO = sketchDAO;
//        }
//
//        @Override
//        protected Sketch[] doInBackground(Sketch... sketches) {
//            Sketch[] allSketches = this.sketchDAO.getAllSketches();
//            return allSketches;
//        }
//    }

}
