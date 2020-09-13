package com.supermegadinamita;

import com.jogamp.opengl.*;

import nub.core.Graph;
import nub.core.Node;
import nub.core.constraint.AxisPlaneConstraint;
import nub.core.constraint.LocalConstraint;
import nub.primitives.Matrix;
import nub.primitives.Quaternion;
import nub.primitives.Vector;
import nub.processing.Scene;
import processing.core.*;
import processing.event.MouseEvent;
import processing.opengl.PJOGL;
import processing.opengl.PShader;

import static com.jogamp.opengl.GL2GL3.GL_CLIP_DISTANCE0;



public class Main extends PApplet {

    Scene scene, waterScene, reflectScene, refractScene;

    PGraphics refrGraphics, reflGraphics, waterGraphics;
    PShader clipShader, waterShader;


    Node waterNode, rootNode, worldNode, skyNode, terrainNode, treeNode, moonNode, boatNode;

    PJOGL  pgl;
    GL2GL3 gl;

    int w = 800, h = 800;
    boolean constrained = false;
    float offset = 0;

    public void settings(){
        size(w, h, P3D);
    }

    public void setup(){
        rectMode(CENTER);

        // OpenGL config
        hint(ENABLE_OPENGL_ERRORS);
        pgl = (PJOGL) beginPGL();
        gl = pgl.gl.getGL2GL3();
        gl.glEnable(GL_CLIP_DISTANCE0);

        // nub graph scene
        scene = new Scene(this,  w, h);
        pointLight(255,255,255, 0, 400, 0);

        // Load shapes
        PShape moon = loadShape("../models/moon/moon.obj");
        PShape treeShape = loadShape("../models/tree/10446_Palm_Tree_v1_max2010_iteration-2.obj");
        PShape boatShape = loadShape("../models/UFO/Low_poly_UFO.obj");
        PShape mountain = terrainShape(800);

        // Default node: where everything except the water is drawn.
        rootNode = new Node();
        rootNode.setPosition(0,0);

        // Default node: where everything except the water is drawn.
        worldNode = new Node(rootNode);
        worldNode.setPosition(0,0);


        // Sky node:     (Using skybox, textures downloaded from https://imgur.com/a/WSGJ5 and https://www.cleanpng.com/png-skybox-texture-mapping-cube-mapping-desktop-wallpa-6020000/)

        skyNode = new Node(worldNode, skyShape(1200));
        skyNode.setPosition(new Vector(0,0,0));
        //skyNode.enablePicking(0);

        // Moon node  Plain texture, downloaded from SketchFab)

        moonNode = new Node(worldNode, moon);
        moonNode.setPosition(new Vector(0,300,0));
        //moonNode.enablePicking(0);
        moonNode.setOrientation(new Quaternion(0, PI*0.5f,0.75f*PI ));
        moonNode.scale(0.02f);

        // Tree node - Plain texture, downloaded from SketchFab

        treeNode = new Node(worldNode, treeShape);
        treeNode.setOrientation(new Quaternion(-PI/2, 0, 0));
        treeNode.scale(0.3f);
        treeNode.setPosition(new Vector(-150, 0, -150));

        // Boat node - Plain texture, downloaded from SketchFab

        boatNode = new Node(worldNode, boatShape);
        boatNode.scale(1.3f);
        boatNode.setPosition(new Vector( -100, 400, 100));


        // Terrain node: Terrain generation with Perlin Noise, based on Daniel Shiffman's tutorial

        terrainNode = new Node(worldNode, mountain);
        terrainNode.setPosition(new Vector(0,-100,0));
        //terrainNode.enablePicking(0);

        // Water node   (Quad textured with shaders)

        waterNode = new Node(rootNode, waterShape(400, null));
        waterNode.setPosition(new Vector(0,0,0));
        //waterNode.enablePicking(0);


        // Scene configuration

        scene.setBounds(1300);
        scene.rotateEye(PI, 0 , 0);
        scene.eye().setPosition(new Vector(50,100,50));
        scene.eye().setOrientation(new Quaternion(PI,0.25f*PI, 0 ));



        if(constrained){
            AxisPlaneConstraint constraint = new LocalConstraint();
            constraint.setRotationConstraintType(AxisPlaneConstraint.Type.AXIS);
            constraint.setRotationConstraintDirection(new Vector(0,1,0));
            scene.eye().setConstraint(constraint);
        }

        // Shaders

        clipShader = loadShader("shaders/clipFrag.glsl",   "shaders/clipVert.glsl");
        waterShader = loadShader("shaders/waterFrag.glsl", "shaders/waterVert.glsl");

        // DuDv map



        // PGraphics reflection, refraction and final water Buffers
        refrGraphics = createGraphics(w/2, h/2, P3D);
        refrGraphics.shader(clipShader);

        reflGraphics = createGraphics(w/2, h/2, P3D);
        reflGraphics.shader(clipShader);

        waterGraphics = createGraphics(w, h, P3D);
        waterGraphics.shader(waterShader);

        // nub graph scene for water refraction and reflection textures.
        waterScene = new Scene(waterGraphics, worldNode, w, h);
        waterScene.setType(Graph.Type.PERSPECTIVE);

        reflectScene = new Scene(reflGraphics, worldNode);
        reflectScene.setType(Graph.Type.PERSPECTIVE);

        refractScene = new Scene(refrGraphics, worldNode);
        refractScene.setType(Graph.Type.PERSPECTIVE);

        waterScene.setBounds(1300);
        reflectScene.setBounds(1300);
        refractScene.setBounds(1300);

        // Framerate

        frameRate(30);
    }



    public void draw(){


        pushMatrix();
        scene.render(worldNode);

        PImage refractionTexture = refractionTexture(scene);
        PImage reflectionTexture = reflectionTexture(scene);



        waterShader.set("refractionTexture", refractionTexture);
        waterShader.set("reflectionTexture", reflectionTexture);
        waterShader.set("offset", (offset+=0.01f)% 1f);
        waterShader.set("eyePosition", scene.eye().position().x(), scene.eye().position().y(), scene.eye().position().z());

        waterGraphics.beginDraw();
        waterGraphics.clear();
        waterGraphics.endDraw();


        //Scene.render(waterGraphics, Graph.Type.PERSPECTIVE, waterNode, scene.eye(), scene.zNear(), scene.zFar());
        //waterScene.setBounds(scene.zNear(), scene.zFar());
        waterScene.setEye(scene.eye());
        waterScene.render(waterNode);

        //waterNode.setShape(waterShape(400, waterGraphics.get()));







        // 2. Display back buffer
        //scene.displayBackBuffer(0, h / 2);
        //println(frameRate);

        clipShader.set("plane", new float[]{0, 1, 0, 100});
        popMatrix();
        image(waterGraphics.get(),0, 0 , w, h);
        image(reflectionTexture(scene),0, 0 , w/4f, h/4f);
        image(refractionTexture(scene),3f*w/4f, 0 , w/4f, h/4f);


    }



    PImage reflectionTexture(Scene scene){

        reflGraphics.beginDraw();
        reflGraphics.clear();

        //Vector wm = n.worldLocation(n.position());

        Matrix wm = Matrix.multiply(Matrix.inverse(scene.view()), worldNode.worldMatrix());

        PMatrix3D aux = new PMatrix3D(wm.m00(),wm.m01(),wm.m02(),wm.m03(),wm.m10(),wm.m11(),wm.m12(),wm.m13(),wm.m20(),wm.m21(),wm.m22(),wm.m23(),wm.m30(),wm.m31(),wm.m32(),wm.m33());

        clipShader.set("worldMatrix",aux);
        clipShader.set("plane", 0,1, 0, -waterNode.position().y());


        float distance = -(scene.eye().position().y() - waterNode.position().y());

        Vector eyePosition = scene.eye().position();

        scene.eye().setPosition(eyePosition.x() , eyePosition.y() - distance, eyePosition.z());
        Quaternion or = scene.eye().orientation();

        Vector eulerAngles = or.eulerAngles();
        scene.eye().setOrientation(new Quaternion(-eulerAngles.x(), eulerAngles.y(), -eulerAngles.z()));

        //Scene.render(reflGraphics, Graph.Type.PERSPECTIVE, defaultNode, scene.eye() , scene.zNear(), scene.zFar());
        reflectScene.setBounds(scene.zNear(), scene.zFar());
        reflectScene.setEye(scene.eye());

        reflectScene.render(worldNode);

        scene.eye().setOrientation(or);
        scene.eye().setPosition(eyePosition);


        reflGraphics.endDraw();

        PGraphics pg = createGraphics(w, h);
        pg.beginDraw();
        pg.translate(0, h);
        pg.scale(1,-1);
        pg.image( reflGraphics.get(),0, 0, w, h);
        pg.endDraw();

        return pg.get();
    }

    PImage refractionTexture(Scene scene) {

        refrGraphics.beginDraw();
        refrGraphics.clear();
        //Vector wm = n.worldLocation(n.position());

        Matrix wm = Matrix.multiply(Matrix.inverse(scene.eye().view()), worldNode.worldMatrix());


        //Por loop
        PMatrix3D aux = new PMatrix3D(wm.m00(), wm.m01(), wm.m02(), wm.m03(),
                                      wm.m10(), wm.m11(), wm.m12(), wm.m13(),
                                      wm.m20(), wm.m21(), wm.m22(), wm.m23(),
                                      wm.m30(), wm.m31(), wm.m32(), wm.m33());

        clipShader.set("worldMatrix", aux);
        clipShader.set("plane", 0, -1, 0, waterNode.position().y());


        //Scene.render(refrGraphics, Graph.Type.PERSPECTIVE, defaultNode, scene.eye(), scene.zNear(), scene.zFar());

        refractScene.setBounds(scene.zNear(), scene.zFar());
        refractScene.setEye(scene.eye());
        refractScene.render(worldNode);

        refrGraphics.endDraw();

        return refrGraphics.get();
    }



    public void mouseDragged() {
        if (mouseButton == LEFT)
            scene.rotateEye( 0, 0.5f* (pmouseX - mouseX)/w, 0);
        else if (mouseButton == RIGHT)
            scene.rotateEye( 0.5f* (pmouseY - mouseY)/h, 0, 0);
        else {
            scene.mouseSpinTag();
        }
    }

    public void mouseWheel(MouseEvent event) {

        scene.moveForward(event.getCount() * 2);
    }



    public void mouseClicked(){
        if ( mouseButton == RIGHT)
        {
            scene.moveForward(1);
        }

    }
    public void keyPressed(){


        if (key == 's'){

            currentSky = (currentSky + 1) % 12;
            skyNode.setShape(skyShape(1500f));

        }

        else if (key == 't'){

            terrainNode.setShape(terrainShape(800));

        }

        else if( key == 'f'){
            scene.fit();
        }


    }
    int currentSky = 0;
    PShape skyShape(float d){

        int i = currentSky;

        //Sky boxes: https://imgur.com/a/WSGJ5

        PImage skyImg;

        skyImg = loadImage("../data/sky_field_" + i +  ".png");
        if (skyImg == null) skyImg = loadImage("../data/sky_field_" + i +  ".jpg");

        noStroke();
        PShape sky = createShape(GROUP);

//------------------------- TOP ------------------------------

        PShape top = createShape();
        top.beginShape();

        PImage img = skyImg.get(skyImg.width/4 + 1, 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        top.texture(img);
        top.vertex( -d,  d,  d, 0, 0);
        top.vertex(  d,  d,  d, img.width, 0);
        top.vertex(  d,  d, -d,  img.width, img.height);
        top.vertex( -d,  d, -d, 0, img.height);
        top.endShape(CLOSE);
        sky.addChild(top);

//------------------------- FRONT ------------------------------

        PShape front = createShape();
        front.beginShape();

        img = skyImg.get(skyImg.width/4 + 1 , skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        front.texture(img);
        front.vertex( -d,  d, -d,  0, 0);
        front.vertex(  d,  d, -d,  img.width, 0);
        front.vertex(  d, -d, -d,  img.width, img.height);
        front.vertex( -d, -d, -d,  0, img.height);
        front.endShape(CLOSE);
        sky.addChild(front);

        //------------------------- LEFT ------------------------------

        PShape left = createShape();
        left.beginShape();

        img = skyImg.get(1 , skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        left.texture(img);
        left.vertex( -d,  d,  d,  0, 0);
        left.vertex( -d,  d, -d,  img.width, 0);
        left.vertex( -d, -d, -d,  img.width, img.height);
        left.vertex( -d, -d,  d,  0, img.height);
        left.endShape(CLOSE);
        sky.addChild(left);


        //------------------------- RIGHT ------------------------------

        PShape right = createShape();
        right.beginShape();

        img = skyImg.get(2 * skyImg.width/4 , skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        right.texture(img);

        right.vertex(  d,  d, -d,   0, 0);
        right.vertex(  d,  d,  d,  img.width, 0);
        right.vertex(  d, -d,  d,  img.width, img.height);
        right.vertex(  d, -d, -d,  0, img.height);
        right.endShape(CLOSE);
        sky.addChild(right);



        //------------------------- BACK ------------------------------

        PShape back = createShape();
        back.beginShape();

        img = skyImg.get(3 * skyImg.width/4 , skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        back.texture(img);

        back.vertex(  d,  d,  d,   0, 0);
        back.vertex( -d,  d,  d,  img.width, 0);
        back.vertex( -d, -d,  d,  img.width, img.height);
        back.vertex(  d, -d,  d,  0, img.height);
        back.endShape(CLOSE);
        sky.addChild(back);



        //------------------------- BOTTOM ------------------------------

        PShape bottom = createShape();
        bottom.beginShape();

        img = skyImg.get(skyImg.width/4 , 2 * skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        bottom.texture(img);

        bottom.vertex(  -d, -d, -d,   0, 0);
        bottom.vertex(   d, -d, -d,  img.width, 0);
        bottom.vertex(   d, -d,  d,  img.width, img.height);
        bottom.vertex(  -d, -d,  d,  0, img.height);
        bottom.endShape(CLOSE);
        sky.addChild(bottom);

        return sky;
    }

    PShape waterShape(int d, PImage img){

        PShape waterQuad = createShape();

        if (img == null)
            img = loadImage("shaders/dudv.png");

        float h = 0;
        waterQuad.beginShape();
        waterQuad.texture(img);
        waterQuad.vertex( -d,  h,  d, 0, img.height);
        waterQuad.vertex(  d,  h,  d, img.width, img.height);
        waterQuad.vertex(  d,  h, -d, img.width, 0);
        waterQuad.vertex( -d,  h, -d, 0,0);
        waterQuad.endShape(CLOSE);

        return waterQuad;

    }

    PShape terrainShape(int wide){

        noiseSeed((long) random(0, 100));

        int d = 50;
        int levels = 2*d;

        float scale = 0.09f;

        float size = wide/d;
        pushMatrix();
        PImage terrainTexture = loadImage("models/terrain/ground_texture.jpg");

        PShape terrain = createShape(GROUP);

        for (int l = 0; l < levels; l++){
            for (int i = max(0, l - d); i< min(l, d); i++){

                int j = l - i - 1;

                //Back triangle

                if (i - 1 >= 0 && j - 1 >= 0){
                    PShape triangle = createShape();

                    triangle.beginShape();
                    triangle.noStroke();
                    triangle.texture(terrainTexture);

                    triangle.vertex(i *size - wide/2f,     map(noise((i)*scale,   (j)*scale), -1, 1, -0.3f*wide, 0.3f*wide), j*size- wide/2  , map(i  , 0, d, 0, terrainTexture.width),     map(j  , 0, d, 0, terrainTexture.width));
                    triangle.vertex((i-1)*size - wide/2f,  map(noise((i-1)*scale, (j)*scale), -1, 1, -0.3f*wide, 0.3f*wide), j*size - wide/2  , map(i-1, 0, d, 0, terrainTexture.width),     map(j  , 0, d, 0, terrainTexture.width));
                    triangle.vertex(i*size - wide/2f,  map(noise((i)*scale, (j-1)*scale), -1, 1, -0.3f*wide, 0.3f*wide), (j-1)*size - wide/2  , map(i  , 0, d, 0, terrainTexture.width),     map(j-1, 0, d, 0, terrainTexture.width));

                    triangle.endShape(CLOSE);

                    terrain.addChild(triangle);
                }


                //Front triangle
                if( i + 1 < d && j + 1 < d){
                    PShape triangle = createShape();

                    triangle.beginShape();
                    triangle.noStroke();
                    triangle.texture(terrainTexture);

                    triangle.vertex(i*size -wide/2f,    map(noise((i)  *scale, (j)    *scale), -1, 1, -0.3f*wide, 0.3f*wide), j*size  -wide/2    ,map(i  , 0, d, 0, terrainTexture.width), map(j,   0, d, 0, terrainTexture.width));
                    triangle.vertex((i+1)*size -wide/2f,  map(noise((i+1)*scale, (j)  *scale), -1, 1, -0.3f*wide, 0.3f*wide), j*size -wide/2  ,map(i+1, 0, d, 0, terrainTexture.width), map(j,   0, d, 0, terrainTexture.width));
                    triangle.vertex(i*size -wide/2f,  map(noise((i)  *scale, (j+1)*scale), -1, 1, -0.3f*wide, 0.3f*wide), (j+1)*size -wide/2  ,map(i  , 0, d, 0, terrainTexture.width), map(j+1, 0, d, 0, terrainTexture.width));

                    triangle.endShape(CLOSE);
                    terrain.addChild(triangle);
                }
            }
        }
        popMatrix();
        return terrain;

    }




    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "com.supermegadinamita.Main" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}

