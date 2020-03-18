/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.chromakeyvideo;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import static android.net.Uri.parse;

/**
 * This is an example activity that shows how to display a video with chroma key filtering in
 * Sceneform.
 */
public class ChromaKeyVideoActivity extends AppCompatActivity implements Scene.OnUpdateListener{

    private static final String TAG = ChromaKeyVideoActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private Scene scene;

    @Nullable private ModelRenderable videoRenderable,videoRenderable1;
    //@Nullable private  ModelRenderable modelRenderable;


    private boolean isImageDetected = false;
    private MediaPlayer mediaPlayer,mediaplayer1;

    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);

    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.2f;


    ExternalTexture mytexture,mytexture1;
    ExternalTexture handtexture;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_video);
        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // Create an ExternalTexture for displaying the contents of the video.
        ExternalTexture texture = new ExternalTexture();
        ExternalTexture texture1 = new ExternalTexture();
        mytexture = texture;
        mytexture1 = texture1;

        // Create an Android MediaPlayer to capture the video on the external texture's surface.
        mediaPlayer = MediaPlayer.create(this, R.raw.short_clip);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(false);

        mediaplayer1 = MediaPlayer.create(this,R.raw.lion_chroma);
        mediaplayer1.setSurface(texture1.getSurface());
        mediaplayer1.setLooping(false);

        // Create a renderable with a material that has a parameter of type 'samplerExternal' so that
        // it can display an ExternalTexture. The material also has an implementation of a chroma key
        // filter.
        ModelRenderable.builder()
                .setSource(this, R.raw.chroma_key_video)
                .build()
                .thenAccept(
                        renderable -> {
                            videoRenderable = renderable;
                            renderable.getMaterial().setExternalTexture("videoTexture", texture);
                            renderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
                        })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load video renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.chroma_key_video)
                .build()
                .thenAccept(
                        renderable -> {
                            videoRenderable1 = renderable;
                            renderable.getMaterial().setExternalTexture("videoTexture", texture1);
                            renderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
                        })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load video renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        scene = arFragment.getArSceneView().getScene();

        scene.addOnUpdateListener(this::onUpdate);

    }

    public void onUpdate(FrameTime frameTime) {

        if (isImageDetected)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        //PointCloud currentCloud = frame.acquirePointCloud();
        //currentCloud.release();

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage image : augmentedImages) {

            if (image.getTrackingState() == TrackingState.TRACKING) {

                if (image.getName().equals("image")) {

                    isImageDetected = true;

                    displayHand(image.createAnchor(image.getCenterPose()),image);
                    break;
                }
            }
        }
    }


    private void displayHand(Anchor anchor,AugmentedImage image) {


        Pose offset1 = anchor.getPose().compose(Pose.makeTranslation(0.1f,0f,0f));

        Anchor anchor1 = image.createAnchor(offset1);


        ModelRenderable.builder().setSource(this, Uri.parse("hand.sfb")).build().thenAccept(modelRenderable1 -> placeModel1(modelRenderable1,anchor1));
        ModelRenderable.builder().setSource(this, Uri.parse("hand.sfb")).build().thenAccept(modelRenderable -> placeModel(modelRenderable,anchor));

    }

    //Anchor centeranchor;

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor)
    {

        AnchorNode anchorNode = new AnchorNode(anchor);
        //centeranchor = anchor;
        anchorNode.setRenderable(modelRenderable);
        anchorNode.setLocalScale(new Vector3(0.01f,0.01f,0.01f));



           anchorNode.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

               playVideo(modelRenderable, anchor);

               removeAnchoreNode(anchorNode);
           });
           arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private void placeModel1(ModelRenderable modelRenderable, Anchor anchor)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        //centeranchor = anchor;
        anchorNode.setRenderable(modelRenderable);
        anchorNode.setLocalScale(new Vector3(0.01f,0.01f,0.01f));



        anchorNode.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            playVideo1(modelRenderable, anchor);

            removeAnchoreNode(anchorNode);
        });
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private void removeAnchoreNode(AnchorNode anchorNode) {

        arFragment.getArSceneView().getScene().removeChild(anchorNode);

    }


    private void playVideo(ModelRenderable modelRenderable,Anchor anchor)
    {

        mediaPlayer.start();
        mediaPlayer.setLooping(false);


        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        Node videoNode = new Node();
        videoNode.setParent(anchorNode);

        float videoWidth = mediaPlayer.getVideoWidth();
        float videoHeight = mediaPlayer.getVideoHeight();
        videoNode.setLocalScale(
                new Vector3(
                        0.1f,0.1f,0.1f));

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //mediaPlayer.release();
                anchorNode.removeChild(videoNode);
                placeModel(modelRenderable,anchor);

            }
        });

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

            mytexture
                    .getSurfaceTexture()
                    .setOnFrameAvailableListener(
                            (SurfaceTexture surfaceTexture) -> {
                                videoNode.setRenderable(videoRenderable);
                                mytexture.getSurfaceTexture().setOnFrameAvailableListener(null);
                            });

        }
        else {
            videoNode.setRenderable(videoRenderable);
        }

        //scene.addChild(anchorNode);

    }

    private void playVideo1(ModelRenderable modelRenderable, Anchor anchor)
    {


        mediaplayer1.start();
        mediaplayer1.setLooping(false);


        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        Node videoNode = new Node();
        videoNode.setParent(anchorNode);

        float videoWidth = mediaplayer1.getVideoWidth();
        float videoHeight = mediaplayer1.getVideoHeight();
        videoNode.setLocalScale(
                new Vector3(
                        0.1f,0.1f,0.1f));

        mediaplayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //mediaPlayer.release();
                anchorNode.removeChild(videoNode);
                placeModel1(modelRenderable,anchor);

            }
        });

        if (!mediaplayer1.isPlaying()) {
            mediaplayer1.start();

            mytexture1
                    .getSurfaceTexture()
                    .setOnFrameAvailableListener(
                            (SurfaceTexture surfaceTexture) -> {
                                videoNode.setRenderable(videoRenderable1);
                                mytexture.getSurfaceTexture().setOnFrameAvailableListener(null);
                            });

        }
        else {
            videoNode.setRenderable(videoRenderable1);
        }
    }

    public void onPause() {

        super.onPause();

        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
        if(mediaplayer1!=null){
            mediaplayer1.stop();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer = null;
        }
        if (mediaplayer1 != null) {
            mediaplayer1.reset();
            mediaplayer1 = null;

        }
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

}




