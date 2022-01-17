package com.hsj.common.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @Author:hsj
 * @Date:2020-06-28
 * @Class:CommonFragment
 * @Desc:
 */
public abstract class CommonFragment extends Fragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            initUI(savedInstanceState);
            initData();
        }
        return rootView;
    }

    protected abstract int getLayoutId();

    protected abstract void initUI(Bundle savedInstanceState);

    protected abstract void initData();

    protected <V extends View> V findView(@IdRes int id) {
        return  rootView.findViewById(id);
    }

}
