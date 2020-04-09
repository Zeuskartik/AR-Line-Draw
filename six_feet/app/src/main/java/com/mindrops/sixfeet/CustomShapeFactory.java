package com.mindrops.sixfeet;

import android.util.Log;

import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.RenderableDefinition;
import com.google.ar.sceneform.rendering.Vertex;
import com.google.ar.sceneform.utilities.AndroidPreconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class CustomShapeFactory {

    static ModelRenderable createPoint(Vector3 center, Material material) {
        AndroidPreconditions.checkMinAndroidApiLevel();
        float radius = 0.010F;
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Vertex> lowerCapVertices = new ArrayList<>();
        ArrayList<Vertex> upperCapVertices = new ArrayList<>();
        ArrayList<Integer> triangleIndices = new ArrayList<>();
        float theta = 0.0F;
        int lowerCenterIndex;
        for (lowerCenterIndex = 0; lowerCenterIndex <= 24; ++lowerCenterIndex) {
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);
            Vector3 position = new Vector3(radius * cosTheta, 0, radius * sinTheta);
            position = Vector3.add(position, center);
            Vertex vertex = Vertex.builder().setPosition(position).setNormal(Vector3.down()).setUvCoordinate(new Vertex.UvCoordinate((cosTheta + 1.0F) / 2.0F, (sinTheta + 1.0F) / 2.0F)).build();
            lowerCapVertices.add(vertex);
            vertex = Vertex.builder().setPosition(position).setNormal(Vector3.up()).setUvCoordinate(new Vertex.UvCoordinate((cosTheta + 1.0F) / 2.0F, (sinTheta + 1.0F) / 2.0F)).build();
            upperCapVertices.add(vertex);
            theta += 0.2617994F;
        }

        lowerCenterIndex = vertices.size();
        vertices.add(Vertex.builder().setPosition(Vector3.add(center, new Vector3(0.0F, 0, 0.0F))).setNormal(Vector3.down()).setUvCoordinate(new Vertex.UvCoordinate(0.5F, 0.5F)).build());
        vertices.addAll(lowerCapVertices);
        int upperCenterIndex = vertices.size();
        vertices.add(Vertex.builder().setPosition(Vector3.add(center, new Vector3(0.0F, 0, 0.0F))).setNormal(Vector3.up()).setUvCoordinate(new Vertex.UvCoordinate(0.5F, 0.5F)).build());
        vertices.addAll(upperCapVertices);
        for (int side = 0; side < 24; ++side) {
            int bottomRight = side + 1;
            int topLeft = side + 24 + 1;
            int topRight = side + 24 + 2;
            triangleIndices.add(side);
            triangleIndices.add(topRight);
            triangleIndices.add(bottomRight);
            triangleIndices.add(side);
            triangleIndices.add(topLeft);
            triangleIndices.add(topRight);
            triangleIndices.add(lowerCenterIndex);
            triangleIndices.add(lowerCenterIndex + side + 1);
            triangleIndices.add(lowerCenterIndex + side + 2);
            triangleIndices.add(upperCenterIndex);
            triangleIndices.add(upperCenterIndex + side + 2);
            triangleIndices.add(upperCenterIndex + side + 1);
        }

        RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder().setTriangleIndices(triangleIndices).setMaterial(material).build();
        RenderableDefinition renderableDefinition = RenderableDefinition.builder().setVertices(vertices).setSubmeshes(Arrays.asList(submesh)).build();
        CompletableFuture future = ((ModelRenderable.Builder) ModelRenderable.builder().setSource(renderableDefinition)).build();

        ModelRenderable result;
        try {
            result = (ModelRenderable) future.get();
        } catch (InterruptedException | ExecutionException var21) {
            throw new AssertionError("Error creating renderable.", var21);
        }

        if (result == null) {
            throw new AssertionError("Error creating renderable.");
        } else {
            return result;
        }
    }

    static ModelRenderable createCustomLine(Vector3 size, Vector3 center, Material material) {
        AndroidPreconditions.checkMinAndroidApiLevel();
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> triangleIndices = new ArrayList<>();

        //Middle Line
        Vector3 extents = size.scaled(0.5F);
        Vector3 p0 = Vector3.add(center, new Vector3(-extents.x, extents.y, extents.z));
        Vector3 p1 = Vector3.add(center, new Vector3(extents.x, extents.y, extents.z));
        Vector3 p2 = Vector3.add(center, new Vector3(extents.x, extents.y, -extents.z));
        Vector3 p3 = Vector3.add(center, new Vector3(-extents.x, extents.y, -extents.z));
        Vector3 up = Vector3.up();
        Vertex.UvCoordinate uv00 = new Vertex.UvCoordinate(0.0F, 0.0F);
        Vertex.UvCoordinate uv10 = new Vertex.UvCoordinate(1.0F, 0.0F);
        Vertex.UvCoordinate uv01 = new Vertex.UvCoordinate(0.0F, 1.0F);
        Vertex.UvCoordinate uv11 = new Vertex.UvCoordinate(1.0F, 1.0F);
        vertices.add(Vertex.builder().setPosition(p3).setNormal(up).setUvCoordinate(uv01).build());
        vertices.add(Vertex.builder().setPosition(p2).setNormal(up).setUvCoordinate(uv11).build());
        vertices.add(Vertex.builder().setPosition(p1).setNormal(up).setUvCoordinate(uv10).build());
        vertices.add(Vertex.builder().setPosition(p0).setNormal(up).setUvCoordinate(uv00).build());

        //Triangle indices
        triangleIndices.add(3);
        triangleIndices.add(1);
        triangleIndices.add(0);
        triangleIndices.add(3);
        triangleIndices.add(2);
        triangleIndices.add(1);

        Log.e("customShape", "triangleIndices-size:" + triangleIndices.size() + " vertices-Size:" + vertices.size());
        Log.e("customShape", "triangleIndices-Array:" + triangleIndices.toString());
        Log.e("customShape", "Vertices-Array:" + vertices);
        Log.e("customShape", "triangleIndices-middle:" + triangleIndices.get(triangleIndices.size()/2));

        RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder().setTriangleIndices(triangleIndices).setMaterial(material).build();
        RenderableDefinition renderableDefinition = RenderableDefinition.builder().setVertices(vertices).setSubmeshes(Arrays.asList(submesh)).build();
        CompletableFuture future = ModelRenderable.builder().setSource(renderableDefinition).build();

        ModelRenderable result;
        try {
            result = (ModelRenderable) future.get();
        } catch (InterruptedException | ExecutionException var32) {
            throw new AssertionError("Error creating renderable.", var32);
        }

        if (result == null) {
            throw new AssertionError("Error creating renderable.");
        } else {
            return result;
        }
    }
}
