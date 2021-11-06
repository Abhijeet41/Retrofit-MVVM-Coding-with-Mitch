package com.abhi41.restapimvvmretrofit.util;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.abhi41.restapimvvmretrofit.R;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

public abstract class BaseActivity extends AppCompatActivity {
    ArcConfiguration configuration;
    SimpleArcDialog mDialog;

    @Override
    public void setContentView(int layoutResID) {
        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater()
                .inflate(R.layout.activity_base, null);

        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        configuration = new ArcConfiguration(this);
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
        configuration.setText("Loading ...");

        mDialog = new SimpleArcDialog(this);
        mDialog.setConfiguration(configuration);
        super.setContentView(layoutResID);
    }

    public void showProgressBar(boolean visibility, Context context) {
        if (visibility) {
            mDialog.show();
        } else {
            mDialog.dismiss();
        }
    }
}
