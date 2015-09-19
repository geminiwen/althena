package cn.geminiwen.component.althena;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executor;

import cn.geminiwen.component.althena.okhttp.OkHttpInstance;
import cn.geminiwen.component.althena.task.Task;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by geminiwen on 8/12/15.
 */
public class TaskManager {
    private Executor mExecutorService = AsyncTask.THREAD_POOL_EXECUTOR;
    private OkHttpClient mOkHttpClient = OkHttpInstance.getHttpClient();

    private final int READ_BYTES = 4096;

    private List<WrapperTask> mRunningTask;
    private List<WrapperTask> mPausedTask;

    public TaskManager() {
        mRunningTask = new Vector<>();
        mPausedTask = new Vector<>();
    }

    public void submit(Task task) {
        DownloadRunnable downloadRunnable = new DownloadRunnable(task);
        mExecutorService.execute(downloadRunnable);
    }

    public void pause(Task task) {
        WrapperTask wrapperTask = new WrapperTask(task);
        int index = mRunningTask.indexOf(wrapperTask);
        if (index != -1) {
            wrapperTask = mRunningTask.get(index);
            wrapperTask.getTask().setPaused(true);
        }
    }

    public void stop(Task task) {
        WrapperTask wrapperTask = new WrapperTask(task);
        int index = mRunningTask.indexOf(wrapperTask);
        boolean handled = false;
        if (index != -1) {
            WrapperTask innerTask = mRunningTask.get(index);
            innerTask.getTask().setCanceled(true);
            handled = true;
        }

        index = mPausedTask.indexOf(wrapperTask);
        if (index != -1) {
            WrapperTask innerTask = mPausedTask.get(index);
            Task realTask = innerTask.getTask();
            mPausedTask.remove(innerTask);
            realTask.getDst().delete();

            // notify listener
            Althena.OnDownloadStateUpdateListener listener = realTask.getOnStateUpdateListener();
            if(listener != null) {
                listener.onStop(realTask);
            }
            handled = true;
        }

        // when this task is not in this task manager
        if (!handled) {
            Althena.OnDownloadStateUpdateListener listener = task.getOnStateUpdateListener();
            if(listener != null) {
                listener.onStop(task);
            }
        }
    }

    private class DownloadRunnable implements Runnable {
        WrapperTask wrapperTask;

        public DownloadRunnable(Task task) {
            this.wrapperTask = new WrapperTask(task);
            int index = mPausedTask.indexOf(this.wrapperTask);
            if (index != -1) {
                this.wrapperTask = mPausedTask.get(index);
            }
        }

        @Override
        public void run()  {
            if(mRunningTask.contains(this.wrapperTask)) {
                return;
            }
            Task task = this.wrapperTask.getTask();
            mRunningTask.add(this.wrapperTask);
            mPausedTask.remove(this.wrapperTask);
            String url = task.getUrl();
            long bytesOffset = this.wrapperTask.byteHasRead;

            Request.Builder builder = new Request.Builder();
            builder.url(url)
                    .get();

            if (bytesOffset > 0l) {
                builder.header("Range", "bytes=" + bytesOffset + "-");
            }
            Request req = builder.build();

            Althena.OnDownloadStateUpdateListener l = task.getOnStateUpdateListener();

            if (task.isPaused()) {
                mRunningTask.remove(this.wrapperTask);
                mPausedTask.add(this.wrapperTask);

                if (l != null) {
                    l.onPause(task);
                }
                return;
            }

            if (task.isCanceled()) {
                mRunningTask.remove(this.wrapperTask);

                if (l != null) {
                    l.onStop(task);
                }
                return;
            }


            if (l != null) {
                l.onStart(task);

                if (bytesOffset > 0l) {
                    l.onRestart(task);
                }
            }

            try {
                Response response = mOkHttpClient.newCall(req).execute();
                boolean isAppend = this.wrapperTask.byteHasRead > 0l;
                /**
                 * buffer sink
                 */
                byte[] sink = new byte[READ_BYTES];

                ResponseBody body = response.body();

                /**
                 * Get content length
                 */
                long contentLength = body.contentLength();
                this.wrapperTask.setContentLength(contentLength);
                if (l != null) {
                    l.onInfo(task, contentLength);
                }

                /**
                 * Get InputStream and initialized okio {@link BufferedSource}
                 */
                InputStream in = body.byteStream();
                BufferedSource source = Okio.buffer(Okio.source(in));

                /**
                 * Initialize File OutputStream
                 */
                File dstFile = task.getDst();
                if (!dstFile.exists()) {
                    dstFile.createNewFile();
                }
                BufferedSink fileSink = Okio.buffer(isAppend ? Okio.appendingSink(dstFile) : Okio.sink(dstFile));

                while( !source.exhausted() ) {
                    int readBytes = source.read(sink);
                    fileSink.write(sink, 0, readBytes);
                    this.wrapperTask.appendLength(readBytes);

                    if (l != null) {
                        l.onProgressUpdate(task,
                                this.wrapperTask.getByteHasRead(),
                                           this.wrapperTask.getContentLength());
                    }

                    if (task.isPaused()) {
                        /**
                         * if task paused, add task to paused task queue
                         */
                        source.close();
                        mPausedTask.add(this.wrapperTask);
                        if (l != null) {
                            l.onPause(task);
                        }
                        break;
                    }
                }

                fileSink.emit();
                /**
                 * if this task is complete successfully
                 */
                if (l != null && !task.isPaused() && !task.isCanceled()) {
                    l.onComplete(task);
                }

                if (task.isCanceled()) {
                    dstFile.delete();

                    if (l != null) {
                        l.onStop(task);
                    }
                }

            } catch (IOException e) {
                /**
                 * Handle IOException
                 */
                if (l != null) {
                    l.onError(task, e);
                }

            } finally {
                mRunningTask.remove(this.wrapperTask);
            }
        }
    }


    /**
     * Wrapper for {@link Task}
     */
    private class WrapperTask {
        private Task task;
        private long contentLength;
        private long byteHasRead;

        public WrapperTask(Task task) {
            this.task = task;
            this.byteHasRead = task.getByteOffset();
        }

        public Task getTask() {
            return task;
        }

        public void appendLength(long byteHasRead) {
            this.byteHasRead += byteHasRead;
            this.task.setByteOffset(byteHasRead);
        }

        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }

        public long getContentLength() {
            return contentLength;
        }

        public long getByteHasRead() {
            return byteHasRead;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof WrapperTask)) {
                return false;
            }
            WrapperTask other = (WrapperTask)o;
            return this.task.equals(other.task);
        }
    }
}
