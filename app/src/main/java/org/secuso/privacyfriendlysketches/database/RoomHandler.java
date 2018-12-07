package org.secuso.privacyfriendlysketches.database;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import java.net.URL;
import java.util.concurrent.ExecutionException;

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
        try {
            iat.execute(sketches).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO
    public Sketch[] getAllSketches() {

        GetAllAsyncTask gaat = new GetAllAsyncTask(sketchDAO);
        try {
            return gaat.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Sketch getSketch(int id) {

        GetAsyncTask gat = new GetAsyncTask(sketchDAO);
        try {
            return gat.execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteSketch(int id) {
        DeleteAsyncTask dat = new DeleteAsyncTask(sketchDAO);
        try {
            dat.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void updateSketch(Sketch sketch) {

        UpdateAsyncTask updateAsyncTask = new UpdateAsyncTask(sketchDAO);
        try {
            updateAsyncTask.execute(sketch).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private static class GetAllAsyncTask extends AsyncTask<Sketch, Long, Sketch[]> {

        private SketchDAO sketchDAO;

        public GetAllAsyncTask(SketchDAO sketchDAO) {
            this.sketchDAO = sketchDAO;
        }

        @Override
        protected Sketch[] doInBackground(Sketch... sketches) {
            Sketch[] allSketches = this.sketchDAO.getAllSketches();
            return allSketches;
        }
    }

    private static class GetAsyncTask extends AsyncTask<Integer, Long, Sketch> {

        private SketchDAO sketchDAO;

        public GetAsyncTask(SketchDAO sketchDAO) {
            this.sketchDAO = sketchDAO;
        }


        @Override
        protected Sketch doInBackground(Integer... integers) {
            int id = integers[0];
            return this.sketchDAO.getSketchById(id);
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Integer, Long, Sketch> {

        private SketchDAO sketchDAO;

        public DeleteAsyncTask(SketchDAO sketchDAO) {
            this.sketchDAO = sketchDAO;
        }


        @Override
        protected Sketch doInBackground(Integer... integers) {
            int id = integers[0];
            this.sketchDAO.deleteSketch(id);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Sketch, Long, Sketch> {

        private SketchDAO sketchDAO;

        public UpdateAsyncTask(SketchDAO sketchDAO) {
            this.sketchDAO = sketchDAO;
        }


        @Override
        protected Sketch doInBackground(Sketch... sketches) {
            this.sketchDAO.updateSketch(sketches);
            return null;
        }
    }

}
