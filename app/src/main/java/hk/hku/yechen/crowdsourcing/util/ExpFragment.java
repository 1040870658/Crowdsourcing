package hk.hku.yechen.crowdsourcing.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.hku.yechen.crowdsourcing.R;

/**
 * Created by yechen on 2018/1/5.
 */

public class ExpFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LevelLog.log(LevelLog.ERROR,"Fragment Life Cycle","onCreateView");
        return inflater.inflate(R.layout.tablayout_m1,null);
    }

    @Override
    public void onPause() {
        LevelLog.log(LevelLog.ERROR,"Fragment Life Cycle","onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        LevelLog.log(LevelLog.ERROR,"Fragment Life Cycle","onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        LevelLog.log(LevelLog.ERROR,"Fragment Life Cycle","onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        LevelLog.log(LevelLog.ERROR,"Fragment Life Cycle","onResume");
        super.onResume();
    }

    @Override
    public void onStart() {
        LevelLog.log(LevelLog.ERROR,"Fragment Life Cycle","onStart");
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        LevelLog.log(LevelLog.ERROR,"Fragment Life Cycle","onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        LevelLog.log(LevelLog.ERROR,"Fragment Life Cycle","onDetach");
        super.onDetach();
    }
}
