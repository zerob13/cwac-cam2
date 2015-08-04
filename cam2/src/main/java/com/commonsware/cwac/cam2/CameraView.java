/***
 Copyright (c) 2015 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.commonsware.cwac.cam2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import com.commonsware.cwac.cam2.util.Size;

/**
 * Class responsible for rendering preview frames on the UI
 * as a View for the user to see and interact with. Also handles
 * maintaining aspect ratios and dealing with full-bleed previews.
 */
public class CameraView extends TextureView implements TextureView.SurfaceTextureListener {
  interface StateCallback {
    void onReady(CameraView cv);
    void onDestroyed(CameraView cv);
  }

  /**
   * The requested size of the preview frames, or null to just
   * use the size of the view itself
   */
  private Size previewSize;

  private StateCallback stateCallback;

  /**
   * Constructor, used for creating instances from Java code.
   *
   * @param context the Activity that will host this View
   */
  public CameraView(Context context) {
    super(context, null);
    initListener();
  }

  /**
   * Constructor, used by layout inflation.
   *
   * @param context the Activity that will host this View
   * @param attrs the parsed attributes from the layout resource tag
   */
  public CameraView(Context context, AttributeSet attrs) {
    super(context, attrs, 0);
    initListener();
  }

  /**
   * Constructor, used by... something.
   *
   * @param context the Activity that will host this View
   * @param attrs the parsed attributes from the layout resource tag
   * @param defStyle "An attribute in the current theme that
   *                 contains a reference to a style resource
   *                 that supplies default values for the view.
   *                 Can be 0 to not look for defaults."
   */
  public CameraView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initListener();
  }

  /**
   * @return the requested preview size
   */
  public Size getPreviewSize() {
    return(previewSize);
  }

  /**
   * @param previewSize the requested preview size
   */
  public void setPreviewSize(Size previewSize) {
    this.previewSize=previewSize;

    enterTheMatrix();
  }

  public void setStateCallback(StateCallback cb) {
    stateCallback=cb;
  }

  private void initListener() {
    setSurfaceTextureListener(this);
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
    if (stateCallback!=null) {
      stateCallback.onReady(this);
    }
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    enterTheMatrix();
  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
    if (stateCallback!=null) {
      stateCallback.onDestroyed(this);
    }

    return(false);
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

  }

  private void enterTheMatrix() {
    if (previewSize!=null) {
      adjustAspectRatio(previewSize.getWidth(),
          previewSize.getHeight(),
          ((Activity)getContext()).getWindowManager().getDefaultDisplay().getRotation());
    }
  }

  // inspired by https://github.com/google/grafika/blob/master/src/com/android/grafika/PlayMovieActivity.java

  private void adjustAspectRatio(int videoWidth, int videoHeight,
                                 int rotation) {
    int viewWidth = getWidth();
    int viewHeight = getHeight();

    // if (rotation==Surface.ROTATION_90 || rotation==Surface.ROTATION_270) {
      int temp=videoWidth;
      videoWidth=videoHeight;
      videoHeight=temp;
    // }

    // why do we need to swap width and height for all rotations?
    // damfino. but, it seems to work.

    double aspectRatio=(double)videoHeight/videoWidth;

    int newWidth, newHeight;

    if (viewHeight>(int)(viewWidth*aspectRatio)) {
      newWidth=(int)(viewHeight/aspectRatio);
      newHeight=viewHeight;
    }
    else {
      newWidth=viewWidth;
      newHeight=(int)(viewWidth*aspectRatio);
    }

    int xoff=(viewWidth-newWidth)/2;
    int yoff=(viewHeight-newHeight)/2;

    Matrix txform=new Matrix();

    getTransform(txform);

    int degrees=0;

    switch(rotation) {
      case Surface.ROTATION_90:
        degrees=270;
        break;

      case Surface.ROTATION_270:
        degrees=90;
        break;
    }

    txform.setScale((float)newWidth/viewWidth, (float)newHeight/viewHeight);
    txform.postRotate(degrees, newWidth/2, newHeight/2);
    txform.postTranslate(xoff, yoff);
    setTransform(txform);
  }
}

/*

Notes about all of this TextureView matrix crap, to help me
remember in the future.

postTranslate() is setting the X/Y coordinates of the
upper-left corner of the frames drawn within the TextureView.

postRotate(), with one parameter, rotates how the frames are
drawn within the TextureView around the upper-left corner
of where the frames are drawn. Hence, most rotations should
probably be using the three-parameter form, that specify
the center around which to rotate.

 */