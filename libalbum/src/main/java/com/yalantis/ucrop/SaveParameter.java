package com.yalantis.ucrop;

import android.graphics.RectF;

import java.io.Serializable;

import imagepicker.view.ClipMatrix;

public class SaveParameter implements Serializable {
    public RectF overLayerRectf;
    public ClipMatrix imageMatrix;

    public SaveParameter(RectF overLayerRectf, ClipMatrix imageMatrix) {
        this.overLayerRectf = overLayerRectf;
        this.imageMatrix = imageMatrix;
    }
}