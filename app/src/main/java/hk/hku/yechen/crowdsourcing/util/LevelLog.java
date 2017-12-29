package hk.hku.yechen.crowdsourcing.util;

import android.util.Log;

/**
 * Created by yechen on 2017/11/20.
 */

public class LevelLog {
    public static final int VERBOSE=1;
    public static final int DEBUG=2;
    public static final int INFO=3;
    public static final int WARN=4;
    public static final int ERROR=5;
    public static void log(int level,String key,String value){
        if(level >= ERROR) {
            Log.e(key, value);
            return;
        }
        if(level >= WARN) {
            Log.w(key, value);
            return;
        }
        if(level >= INFO){
            Log.i(key,value);
            return;
        }
        if(level >= DEBUG){
            Log.d(key,value);
            return;
        }
        if(level >= VERBOSE){
            Log.v(key,value);
            return;
        }
    }
}
