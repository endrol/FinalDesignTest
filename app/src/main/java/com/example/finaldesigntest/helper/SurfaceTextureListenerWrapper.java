package com.example.finaldesigntest.helper;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

public abstract class SurfaceTextureListenerWrapper implements TextureView.SurfaceTextureListener {


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
