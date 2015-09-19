package cn.geminiwen.component.althena;

import java.io.IOException;

import cn.geminiwen.component.althena.task.Task;

/**
 * Created by geminiwen on 8/12/15.
 */
public class Althena {
    private static TaskManager sTaskManager = new TaskManager();

    public static void start(Task task) {
        sTaskManager.submit(task);
    }

    public static void pause(Task task) {
        sTaskManager.pause(task);
    }

    public static void stop(Task task) {
        sTaskManager.stop(task);
    }


    public interface OnDownloadStateUpdateListener {
        /**
         * Called when task Start
         * @param task
         */
        void onStart(Task task);

        /**
         * Called when task get contentLength
         *
         * @param task
         * @param contentLength
         */
        void onInfo(Task task, long contentLength);

        /**
         * Called when task is paused
         * @param task
         */
        void onPause(Task task);

        /**
         * Called when task is stop
         * @param task
         */
        void onStop(Task task);

        /**
         * Called when task is restart, after {@link #onStart} has been called
         * @param task
         */
        void onRestart(Task task);

        /**
         * Called when task download progress update
         *
         * @param task the task is downloading
         * @param downloadedBytes bytes we downloaded
         * @param contentLength content length of this task
         */
        void onProgressUpdate(Task task, long downloadedBytes, long contentLength);

        /**
         * Called when an error occured
         * @param task
         * @param e
         */
        void onError(Task task, IOException e);

        /**
         * Called when task is completed
         * @param task
         */
        void onComplete(Task task);
    }

    public static class DefaultStateUpdateListener implements OnDownloadStateUpdateListener {
        @Override
        public void onStart(Task task) {

        }

        @Override
        public void onInfo(Task task, long contentLength) {

        }

        @Override
        public void onPause(Task task) {

        }

        @Override
        public void onStop(Task task) {

        }

        @Override
        public void onRestart(Task task) {

        }

        @Override
        public void onProgressUpdate(Task task, long downloadedBytes, long contentLength) {

        }

        @Override
        public void onError(Task task, IOException e) {

        }

        @Override
        public void onComplete(Task task) {

        }
    }
}
