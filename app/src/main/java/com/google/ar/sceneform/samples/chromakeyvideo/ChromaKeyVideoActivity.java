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
import android.widget.Toast;
import android.widget.VideoView;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
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

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.ar.sceneform.samples.chromakeyvideo.R.id.ux_fragment;


public class ChromaKeyVideoActivity extends AppCompatActivity implements Scene.OnUpdateListener{

    private static final String TAG = ChromaKeyVideoActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private Scene scene;

    @Nullable private ModelRenderable videoRenderable,videoRenderable1,videoRenderable2,videoRenderable3,videoRenderable4;

    private boolean isImageDetected = false;
    private MediaPlayer mediaPlayer,mediaPlayer1,mediaPlayer2,mediaPlayer3,mediaPlayer4;

    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);

    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.2f;


    private ExternalTexture mytexture,mytexture1,mytexture2,mytexture3,mytexture4;
    private ViewRenderable imageRenderable,imageRenderable1,imageRenderable2,imageRenderable3,imageRenderable4;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_video);
        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(ux_fragment);

        arFragment.getArSceneView().getPlaneRenderer().setVisible(false);
        // Create an ExternalTexture for displaying the contents of the video.
        mytexture = new ExternalTexture();
        mytexture1 = new ExternalTexture();
        mytexture2 = new ExternalTexture();
        mytexture3 = new ExternalTexture();
        mytexture4 = new ExternalTexture();



//        mediaPlayer = MediaPlayer.create(this, R.raw.bridge);
//        mediaPlayer.setSurface(mytexture.getSurface());
//        mediaPlayer.setLooping(false);
//
//
//        mediaPlayer1 = MediaPlayer.create(this, R.raw.crossings);
//        mediaPlayer1.setSurface(mytexture1.getSurface());
//        mediaPlayer1.setLooping(false);
//
//        mediaPlayer2 = MediaPlayer.create(this, R.raw.liberty);
//        mediaPlayer2.setSurface(mytexture2.getSurface());
//        mediaPlayer2.setLooping(false);
//
//        mediaPlayer3 = MediaPlayer.create(this, R.raw.marathon_course);
//        mediaPlayer3.setSurface(mytexture3.getSurface());
//        mediaPlayer3.setLooping(false);
//
//        mediaPlayer4 = MediaPlayer.create(this, R.raw.runners);
//        mediaPlayer4.setSurface(mytexture4.getSurface());
//        mediaPlayer4.setLooping(false);
//
//        mediaPlayer5 = MediaPlayer.create(this,R.raw.lion_chroma);
//        mediaPlayer5.setSurface(mytexture5.getSurface());
//        mediaPlayer5.setLooping(false);



        ModelRenderable.builder()
                .setSource(this, R.raw.chroma_key_video)
                .build()
                .thenAccept(
                        renderable -> {
                            videoRenderable = renderable;
                            renderable.getMaterial().setExternalTexture("videoTexture", mytexture);
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
                        renderable1 -> {
                            videoRenderable1 = renderable1;
                            renderable1.getMaterial().setExternalTexture("videoTexture", mytexture1);
                            renderable1.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
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
                        renderable2 -> {
                            videoRenderable2 = renderable2;
                            renderable2.getMaterial().setExternalTexture("videoTexture", mytexture2);
                            renderable2.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
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
                        renderable3 -> {
                            videoRenderable3 = renderable3;
                            renderable3.getMaterial().setExternalTexture("videoTexture", mytexture3);
                            renderable3.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
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
                        renderable4 -> {
                            videoRenderable4 = renderable4;
                            renderable4.getMaterial().setExternalTexture("videoTexture", mytexture4);
                            renderable4.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
                        })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load video renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });



        ViewRenderable.builder()
                .setView(this, R.layout.img_board)
                .build()
                .thenAccept(v_renderable -> imageRenderable = v_renderable);

        ViewRenderable.builder()
                .setView(this, R.layout.img_board)
                .build()
                .thenAccept(v_renderable1 -> imageRenderable1 = v_renderable1);

        ViewRenderable.builder()
                .setView(this, R.layout.img_board)
                .build()
                .thenAccept(v_renderable2 -> imageRenderable2 = v_renderable2);

        ViewRenderable.builder()
                .setView(this, R.layout.img_board)
                .build()
                .thenAccept(v_renderable3 -> imageRenderable3 = v_renderable3);
        ViewRenderable.builder()
                .setView(this, R.layout.img_board)
                .build()
                .thenAccept(v_renderable4 -> imageRenderable4 = v_renderable4);






        scene = arFragment.getArSceneView().getScene();
        arFragment.getPlaneDiscoveryController().hide();

        scene.addOnUpdateListener(this::onUpdate);

    }



    AugmentedImage image;

    public void onUpdate(FrameTime frameTime) {

        if (isImageDetected)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        //PointCloud currentCloud = frame.acquirePointCloud();
        //currentCloud.release();

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage myimage : augmentedImages) {

            if (myimage.getTrackingState() == TrackingState.TRACKING) {

                if (myimage.getName().equals("image")) {

                    isImageDetected = true;

                    initialise_media_players(R.raw.bridge,R.raw.crossings,R.raw.liberty,R.raw.marathon_course,R.raw.runners);

                    display_toast_message("Image Detected!!");

                    image = myimage;

                    displayImages();

                    break;
                }

                if(myimage.getName().equals("bib1") || myimage.getName().equals("bib2") || myimage.getName().equals("bib3")){

                    isImageDetected = true;

                    initialise_media_players(R.raw.lion_chroma,0,0,0,0);

                    display_toast_message("Bib image detected");

                    image = myimage;
                    display_Green_Screen();
                    break;
                }
            }
        }
    }

   public void initialise_media_players(int v,int v1,int v2,int v3,int v4){

        if(v!=0) {
            mediaPlayer = MediaPlayer.create(this, v);
            mediaPlayer.setSurface(mytexture.getSurface());
            mediaPlayer.setLooping(false);
        }

        if(v1!=0) {
            mediaPlayer1 = MediaPlayer.create(this, v1);
            mediaPlayer1.setSurface(mytexture1.getSurface());
            mediaPlayer1.setLooping(false);
        }

        if(v2!=0) {
            mediaPlayer2 = MediaPlayer.create(this, v2);
            mediaPlayer2.setSurface(mytexture2.getSurface());
            mediaPlayer2.setLooping(false);
        }

        if(v3!=0) {
            mediaPlayer3 = MediaPlayer.create(this, v3);
            mediaPlayer3.setSurface(mytexture3.getSurface());
            mediaPlayer3.setLooping(false);
        }

       if(v4!=0) {
           mediaPlayer4 = MediaPlayer.create(this, v4);
           mediaPlayer4.setSurface(mytexture4.getSurface());
           mediaPlayer4.setLooping(false);
       }
    }


    Node videoNode,videoNode1,videoNode2,videoNode3,videoNode4;
    Anchor extAnchor,extAnchor1,extAnchor2,extAnchor3,extAnchor4;
    AnchorNode extAnchorNode,extAnchorNode1,extAnchorNode2,extAnchorNode3,extAnchorNode4;
    AnchorNode imageNode,imageNode1,imageNode2,imageNode3,imageNode4;


    public void display_Green_Screen(){

        extAnchor = image.createAnchor(image.getCenterPose());
        extAnchorNode = new AnchorNode(extAnchor);

        extAnchorNode.setParent(arFragment.getArSceneView().getScene());

        if(videoNode==null)
            videoNode = new Node();
        videoNode.setParent(extAnchorNode);

        videoNode.setLocalScale(new Vector3(0.25f,0.15f,0.25f));

        mytexture
                .getSurfaceTexture()
                .setOnFrameAvailableListener(
                        (SurfaceTexture surfaceTexture) -> {
                            videoNode.setRenderable(videoRenderable);
                            mytexture.getSurfaceTexture().setOnFrameAvailableListener(null);
                        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                extAnchorNode.removeChild(videoNode);
            }
        });

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        else {
            videoNode.setRenderable(videoRenderable);
        }

    }



    public void display_toast_message(String message){

        Toast toast = Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

    }


    public void displayImages(){

        Pose offset1 = image.getCenterPose().compose(Pose.makeTranslation(0.1f,0f,0f));
        Pose offset2 = image.getCenterPose().compose(Pose.makeTranslation(-0.08f,0f,0f));
        Pose offset3 = image.getCenterPose().compose(Pose.makeTranslation(0.04f,0f,-0.01f));
        Pose offset4 = image.getCenterPose().compose(Pose.makeTranslation(0f,0f,0.03f));

        extAnchor = image.createAnchor(image.getCenterPose());
        extAnchor1 = image.createAnchor(offset1);
        extAnchor2 = image.createAnchor(offset2);
        extAnchor3 = image.createAnchor(offset3);
        extAnchor4 = image.createAnchor(offset4);

        displayHandImage();
        displayHandImage1();
        displayHandImage2();
        displayHandImage3();
        displayHandImage4();

    }


    private void displayHandImage(){

        float qx,qy,qz,qw;

        Pose imgpose = extAnchor.getPose();
        qx = imgpose.qw();
        qy = imgpose.qy();
        qz = imgpose.qz();
        qw = imgpose.qw();
        Log.i(TAG, "qx: " + qx + "qy: " + qy + "qz: " + qz + "qw: " + qw);

        Pose offset = extAnchor.getPose().compose(Pose.makeRotation(qx,qy,qz,qw));
        Anchor anchor1 = image.createAnchor(offset);
        //AnchorNode imageNode = new AnchorNode(anchor1);
        if(imageNode==null)
        imageNode = new AnchorNode(anchor1);

        imageNode.setRenderable(imageRenderable);
        imageNode.setLocalScale(new Vector3(0.01f,0.01f,0.01f));

        if(!imageNode.isDescendantOf(arFragment.getArSceneView().getScene()))
        imageNode.setParent(arFragment.getArSceneView().getScene());

        imageNode.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            playVideoImage();

            arFragment.getArSceneView().getScene().removeChild(imageNode);
        });
    }

    private void displayHandImage1(){

        float qx,qy,qz,qw;

        Pose imgpose = extAnchor1.getPose();
        qx = imgpose.qw();
        qy = imgpose.qy();
        qz = imgpose.qz();
        qw = imgpose.qw();
        Log.i(TAG, "qx: " + qx + "qy: " + qy + "qz: " + qz + "qw: " + qw);

        Pose offset = extAnchor1.getPose().compose(Pose.makeRotation(qx,qy,qz,qw));
        Anchor anchor1 = image.createAnchor(offset);

        if(imageNode1==null)
        imageNode1 = new AnchorNode(anchor1);

        imageNode1.setRenderable(imageRenderable1);
        imageNode1.setLocalScale(new Vector3(0.01f,0.01f,0.01f));

        if(!imageNode1.isDescendantOf(arFragment.getArSceneView().getScene()))
        imageNode1.setParent(arFragment.getArSceneView().getScene());

        imageNode1.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            playVideoImage1();

            arFragment.getArSceneView().getScene().removeChild(imageNode1);
        });
    }

    private void displayHandImage2(){

        float qx,qy,qz,qw;

        Pose imgpose = extAnchor2.getPose();
        qx = imgpose.qw();
        qy = imgpose.qy();
        qz = imgpose.qz();
        qw = imgpose.qw();
        Log.i(TAG, "qx: " + qx + "qy: " + qy + "qz: " + qz + "qw: " + qw);

        Pose offset = extAnchor2.getPose().compose(Pose.makeRotation(qx,qy,qz,qw));
        Anchor anchor1 = image.createAnchor(offset);

        if(imageNode2==null)
        imageNode2 = new AnchorNode(anchor1);

        imageNode2.setRenderable(imageRenderable2);
        imageNode2.setLocalScale(new Vector3(0.01f,0.01f,0.01f));

        if(!imageNode2.isDescendantOf(arFragment.getArSceneView().getScene()))
        imageNode2.setParent(arFragment.getArSceneView().getScene());

        imageNode2.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            playVideoImage2();

            arFragment.getArSceneView().getScene().removeChild(imageNode2);
        });
    }

    private void displayHandImage3(){

        float qx,qy,qz,qw;

        Pose imgpose = extAnchor3.getPose();
        qx = imgpose.qw();
        qy = imgpose.qy();
        qz = imgpose.qz();
        qw = imgpose.qw();
        Log.i(TAG, "qx: " + qx + "qy: " + qy + "qz: " + qz + "qw: " + qw);

        Pose offset = extAnchor3.getPose().compose(Pose.makeRotation(qx,qy,qz,qw));
        Anchor anchor1 = image.createAnchor(offset);

        if(imageNode3==null)
        imageNode3 = new AnchorNode(anchor1);

        imageNode3.setRenderable(imageRenderable3);
        imageNode3.setLocalScale(new Vector3(0.01f,0.01f,0.01f));

        if(!imageNode3.isDescendantOf(arFragment.getArSceneView().getScene()))
        imageNode3.setParent(arFragment.getArSceneView().getScene());

        imageNode3.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            playVideoImage3();

            arFragment.getArSceneView().getScene().removeChild(imageNode3);
        });
    }

    private void displayHandImage4(){

        float qx,qy,qz,qw;

        Pose imgpose = extAnchor4.getPose();
        qx = imgpose.qw();
        qy = imgpose.qy();
        qz = imgpose.qz();
        qw = imgpose.qw();
        Log.i(TAG, "qx: " + qx + "qy: " + qy + "qz: " + qz + "qw: " + qw);

        Pose offset = extAnchor4.getPose().compose(Pose.makeRotation(qx,qy,qz,qw));
        Anchor anchor1 = image.createAnchor(offset);

        if(imageNode4==null)
        imageNode4 = new AnchorNode(anchor1);

        imageNode4.setRenderable(imageRenderable4);
        imageNode4.setLocalScale(new Vector3(0.01f,0.01f,0.01f));

        if(!imageNode4.isDescendantOf(arFragment.getArSceneView().getScene()))
        imageNode4.setParent(arFragment.getArSceneView().getScene());

        imageNode4.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            playVideoImage4();

            arFragment.getArSceneView().getScene().removeChild(imageNode4);
        });
    }


    private void playVideoImage(){

        //AtomicBoolean tapped = new AtomicBoolean(false);
        stopOtherMedia("main");


        extAnchorNode = new AnchorNode(extAnchor);
        extAnchorNode.setParent(arFragment.getArSceneView().getScene());

        if(videoNode==null)
        videoNode = new Node();
        videoNode.setParent(extAnchorNode);

        videoNode.setLocalScale(new Vector3(0.1f,0.15f,0.1f));

        videoNode.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {
            stick_to_screen(R.raw.bridge);
        });

        mytexture
                .getSurfaceTexture()
                .setOnFrameAvailableListener(
                        (SurfaceTexture surfaceTexture) -> {
                            videoNode.setRenderable(videoRenderable);
                            mytexture.getSurfaceTexture().setOnFrameAvailableListener(null);
                        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                extAnchorNode.removeChild(videoNode);
                displayHandImage();
                //displayImages(image);
            }
        });

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        else {
            videoNode.setRenderable(videoRenderable);
        }
    }

    private void playVideoImage1(){

        AtomicBoolean tapped = new AtomicBoolean(false);
        stopOtherMedia("one");
        extAnchorNode1 = new AnchorNode(extAnchor1);
        extAnchorNode1.setParent(arFragment.getArSceneView().getScene());

        if(videoNode1==null)
        videoNode1 = new Node();
        videoNode1.setParent(extAnchorNode1);

        videoNode1.setLocalScale(new Vector3(0.1f,0.15f,0.1f));

        videoNode1.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            stick_to_screen(R.raw.crossings);

        });

        mytexture1
                .getSurfaceTexture()
                .setOnFrameAvailableListener(
                        (SurfaceTexture surfaceTexture) -> {
                            videoNode1.setRenderable(videoRenderable1);
                            mytexture1.getSurfaceTexture().setOnFrameAvailableListener(null);
                        });

        mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                extAnchorNode1.removeChild(videoNode1);
                displayHandImage1();
            }
        });

        if (!mediaPlayer1.isPlaying()) {
            mediaPlayer1.start();
        }
        else {
            videoNode1.setRenderable(videoRenderable1);
        }
    }

    private void playVideoImage2(){

        AtomicBoolean tapped = new AtomicBoolean(false);
        stopOtherMedia("two");

        mediaPlayer2.start();
        mediaPlayer2.setLooping(false);

        extAnchorNode2 = new AnchorNode(extAnchor2);
        extAnchorNode2.setParent(arFragment.getArSceneView().getScene());

        if(videoNode2==null)
        videoNode2 = new Node();
        videoNode2.setParent(extAnchorNode2);

        videoNode2.setLocalScale(new Vector3(0.1f,0.15f,0.1f));
        videoNode2.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

           stick_to_screen(R.raw.liberty);

        });

        mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                extAnchorNode2.removeChild(videoNode2);
                displayHandImage2();

            }
        });

        if (!mediaPlayer2.isPlaying()) {
            mediaPlayer2.start();

            mytexture2
                    .getSurfaceTexture()
                    .setOnFrameAvailableListener(
                            (SurfaceTexture surfaceTexture) -> {
                                videoNode2.setRenderable(videoRenderable2);
                                mytexture2.getSurfaceTexture().setOnFrameAvailableListener(null);
                            });

        }
        else {
            videoNode2.setRenderable(videoRenderable2);
        }
    }

    private void playVideoImage3(){

        AtomicBoolean tapped = new AtomicBoolean(false);

        stopOtherMedia("three");

        mediaPlayer3.start();
        mediaPlayer3.setLooping(false);


        extAnchorNode3 = new AnchorNode(extAnchor3);
        extAnchorNode3.setParent(arFragment.getArSceneView().getScene());

        if(videoNode3==null)
        videoNode3 = new Node();

        videoNode3.setParent(extAnchorNode3);

        videoNode3.setLocalScale(new Vector3(0.1f,0.15f,0.1f));

        videoNode3.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            stick_to_screen(R.raw.marathon_course);

        });

        mediaPlayer3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                extAnchorNode3.removeChild(videoNode3);
                displayHandImage3();

            }
        });

        if (!mediaPlayer3.isPlaying()) {
            mediaPlayer3.start();

            mytexture3
                    .getSurfaceTexture()
                    .setOnFrameAvailableListener(
                            (SurfaceTexture surfaceTexture) -> {
                                videoNode3.setRenderable(videoRenderable3);
                                mytexture3.getSurfaceTexture().setOnFrameAvailableListener(null);
                            });

        }
        else {
            videoNode3.setRenderable(videoRenderable3);
        }

    }

    private void playVideoImage4(){

        AtomicBoolean tapped = new AtomicBoolean(false);

        stopOtherMedia("four");

        mediaPlayer4.start();
        mediaPlayer4.setLooping(false);

        extAnchorNode4 = new AnchorNode(extAnchor4);
        extAnchorNode4.setParent(arFragment.getArSceneView().getScene());

        if(videoNode4==null)
        videoNode4 = new Node();
        videoNode4.setParent(extAnchorNode4);

        videoNode4.setLocalScale(new Vector3(0.1f,0.15f,0.1f));

        videoNode4.setOnTapListener((HitTestResult hitresult, MotionEvent motionevent) -> {

            stick_to_screen(R.raw.runners);

        });

        mediaPlayer4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                extAnchorNode4.removeChild(videoNode4);
                displayHandImage4();

            }
        });

        if (!mediaPlayer4.isPlaying()) {
            mediaPlayer4.start();

            mytexture4
                    .getSurfaceTexture()
                    .setOnFrameAvailableListener(
                            (SurfaceTexture surfaceTexture) -> {
                                videoNode4.setRenderable(videoRenderable4);
                                mytexture4.getSurfaceTexture().setOnFrameAvailableListener(null);
                            });

        }
        else {
            videoNode4.setRenderable(videoRenderable4);
        }

    }

    public void stick_to_screen(int video_res){
        VideoView playme = findViewById(R.id.video_card);
        playme.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + video_res));
        playme.setVisibility(View.VISIBLE);
        playme.start();

        playme.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playme.setVisibility(View.GONE);
            }
        });

        playme.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( motionEvent.getAction() == MotionEvent.ACTION_DOWN )
                {
                    playme.setVisibility(View.GONE);

                }
                return true;
            }
        });

    }

    public void stopOtherMedia(String name){

        if(name.equals("main")){
            if(videoNode1!=null &&  videoNode1.isDescendantOf(extAnchorNode1) && mediaPlayer1.isPlaying()){
            extAnchorNode1.removeChild(videoNode1);
            displayHandImage1();
            }

            if(videoNode2!=null &&   videoNode2.isDescendantOf(extAnchorNode2) && mediaPlayer2.isPlaying()) {
                extAnchorNode2.removeChild(videoNode2);
            displayHandImage2();
            }

            if(videoNode3!=null &&   videoNode3.isDescendantOf(extAnchorNode3) && mediaPlayer3.isPlaying()) {
                extAnchorNode3.removeChild(videoNode3);
                displayHandImage3();
            }

            if(videoNode4!=null &&  videoNode4.isDescendantOf(extAnchorNode4) && mediaPlayer4.isPlaying()) {
                extAnchorNode4.removeChild(videoNode4);
                displayHandImage4();
            }

        }

        if(name.equals("one")){
            if(videoNode!=null &&   videoNode.isDescendantOf(extAnchorNode) && mediaPlayer.isPlaying()){
                extAnchorNode.removeChild(videoNode);
                displayHandImage();
            }

            if(videoNode2!=null &&   videoNode2.isDescendantOf(extAnchorNode2) && mediaPlayer2.isPlaying()) {
                extAnchorNode2.removeChild(videoNode2);
                displayHandImage2();
            }

            if(videoNode3!=null &&  videoNode3.isDescendantOf(extAnchorNode3) && mediaPlayer3.isPlaying()) {
                extAnchorNode3.removeChild(videoNode3);
                displayHandImage3();
            }

            if(videoNode4!=null &&  videoNode4.isDescendantOf(extAnchorNode4) && mediaPlayer4.isPlaying()) {
                extAnchorNode4.removeChild(videoNode4);
                displayHandImage4();
            }
        }

        if(name.equals("two")){
            if(videoNode1!=null &&   videoNode1.isDescendantOf(extAnchorNode1) && mediaPlayer1.isPlaying()){
                extAnchorNode1.removeChild(videoNode1);
                displayHandImage1();
            }

            if(videoNode!=null &&   videoNode.isDescendantOf(extAnchorNode) && mediaPlayer.isPlaying()){
                extAnchorNode.removeChild(videoNode);
                displayHandImage();
            }

            if(videoNode3!=null &&   videoNode3.isDescendantOf(extAnchorNode3) && mediaPlayer3.isPlaying()) {
                extAnchorNode3.removeChild(videoNode3);
                displayHandImage3();
            }

            if(videoNode4!=null &&  videoNode4.isDescendantOf(extAnchorNode4) && mediaPlayer4.isPlaying()) {
                extAnchorNode4.removeChild(videoNode4);
                displayHandImage4();
            }
        }

        if(name.equals("three")){
            if(videoNode1!=null &&   videoNode1.isDescendantOf(extAnchorNode1) && mediaPlayer1.isPlaying()){
                extAnchorNode1.removeChild(videoNode1);
                displayHandImage1();
            }

            if(videoNode2!=null &&   videoNode2.isDescendantOf(extAnchorNode2) && mediaPlayer2.isPlaying()) {
                extAnchorNode2.removeChild(videoNode2);
                displayHandImage2();
            }

            if(videoNode!=null &&   videoNode.isDescendantOf(extAnchorNode) && mediaPlayer.isPlaying()){
                extAnchorNode.removeChild(videoNode);
                displayHandImage();
            }

            if(videoNode4!=null &&  videoNode4.isDescendantOf(extAnchorNode4) && mediaPlayer4.isPlaying()) {
                extAnchorNode4.removeChild(videoNode4);
                displayHandImage4();
            }
        }

        if(name.equals("four")){
            if(videoNode1!=null &&   videoNode1.isDescendantOf(extAnchorNode1) && mediaPlayer1.isPlaying()){
                extAnchorNode1.removeChild(videoNode1);
                displayHandImage1();
            }

            if(videoNode2!=null &&   videoNode2.isDescendantOf(extAnchorNode2) && mediaPlayer2.isPlaying()) {
                extAnchorNode2.removeChild(videoNode2);
                displayHandImage2();
            }

            if(videoNode3!=null &&   videoNode3.isDescendantOf(extAnchorNode3) && mediaPlayer3.isPlaying()) {
                extAnchorNode3.removeChild(videoNode3);
                displayHandImage3();
            }

            if(videoNode!=null &&   videoNode.isDescendantOf(extAnchorNode) && mediaPlayer.isPlaying()){
                extAnchorNode.removeChild(videoNode);
                displayHandImage();
            }
        }
    }

    public void onPause() {

        super.onPause();

        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
        if(mediaPlayer1!=null){
            mediaPlayer1.stop();
        }
        if(mediaPlayer2!=null){
            mediaPlayer2.stop();
        }
        if(mediaPlayer3!=null){
            mediaPlayer3.stop();
        }
        if(mediaPlayer4!=null){
            mediaPlayer4.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer = null;
        }
        if (mediaPlayer1 != null) {
            mediaPlayer1.reset();
            mediaPlayer1 = null;
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.reset();
            mediaPlayer2 = null;
        }
        if (mediaPlayer3 != null) {
            mediaPlayer3.reset();
            mediaPlayer3 = null;
        }
        if (mediaPlayer4 != null) {
            mediaPlayer4.reset();
            mediaPlayer4 = null;
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