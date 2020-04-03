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

public class CustomShapeFactory {

    public static ModelRenderable createCube(Vector3 size, Vector3 center, Material material) {
        AndroidPreconditions.checkMinAndroidApiLevel();
        //Vertices
        Vector3 extents = size.scaled(0.5F);
        Vector3 p0 = Vector3.add(center, new Vector3(-extents.x, -extents.y, extents.z));
        Vector3 p1 = Vector3.add(center, new Vector3(extents.x, -extents.y, extents.z));
        Vector3 p2 = Vector3.add(center, new Vector3(extents.x, -extents.y, -extents.z));
        Vector3 p3 = Vector3.add(center, new Vector3(-extents.x, -extents.y, -extents.z));
        Vector3 p4 = Vector3.add(center, new Vector3(-extents.x, extents.y, extents.z));
        Vector3 p5 = Vector3.add(center, new Vector3(extents.x, extents.y, extents.z));
        Vector3 p6 = Vector3.add(center, new Vector3(extents.x, extents.y, -extents.z));
        Vector3 p7 = Vector3.add(center, new Vector3(-extents.x, extents.y, -extents.z));
        Vector3 up = Vector3.up();
        Vector3 down = Vector3.down();
        Vector3 front = Vector3.forward();
        Vector3 back = Vector3.back();
        Vector3 left = Vector3.left();
        Vector3 right = Vector3.right();
        Vertex.UvCoordinate uv00 = new Vertex.UvCoordinate(0.0F, 0.0F);
        Vertex.UvCoordinate uv10 = new Vertex.UvCoordinate(1.0F, 0.0F);
        Vertex.UvCoordinate uv01 = new Vertex.UvCoordinate(0.0F, 1.0F);
        Vertex.UvCoordinate uv11 = new Vertex.UvCoordinate(1.0F, 1.0F);
        ArrayList<Vertex> vertices = new ArrayList(Arrays.asList(Vertex.builder().setPosition(p0).setNormal(down).setUvCoordinate(uv01).build(), Vertex.builder().setPosition(p1).setNormal(down).setUvCoordinate(uv11).build(), Vertex.builder().setPosition(p2).setNormal(down).setUvCoordinate(uv10).build(), Vertex.builder().setPosition(p3).setNormal(down).setUvCoordinate(uv00).build(), Vertex.builder().setPosition(p7).setNormal(left).setUvCoordinate(uv01).build(), Vertex.builder().setPosition(p4).setNormal(left).setUvCoordinate(uv11).build(), Vertex.builder().setPosition(p0).setNormal(left).setUvCoordinate(uv10).build(), Vertex.builder().setPosition(p3).setNormal(left).setUvCoordinate(uv00).build(), Vertex.builder().setPosition(p4).setNormal(front).setUvCoordinate(uv01).build(), Vertex.builder().setPosition(p5).setNormal(front).setUvCoordinate(uv11).build(), Vertex.builder().setPosition(p1).setNormal(front).setUvCoordinate(uv10).build(), Vertex.builder().setPosition(p0).setNormal(front).setUvCoordinate(uv00).build(), Vertex.builder().setPosition(p6).setNormal(back).setUvCoordinate(uv01).build(), Vertex.builder().setPosition(p7).setNormal(back).setUvCoordinate(uv11).build(), Vertex.builder().setPosition(p3).setNormal(back).setUvCoordinate(uv10).build(), Vertex.builder().setPosition(p2).setNormal(back).setUvCoordinate(uv00).build(), Vertex.builder().setPosition(p5).setNormal(right).setUvCoordinate(uv01).build(), Vertex.builder().setPosition(p6).setNormal(right).setUvCoordinate(uv11).build(), Vertex.builder().setPosition(p2).setNormal(right).setUvCoordinate(uv10).build(), Vertex.builder().setPosition(p1).setNormal(right).setUvCoordinate(uv00).build(), Vertex.builder().setPosition(p7).setNormal(up).setUvCoordinate(uv01).build(), Vertex.builder().setPosition(p6).setNormal(up).setUvCoordinate(uv11).build(), Vertex.builder().setPosition(p5).setNormal(up).setUvCoordinate(uv10).build(), Vertex.builder().setPosition(p4).setNormal(up).setUvCoordinate(uv00).build()));

        //Triangle indices
        ArrayList<Integer> triangleIndices = new ArrayList(36);

        for (int i = 0; i < 6; ++i) {
            triangleIndices.add(3 + 4 * i);
            triangleIndices.add(1 + 4 * i);
            triangleIndices.add(0 + 4 * i);
            triangleIndices.add(3 + 4 * i);
            triangleIndices.add(2 + 4 * i);
            triangleIndices.add(1 + 4 * i);
        }

        RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder().setTriangleIndices(triangleIndices).setMaterial(material).build();
        RenderableDefinition renderableDefinition = RenderableDefinition.builder().setVertices(vertices).setSubmeshes(Arrays.asList(submesh)).build();
        CompletableFuture future = ((ModelRenderable.Builder) ModelRenderable.builder().setSource(renderableDefinition)).build();

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

    //Sphere

    public static ModelRenderable createSphere(float radius, Vector3 center, Material material) {
        AndroidPreconditions.checkMinAndroidApiLevel();
        //int stacks = true;
        //int slices = true;
        //Vertices
        ArrayList<Vertex> vertices = new ArrayList(602);
        float pi = 3.1415927F;
        float doublePi = pi * 2.0F;

        int stack;
        int v;
        for (stack = 0; stack <= 24; ++stack) {
            float phi = pi * (float) stack / 24.0F;
            float sinPhi = (float) Math.sin((double) phi);
            float cosPhi = (float) Math.cos((double) phi);

            for (v = 0; v < 2; ++v) {
                float theta = doublePi * (float) (v == 1 ? 0 : v) / 1.0F;
                float sinTheta = (float) Math.sin((double) theta);
                float cosTheta = (float) Math.cos((double) theta);
                Vector3 position = (new Vector3(sinPhi * cosTheta, cosPhi, sinPhi * sinTheta)).scaled(radius);
                Vector3 normal = position.normalized();
                position = Vector3.add(position, center);
                Vertex.UvCoordinate uvCoordinate = new Vertex.UvCoordinate(1.0F - (float) v / 24.0F, 1.0F - (float) stack / 24.0F);
                Vertex vertex = Vertex.builder().setPosition(position).setNormal(normal).setUvCoordinate(uvCoordinate).build();
                vertices.add(vertex);
            }
        }

        stack = vertices.size();
        int numTriangles = stack * 2;
        int numIndices = numTriangles * 3;

        //Triangle indices
        ArrayList<Integer> triangleIndices = new ArrayList(numIndices);
        v = 0;

        for (stack = 0; stack < 2; ++stack) {
            for (int slice = 0; slice < 24; ++slice) {
                boolean topCap = stack == 0;
                boolean bottomCap = stack == 1;
                int next = slice + 1;
                if (!topCap) {
                    triangleIndices.add(v + slice);
                    triangleIndices.add(v + next);
                    triangleIndices.add(v + slice + 24 + 1);
                }

                if (!bottomCap) {
                    triangleIndices.add(v + next);
                    triangleIndices.add(v + next + 24 + 1);
                    triangleIndices.add(v + slice + 24 + 1);
                }
            }

            v += 2;
        }

        RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder().setTriangleIndices(triangleIndices).setMaterial(material).build();
        RenderableDefinition renderableDefinition = RenderableDefinition.builder().setVertices(vertices).setSubmeshes(Arrays.asList(submesh)).build();
        CompletableFuture future = ((ModelRenderable.Builder) ModelRenderable.builder().setSource(renderableDefinition)).build();

        ModelRenderable result;
        try {
            result = (ModelRenderable) future.get();
        } catch (InterruptedException | ExecutionException var20) {
            throw new AssertionError("Error creating renderable.", var20);
        }

        if (result == null) {
            throw new AssertionError("Error creating renderable.");
        } else {
            return result;
        }
    }

    public static ModelRenderable createCylinder(float radius, float height, Vector3 center, Material material) {
        AndroidPreconditions.checkMinAndroidApiLevel();
        float halfHeight = height / 2.0F;
        float thetaIncrement = 0.2617994F;
        float theta = 0.0F;
        float uStep = 0.041666668F;
        ArrayList<Vertex> vertices = new ArrayList(100);
        ArrayList<Vertex> lowerCapVertices = new ArrayList(25);
        ArrayList<Vertex> upperCapVertices = new ArrayList(25);
        ArrayList<Vertex> upperEdgeVertices = new ArrayList(25);

        int lowerCenterIndex;
        for (lowerCenterIndex = 0; lowerCenterIndex <= 24; ++lowerCenterIndex) {
            float cosTheta = (float) Math.cos((double) theta);
            float sinTheta = (float) Math.sin((double) theta);
            Vector3 lowerPosition = new Vector3(radius * cosTheta, -halfHeight, radius * sinTheta);
            Vector3 normal = (new Vector3(lowerPosition.x, 0.0F, lowerPosition.z)).normalized();
            lowerPosition = Vector3.add(lowerPosition, center);
            Vertex.UvCoordinate uvCoordinate = new Vertex.UvCoordinate(uStep * (float) lowerCenterIndex, 0.0F);
            Vertex vertex = Vertex.builder().setPosition(lowerPosition).setNormal(normal)
                    .setUvCoordinate(uvCoordinate).build();
            vertices.add(vertex);
            vertex = Vertex.builder()
                    .setPosition(lowerPosition)
                    .setNormal(Vector3.down())
                    .setUvCoordinate(new Vertex.UvCoordinate((cosTheta + 1.0F) / 2.0F, (sinTheta + 1.0F) / 2.0F)).build();
            lowerCapVertices.add(vertex);
            Vector3 upperPosition = new Vector3(radius * cosTheta, halfHeight, radius * sinTheta);
            normal = (new Vector3(upperPosition.x, 0.0F, upperPosition.z)).normalized();
            upperPosition = Vector3.add(upperPosition, center);
            uvCoordinate = new Vertex.UvCoordinate(uStep * (float) lowerCenterIndex, 1.0F);
            vertex = Vertex.builder().setPosition(upperPosition).setNormal(normal).setUvCoordinate(uvCoordinate).build();
            upperEdgeVertices.add(vertex);
            vertex = Vertex.builder().setPosition(upperPosition).setNormal(Vector3.up()).setUvCoordinate(new Vertex.UvCoordinate((cosTheta + 1.0F) / 2.0F, (sinTheta + 1.0F) / 2.0F)).build();
            upperCapVertices.add(vertex);
            theta += 0.2617994F;
        }

        vertices.addAll(upperEdgeVertices);
        lowerCenterIndex = vertices.size();
        vertices.add(Vertex.builder().setPosition(Vector3.add(center, new Vector3(0.0F, -halfHeight, 0.0F))).setNormal(Vector3.down()).setUvCoordinate(new Vertex.UvCoordinate(0.5F, 0.5F)).build());
        vertices.addAll(lowerCapVertices);
        int upperCenterIndex = vertices.size();
        vertices.add(Vertex.builder().setPosition(Vector3.add(center, new Vector3(0.0F, halfHeight, 0.0F))).setNormal(Vector3.up()).setUvCoordinate(new Vertex.UvCoordinate(0.5F, 0.5F)).build());
        vertices.addAll(upperCapVertices);
        ArrayList<Integer> triangleIndices = new ArrayList();

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

    public static ModelRenderable createCustomLine(float radius, Vector3 size, Vector3 center, Material material) {
        AndroidPreconditions.checkMinAndroidApiLevel();
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> triangleIndices = new ArrayList<>();
        //StartingCircle
        ArrayList<Vertex> lowerCapVertices = new ArrayList<>();
        ArrayList<Vertex> upperCapVertices = new ArrayList<>();
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

        //Middle Line


        Log.e("triangleIndices", "size:" + triangleIndices.size() + " vertices Size:" + vertices.size());
        Log.e("triangleIndices", "Array:" + triangleIndices.toString());
        Log.e("triangleIndices", "Vertices:" + vertices);

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
