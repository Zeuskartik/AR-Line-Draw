package com.mindrops.sixfeet;

import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.RenderableDefinition;
import com.google.ar.sceneform.rendering.Vertex;
import com.google.ar.sceneform.utilities.AndroidPreconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CustomShapeFactory {

    public static ModelRenderable createCube(Vector3 size, Vector3 center, Material material) {
        AndroidPreconditions.checkMinAndroidApiLevel();
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
        ArrayList<Integer> triangleIndices = new ArrayList(36);

        for(int i = 0; i < 6; ++i) {
            triangleIndices.add(3 + 4 * i);
            triangleIndices.add(1 + 4 * i);
            triangleIndices.add(0 + 4 * i);
            triangleIndices.add(3 + 4 * i);
            triangleIndices.add(2 + 4 * i);
            triangleIndices.add(1 + 4 * i);
        }

        RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder().setTriangleIndices(triangleIndices).setMaterial(material).build();
        RenderableDefinition renderableDefinition = RenderableDefinition.builder().setVertices(vertices).setSubmeshes(Arrays.asList(submesh)).build();
        CompletableFuture future = ((ModelRenderable.Builder)ModelRenderable.builder().setSource(renderableDefinition)).build();

        ModelRenderable result;
        try {
            result = (ModelRenderable)future.get();
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
        ArrayList<Vertex> vertices = new ArrayList(602);
        float pi = 3.1415927F;
        float doublePi = pi * 2.0F;

        int stack;
        int v;
        for(stack = 0; stack <= 24; ++stack) {
            float phi = pi * (float)stack / 24.0F;
            float sinPhi = (float)Math.sin((double)phi);
            float cosPhi = (float)Math.cos((double)phi);

            for(v = 0; v <= 24; ++v) {
                float theta = doublePi * (float)(v == 24 ? 0 : v) / 24.0F;
                float sinTheta = (float)Math.sin((double)theta);
                float cosTheta = (float)Math.cos((double)theta);
                Vector3 position = (new Vector3(sinPhi * cosTheta, cosPhi, sinPhi * sinTheta)).scaled(radius);
                Vector3 normal = position.normalized();
                position = Vector3.add(position, center);
                Vertex.UvCoordinate uvCoordinate = new Vertex.UvCoordinate(1.0F - (float)v / 24.0F, 1.0F - (float)stack / 24.0F);
                Vertex vertex = Vertex.builder().setPosition(position).setNormal(normal).setUvCoordinate(uvCoordinate).build();
                vertices.add(vertex);
            }
        }

        stack = vertices.size();
        int numTriangles = stack * 2;
        int numIndices = numTriangles * 3;
        ArrayList<Integer> triangleIndices = new ArrayList(numIndices);
        v = 0;

        for(stack = 0; stack < 24; ++stack) {
            for(int slice = 0; slice < 24; ++slice) {
                boolean topCap = stack == 0;
                boolean bottomCap = stack == 23;
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

            v += 25;
        }

        RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder().setTriangleIndices(triangleIndices).setMaterial(material).build();
        RenderableDefinition renderableDefinition = RenderableDefinition.builder().setVertices(vertices).setSubmeshes(Arrays.asList(submesh)).build();
        CompletableFuture future = ((ModelRenderable.Builder)ModelRenderable.builder().setSource(renderableDefinition)).build();

        ModelRenderable result;
        try {
            result = (ModelRenderable)future.get();
        } catch (InterruptedException | ExecutionException var20) {
            throw new AssertionError("Error creating renderable.", var20);
        }

        if (result == null) {
            throw new AssertionError("Error creating renderable.");
        } else {
            return result;
        }
    }


/*    public static ModelRenderable createCustomeLine(Vector3 lineSize, Vector3 lineCenter, Material lineMaterial,
                                                    float radius1, Vector3 center1, Material material1,
                                                    float radius2, Vector3 center2, Material material2) {


        AndroidPreconditions.checkMinAndroidApiLevel();

        ArrayList<Vertex> vertices = new ArrayList(602);
        float pi = 3.1415927F;
        float doublePi = pi * 2.0F;

        int stack;
        int v;
        for(stack = 0; stack <= 24; ++stack) {
            float phi = pi * (float)stack / 24.0F;
            float sinPhi = (float)Math.sin((double)phi);
            float cosPhi = (float)Math.cos((double)phi);

            for(v = 0; v <= 24; ++v) {
                float theta = doublePi * (float)(v == 24 ? 0 : v) / 24.0F;
                float sinTheta = (float)Math.sin((double)theta);
                float cosTheta = (float)Math.cos((double)theta);
                Vector3 position = (new Vector3(sinPhi * cosTheta, cosPhi, sinPhi * sinTheta)).scaled(radius1);
                Vector3 normal = position.normalized();
                position = Vector3.add(position, center1);
                Vertex.UvCoordinate uvCoordinate = new Vertex.UvCoordinate(1.0F - (float)v / 24.0F, 1.0F - (float)stack / 24.0F);
                Vertex vertex = Vertex.builder().setPosition(position).setNormal(normal).setUvCoordinate(uvCoordinate).build();
                vertices.add(vertex);
            }
        }


        Vector3 extents = lineSize.scaled(0.5F);
        Vector3 p0 = Vector3.add(lineCenter, new Vector3(-extents.x, -extents.y,  extents.z));
        Vector3 p1 = Vector3.add(lineCenter, new Vector3( extents.x, -extents.y,  extents.z));
        Vector3 p2 = Vector3.add(lineCenter, new Vector3( extents.x, -extents.y, -extents.z));
        Vector3 p3 = Vector3.add(lineCenter, new Vector3(-extents.x, -extents.y, -extents.z));
        Vector3 p4 = Vector3.add(lineCenter, new Vector3(-extents.x,  extents.y,  extents.z));
        Vector3 p5 = Vector3.add(lineCenter, new Vector3( extents.x,  extents.y,  extents.z));
        Vector3 p6 = Vector3.add(lineCenter, new Vector3( extents.x,  extents.y, -extents.z));
        Vector3 p7 = Vector3.add(lineCenter, new Vector3(-extents.x,  extents.y, -extents.z));

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
        ArrayList<Integer> triangleIndices = new ArrayList(36);

        for(int i = 0; i < 6; ++i) {
            triangleIndices.add(3 + 4 * i);
            triangleIndices.add(1 + 4 * i);
            triangleIndices.add(0 + 4 * i);
            triangleIndices.add(3 + 4 * i);
            triangleIndices.add(2 + 4 * i);
            triangleIndices.add(1 + 4 * i);
        }

        RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder().setTriangleIndices(triangleIndices).setMaterial(material).build();
        RenderableDefinition renderableDefinition = RenderableDefinition.builder().setVertices(vertices).setSubmeshes(Arrays.asList(submesh)).build();
        CompletableFuture future = ((ModelRenderable.Builder)ModelRenderable.builder().setSource(renderableDefinition)).build();

        ModelRenderable result;
        try {
            result = (ModelRenderable)future.get();
        } catch (InterruptedException | ExecutionException var32) {
            throw new AssertionError("Error creating renderable.", var32);
        }

        if (result == null) {
            throw new AssertionError("Error creating renderable.");
        } else {
            return result;
        }
    }*/

}
