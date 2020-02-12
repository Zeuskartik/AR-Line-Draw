package com.mindrops.sixfeet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class ARActivity extends AppCompatActivity {
    private ArFragment arFragment;
    private Config config;
    private FrameLayout targetLayout;
    private FrameLayout startButtonLayout;
    private TextView distanceTv;
    private Anchor hitAnchor;
    private AnchorNode firstAnchorNode;
    private AnchorNode lastAnchorNode;
    private Node initNode;
    private Node lineNode;
    private Node endPointNode;
    private boolean isTracking;
    private boolean isHitting;
    private boolean firstPointReceived = false;
    private Color lineColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        targetLayout = findViewById(R.id.target_button);
        startButtonLayout = findViewById(R.id.start_button);
        distanceTv = findViewById(R.id.distance_tv);
        lineColor = new Color(238.00f / 255.00f, 189.00f / 255.00f, 15.00f / 255.00f);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);


    }

    @Override
    protected void onResume() {
        super.onResume();
        initArFragment();
    }

    private void initArFragment() {
        if (arFragment != null) {
            hideControls(true);
            arFragment.getArSceneView().getPlaneRenderer().setVisible(false);
            arFragment.getArSceneView().getScene().addOnUpdateListener(this::onFrameDetected);
        }
    }

    private void onFrameDetected(FrameTime frameTime) {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                new Handler().postDelayed(this::showControls, 1000);
            } else {
                hideControls(true);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                contentView.invalidate();
            }
        }
    }

    private boolean updateTracking() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        /*isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;*/
        assert frame != null;
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
        for (Plane p : planes) {
            if (p.getTrackingState() == TrackingState.TRACKING) {
                isTracking = true;

                /* break;*/
            } else {
                isTracking = false;
            }
        }
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    hitAnchor = hit.createAnchor();
                    if (firstPointReceived && isTracking) {
                        placePointsOnScreen(hit.createAnchor());

                    }
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }

    private void placePointsOnScreen(Anchor anchor) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        hideControls(false);
        if (firstPointReceived) {
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            Vector3 point1, point2;
            point1 = firstAnchorNode.getWorldPosition();
            point2 = anchorNode.getWorldPosition();
            calculateDistanceInMeters(point1, point2);
            final Vector3 difference = Vector3.subtract(point2, point1);
            final Vector3 directionFromTopToBottom = difference.normalized();
            final Quaternion rotationFromAToB =
                    Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
            MaterialFactory.makeTransparentWithColor(this, lineColor)
                    .thenAccept(
                            material -> {
                                if (lineNode == null) {
                                    lineNode = new Node();
                                    endPointNode = new Node();
                                } else {
                                    lastAnchorNode.removeChild(endPointNode);
                                    lastAnchorNode.removeChild(lineNode);
                                }
                                lineNode.setParent(anchorNode);
                                endPointNode.setParent(anchorNode);
                                lineNode.setRenderable(getModel(material, difference));
                                createPoint(endPointNode);
                                lineNode.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
                                //For Cubical line
                                lineNode.setWorldRotation(rotationFromAToB);
                                //For cylinderical line
                                /*lineNode.setWorldRotation(Quaternion.multiply(rotationFromAToB,
                                        Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 90)));*/
                            }
                    );
            lastAnchorNode = anchorNode;
        }


    }

    private void calculateDistanceInMeters(Vector3 point1, Vector3 point2) {
        float dx = point2.x - point1.x;
        float dy = point2.y - point1.y;
        float dz = point2.z - point1.z;
        float dist = (float) (Math.sqrt((dx * dx + dy * dy + dz * dz)));
        String distanceFormatted = String.format("%.4f", dist);
        /*if(distanceFormatted.equals("1.8288")){
            lineColor = new Color(0.00f / 255.00f, 214.00f / 255.00f, 145.00f / 255.00f);
        }*/
        distanceTv.setText(distanceFormatted + "m");
    }

    private Renderable getModel(Material material, Vector3 difference) {
        //Cubical line
        ModelRenderable model = ShapeFactory.makeCube(
                new Vector3(.01f, .01f, difference.length()),
                Vector3.zero(), material);
        model.setShadowCaster(false);
        model.setShadowReceiver(false);
        //Cyliderical line
       /* ModelRenderable model = ShapeFactory.makeCylinder(0.005f, difference.length(),
                new Vector3(0f, difference.length() / 2, 0f), material);
        model.setShadowCaster(false);
        model.setShadowReceiver(false);*/

        return model;
    }


    public void showControls() {
        distanceTv.setVisibility(View.VISIBLE);
        arFragment.getPlaneDiscoveryController().hide();
        //disablePlaneDiscovery();
        targetLayout.setVisibility(View.VISIBLE);
        startButtonLayout.setVisibility(View.VISIBLE);
        startButtonLayout.setOnClickListener(v -> {
            createFirstAnchor(hitAnchor);
        });

    }

    private void createPoint(Node node) {
        MaterialFactory.makeTransparentWithColor(getApplicationContext(), lineColor).thenAccept(material -> {
            ModelRenderable modelRenderable = ShapeFactory.makeSphere(0.008f, new Vector3(0f, 0f, 0f), material);
            modelRenderable.setShadowCaster(false);
            modelRenderable.setShadowReceiver(false);
            node.setRenderable(modelRenderable);
        });
    }


    private void createFirstAnchor(Anchor hitAnchor) {
        hideControls(false);
        firstAnchorNode = new AnchorNode(hitAnchor);
        createPoint(firstAnchorNode);
        arFragment.getArSceneView().getScene().addChild(firstAnchorNode);
        firstPointReceived = true;
    }

    public void hideControls(boolean showPlaneDiscovery) {
        if (showPlaneDiscovery) {
            arFragment.getPlaneDiscoveryController().show();
            distanceTv.setVisibility(View.GONE);
            //enablePlaneDiscovery();
        }
        targetLayout.setVisibility(View.GONE);
        startButtonLayout.setVisibility(View.GONE);
        startButtonLayout.setOnClickListener(null);
    }

    private void enablePlaneDiscovery() {
        if (arFragment.getArSceneView().getSession() != null) {
            config = arFragment.getArSceneView().getSession().getConfig();
            config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
            arFragment.getArSceneView().getSession().configure(config);
        }
    }

    private void disablePlaneDiscovery() {
        if (arFragment.getArSceneView().getSession() != null) {
            config = arFragment.getArSceneView().getSession().getConfig();
            config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
            arFragment.getArSceneView().getSession().configure(config);
        }
    }
}
