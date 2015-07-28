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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Activity for recording video. Analogue of CameraActivity.
 * Supports the same protocol, in terms of extras and return data,\
 * as does ACTION_VIDEO_CAPTURE.
 */
public class VideoRecorderActivity extends AbstractCameraActivity {
  @Override
  boolean needsOverlay() {
    return(false);
  }

  @Override
  boolean needsActionBar() {
    return(false);
  }

  @Override
  boolean isVideo() {
    return(true);
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(CameraEngine.VideoTakenEvent event) {
    if (event.getVideoTransaction()==null) {
      setResult(RESULT_CANCELED);
      finish();
      // TODO: something with the exception
    }
    else {
      findViewById(android.R.id.content).post(new Runnable() {
        @Override
        public void run() {
         setResult(RESULT_OK, new Intent().setData(getOutputUri()));
         finish();
        }
      });
    }
  }

  /**
   * Class to build an Intent used to start the VideoRecorderActivity.
   * Call setComponent() on the Intent if you are using your
   * own subclass of VideoRecorderActivity.
   */
  public static class IntentBuilder extends AbstractCameraActivity.IntentBuilder {
    /**
     * Standard constructor. May throw a runtime exception
     * if the environment is not set up properly (see
     * validateEnvironment() on Utils).
     *
     * @param ctxt any Context will do
     */
    public IntentBuilder(Context ctxt) {
      super(ctxt, VideoRecorderActivity.class);
    }

    @Override
    public AbstractCameraActivity.IntentBuilder to(Uri output) {
      if (!"file".equals(output.getScheme())) {
        throw new IllegalArgumentException("must be a file:/// Uri");
      }

      return(super.to(output));
    }
  }
}
