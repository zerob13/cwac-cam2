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

/**
 * Activity for recording video. Analogue of CameraActivity.
 * Supports the same protocol, in terms of extras and return data,\
 * as does ACTION_VIDEO_CAPTURE.
 */
public class VideoRecorderActivity extends AbstractCameraActivity {
  @Override
  boolean needsOverlay() {
    return(true);
  }

  @Override
  boolean isVideo() {
    return(true);
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
  }
}
