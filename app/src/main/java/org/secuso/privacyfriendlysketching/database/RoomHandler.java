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
package org.secuso.privacyfriendlysketching.database;

import android.app.Application;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

/**
 * This class handles ROOM queries to the database.
 */

public class RoomHandler {
    private static RoomHandler instance;
    private SketchDAO sketchDAO;

    private RoomHandler(Application application) {
        SketchingRoomDB db = SketchingRoomDB.getDatabase(application);
        this.sketchDAO = db.sketchDao();
    }

    public static RoomHandler getInstance(Application application) {
        if (instance == null)
            instance = new RoomHandler(application);
        return instance;
    }

    public int insertSketch(Sketch... sketches) {

        InsertAsyncTask iat = new InsertAsyncTask(this.sketchDAO);
        try {
            return iat.execute(sketches).get().intValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return -1;
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

    public int getSketchCount() {
        return sketchDAO.getSketchCount();
    }

    public int[] getSketchIds() {
        return sketchDAO.getSketchIds();
    }

    public Sketch getSketchSync(int id) {
        return this.sketchDAO.getSketchById(id);
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
            dat.execute(id).get();
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


    private static class InsertAsyncTask extends AsyncTask<Sketch, Long, Long> {

        private SketchDAO sketchDAO;

        public InsertAsyncTask(SketchDAO sketchDAO) {
            this.sketchDAO = sketchDAO;
        }

        @Override
        protected Long doInBackground(Sketch... sketches) {
            return this.sketchDAO.insertSketch(sketches[0]);
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
