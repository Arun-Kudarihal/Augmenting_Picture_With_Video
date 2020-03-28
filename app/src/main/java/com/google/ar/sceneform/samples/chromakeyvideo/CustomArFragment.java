package com.google.ar.sceneform.samples.chromakeyvideo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.IOException;
import java.io.InputStream;
import android.content.Context;

public class CustomArFragment extends ArFragment{

    protected Config getSessionConfiguration(Session session){

        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);

        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);

        Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.mural);
//        Bitmap image2 = BitmapFactory.decodeResource(getResources(),R.drawable.image2);

        aid.addImage("image",image,0.4f);
//        aid.addImage("image2",image2,0.25f);

        config.setAugmentedImageDatabase(aid);
        this.getArSceneView().setupSession(session);
//        try (InputStream is = getContext().getAssets().open("mural.imgdb")) {
//            aid = AugmentedImageDatabase.deserialize(session, is);
//        } catch (IOException e) {
//            Log.i("NO_DB : ", "IO exception loading augmented image database.", e);
//        }
//        config.setAugmentedImageDatabase(aid);
//        this.getArSceneView().setupSession(session);
        return config;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater,container,savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);


        return frameLayout;

    }


}
