package com.hsj.common.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author:hsj
 * @Date:2020-06-28
 * @Class:CommonActivity
 * @Desc:
 */
public abstract class CommonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI(savedInstanceState);

        initData();
    }

    protected abstract void initUI(Bundle savedInstanceState);

    protected abstract void initData();

}
