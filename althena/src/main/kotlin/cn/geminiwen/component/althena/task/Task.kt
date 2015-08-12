package cn.geminiwen.component.althena.task

import cn.geminiwen.component.althena.Althena
import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by geminiwen on 8/12/15.
 */
public data class Task(val url:String, val dst:File, val id:Long = System.currentTimeMillis()) {

    constructor(url:String, dst: String, id:Long = System.currentTimeMillis()): this(url, File(dst), id){
    }

    private var stateUpdateListener: WeakReference<Althena.OnDownloadStateUpdateListener>? = null;

    public var tag:Any? = null;
    public var paused:Boolean = false;
    public var canceled:Boolean = false;
    public var bytesOffset:Long = 0;

    fun setStateUpdateListener(l:Althena.OnDownloadStateUpdateListener) {
        this.stateUpdateListener = WeakReference<Althena.OnDownloadStateUpdateListener>(l);
    }

    fun getStateUpdateListener():Althena.OnDownloadStateUpdateListener? {
        return this.stateUpdateListener?.get();
    }


    override fun equals(other: Any?): Boolean {
        if (other == this) {
            return true;
        }
        if (other !is Task) {
            return false;
        }
        return other.id == this.id;
    }
}