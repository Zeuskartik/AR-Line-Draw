package com.mindrops.sixfeet;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Collection;
import java.util.List;


public class ARActivity extends AppCompatActivity implements Scene.OnUpdateListener {
    private ArFragment arFragment;
    private Config config;
    private ImageView targetLayout;
    private TextView distanceTv, startButtonLayout, warningTv;
    private Anchor hitAnchor;
    private AnchorNode firstAnchorNode;
    private AnchorNode lastAnchorNode;
    private AnchorNode lineNode;
    private AnchorNode endPointNode;
    private boolean isTracking;
    private boolean isHitting;
    private boolean firstPointReceived = false;
    private Color yellowLineColor = new Color(238.00f / 255.00f, 189.00f / 255.00f, 15.00f / 255.00f);
    private Color greenLineColor = new Color(0.00f / 255.00f, 214.00f / 255.00f, 145.00f / 255.00f);
    private Color lineColor;
    double currentDistance = 0.0;
    boolean sixFeetCovered = false;
    TextView resetBtn, finishBtn, descrTv;
    String distanceFormatted;
    CardView distanceCv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        targetLayout = findViewById(R.id.target_button);
        startButtonLayout = findViewById(R.id.start_button);
        warningTv = findViewById(R.id.warning_tv);
        distanceTv = findViewById(R.id.distance_tv);
        lineColor = yellowLineColor;
        distanceCv = findViewById(R.id.ditanceCv);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        resetBtn = findViewById(R.id.reset_btn);
        finishBtn = findViewById(R.id.finish_btn);
        descrTv = findViewById(R.id.descrText_Tv);

        resetBtn.setOnClickListener(v -> {
            try {

                if (firstAnchorNode != null) {
                    arFragment.getArSceneView().getScene().removeOnUpdateListener(this);
                    firstAnchorNode.setRenderable(null);
                    arFragment.getArSceneView().getScene().removeChild(firstAnchorNode);
                    firstAnchorNode = null;
                    firstPointReceived = false;
                }
                if (lineNode != null) {
                    lineNode.setRenderable(null);
                    lastAnchorNode.removeChild(lineNode);
                    lineNode = null;
                }
                if (endPointNode != null) {
                    endPointNode.setRenderable(null);
                    lastAnchorNode.removeChild(endPointNode);
                    endPointNode = null;
                }
                if (lastAnchorNode != null) {
                    lastAnchorNode.setRenderable(null);
                    arFragment.getArSceneView().getScene().removeChild(lastAnchorNode);
                    lastAnchorNode = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("##### Exception -->  ", "exception occurred");
            }
            resetBtn.setVisibility(View.GONE);
            finishBtn.setVisibility(View.GONE);
            distanceCv.setVisibility(View.GONE);
            descrTv.setVisibility(View.GONE);
            sixFeetCovered = false;
            lineColor = yellowLineColor;
            new Handler().postDelayed(() -> showControls(true), 1500);

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initArFragment();
    }

    private void initArFragment() {
        if (arFragment != null) {
            arFragment.getArSceneView().getPlaneRenderer().setVisible(true);
            hideControls(true);
        }

    }

    private void onFrameDetected(FrameTime frameTime) {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                new Handler().postDelayed(
                        () -> {
                            showControls(false);
                        }, 1500
                );
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
        /*isTracking = frame != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING;*/
        assert frame != null;
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
        for (Plane p : planes) {
            isTracking = p.getTrackingState() == TrackingState.TRACKING;
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
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {

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
            final Quaternion rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
            if (currentDistance < 2.02 && !sixFeetCovered) {
                MaterialFactory.makeOpaqueWithColor(this, lineColor)
                        .thenAccept(
                                material -> {
                                    if (lineNode == null) {
                                        lineNode = new AnchorNode();
                                        endPointNode = new AnchorNode();
                                    } else {
                                        lastAnchorNode.removeChild(endPointNode);
                                        lastAnchorNode.removeChild(lineNode);
                                    }
                                    lineNode.setRenderable(getModel(material, difference));
                                    lineNode.setParent(anchorNode);
                                    createPoint(endPointNode);
                                    endPointNode.setParent(lineNode.getParent());
                                    lineNode.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
                                    lineNode.setWorldRotation(rotationFromAToB);
                                    if (lineColor.equals(greenLineColor)) {
                                        sixFeetCovered = true;
                                        warningTv.setVisibility(View.GONE);
                                        distanceTv.setText("2.0m");
                                        showPopup();
                                    }
                                }
                        );
                lastAnchorNode = anchorNode;
            }
        }
    }

    private void showPopup() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        distanceTv.setVisibility(View.GONE);
        warningTv.setVisibility(View.GONE);
        descrTv.setVisibility(View.VISIBLE);
        finishBtn.setVisibility(View.VISIBLE);
    }

    private void calculateDistanceInMeters(Vector3 point1, Vector3 point2) {
        float dx = point2.x - point1.x;
        float dy = point2.y - point1.y;
        float dz = point2.z - point1.z;
        float dist = (float) (Math.sqrt((dx * dx + dy * dy + dz * dz)));
        distanceFormatted = String.format("%.1f", dist);

        currentDistance = dist;

        if (currentDistance < 1.98) {
            lineColor = yellowLineColor;
        } else if (currentDistance > 1.98 && currentDistance < 2.02) {
            lineColor = greenLineColor;
        }

        if (currentDistance > 2.02 && !sixFeetCovered) {
            warningTv.setVisibility(View.VISIBLE);
        } else {
            warningTv.setVisibility(View.GONE);
        }

        if (!sixFeetCovered && currentDistance < 2.02) {
            distanceTv.setVisibility(View.VISIBLE);
            distanceTv.setText(String.format("%sm", distanceFormatted));
        }
    }


    private Renderable getModel(Material material, Vector3 difference) {
        //Cubical line
        ModelRenderable model = CustomShapeFactory.createCustomLine(
                new Vector3(.013f, .01f, difference.length()),
                Vector3.zero(), material);
        model.setShadowCaster(false);
        model.setShadowReceiver(false);
        return model;
    }


    public void showControls(Boolean shouldInitListener) {
        distanceTv.setVisibility(View.VISIBLE);
        arFragment.getPlaneDiscoveryController().hide();
        //disablePlaneDiscovery();
        if (shouldInitListener) {
            arFragment.getArSceneView().getScene().addOnUpdateListener(this);
        }
        targetLayout.setVisibility(View.VISIBLE);
        startButtonLayout.setVisibility(View.VISIBLE);
        startButtonLayout.setOnClickListener(v -> {
            createFirstAnchor(hitAnchor);
            resetBtn.setVisibility(View.VISIBLE);
            distanceCv.setVisibility(View.VISIBLE);
        });
    }

    private void createPoint(Node node) {
        MaterialFactory.makeTransparentWithColor(getApplicationContext(), lineColor).thenAccept(material -> {
            ModelRenderable modelRenderable = CustomShapeFactory.createPoint( new Vector3(0f, 0f, 0f), material);
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
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
        targetLayout.setVisibility(View.GONE);
        startButtonLayout.setVisibility(View.GONE);
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

    @Override
    public void onUpdate(FrameTime frameTime) {
        onFrameDetected(frameTime);
    }


}
