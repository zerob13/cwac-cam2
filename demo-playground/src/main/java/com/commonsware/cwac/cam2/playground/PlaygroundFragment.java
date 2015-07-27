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

package com.commonsware.cwac.cam2.playground;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.commonsware.cwac.cam2.AbstractCameraActivity;
import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.CameraSelectionCriteria;
import com.commonsware.cwac.cam2.VideoRecorderActivity;
import java.io.File;

public class PlaygroundFragment extends PreferenceFragment {
  interface Contract {
    void takePicture(Intent i);
    void takeVideo(Intent i);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.prefs);
    setHasOptionsMenu(true);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (!(activity instanceof Contract)) {
      throw new IllegalStateException("Hosting activity does not implement Contract interface!");
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.take_picture) {
      takePicture();

      return(true);
    }

    return super.onOptionsItemSelected(item);
  }

  private void takePicture() {
    SharedPreferences prefs=getPreferenceManager().getSharedPreferences();
    AbstractCameraActivity.IntentBuilder b;
    boolean isVideo=prefs.getBoolean("video", false);

    if (isVideo) {
      b=buildVideoIntent(prefs);
    }
    else {
      b=buildPictureIntent(prefs);
    }

    if (prefs.getBoolean("ffc", false)) {
      b.facing(CameraSelectionCriteria.Facing.FRONT);
    }
    else {
      b.facing(CameraSelectionCriteria.Facing.BACK);
    }

    if (prefs.getBoolean("debug", false)) {
      b.debug();
    }

    if (prefs.getBoolean("file", false)) {
      b.to(new File(getActivity().getExternalFilesDir(null), "test.jpg"));
    }

    if (prefs.getBoolean("updateMediaStore", false)) {
      b.updateMediaStore();
    }

    if (prefs.getBoolean("forceClassic", false)) {
      b.forceClassic();
    }

    if (isVideo) {
      ((Contract)getActivity()).takeVideo(b.build());
    }
    else {
      ((Contract)getActivity()).takePicture(b.build());
    }
  }

  private AbstractCameraActivity.IntentBuilder buildPictureIntent(SharedPreferences prefs) {
    CameraActivity.IntentBuilder b=new CameraActivity.IntentBuilder(getActivity());

    if (!prefs.getBoolean("confirm", false)) {
      b.skipConfirm();
    }

    return(b);
  }

  private AbstractCameraActivity.IntentBuilder buildVideoIntent(SharedPreferences prefs) {
    VideoRecorderActivity.IntentBuilder b=new VideoRecorderActivity.IntentBuilder(getActivity());

    return(b);
  }
}
