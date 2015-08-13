package cn.geminiwen.component.althena.task;

import java.io.File;
import java.lang.ref.WeakReference;

import cn.geminiwen.component.althena.Althena;

/**
 * Created by geminiwen on 15/8/12.
 */
public class Task {
    private String url;
    private File dst;
    private long id;
    private Object tag;
    private boolean paused;
    private boolean canceled;
    private long byteOffset;
    private WeakReference<Althena.OnDownloadStateUpdateListener> stateUpdateListener;

    public Task(String url, File dst, long id) {
        this.url = url;
        this.dst = dst;
        this.id = id;
    }

    public Task(String url, String dst, long id) {
        this(url, new File(dst), id);
    }

    public Task(String url, File dst) {
        this(url, dst, System.currentTimeMillis());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getDst() {
        return dst;
    }

    public void setDst(File dst) {
        this.dst = dst;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public long getByteOffset() {
        return byteOffset;
    }

    public void setByteOffset(long byteOffset) {
        this.byteOffset = byteOffset;
    }

    public void setOnStateUpdateListener(Althena.OnDownloadStateUpdateListener l) {
        this.stateUpdateListener = new WeakReference<>(l);
    }

    public Althena.OnDownloadStateUpdateListener getOnStateUpdateListener() {
        return this.stateUpdateListener == null ? null : this.stateUpdateListener.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task other = (Task)o;
        return this.id == other.id;
    }
}
