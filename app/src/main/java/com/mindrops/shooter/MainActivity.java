package com.mindrops.shooter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ColorSpace;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Scene scene;
    private Camera camera;
    private ModelRenderable bullet;
    private boolean shouldStartTimer = true;
    private int targetsLeft = 20;
    private Point point;
    private TextView targetsRemaining;
    private SoundPool soundPool;
    private int sound;
    LocalStorage localStorage = new LocalStorage(MainActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getRealSize(point);
        setContentView(R.layout.activity_main);
        loadSoundPool();
        targetsRemaining = findViewById(R.id.count_Text);
        CustomArFragment customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        scene = customArFragment.getArSceneView().getScene();
        camera = scene.getCamera();
        addShipsToScreen();
        buildLasers();
        FrameLayout shoot = findViewById(R.id.shootbutton);
        shoot.setOnClickListener(view -> {
            if (shouldStartTimer) {
                startTimer();
                shouldStartTimer = false;
            }
            shootBullets();
        });
    }

    private void loadSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_GAME).build();
        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();
        sound = soundPool.load(MainActivity.this, R.raw.laser_gun, 1);
    }

    private void startTimer() {
        TextView timer = findViewById(R.id.timer_Text);
        new Thread(() -> {
            int seconds = 0;
            while (targetsLeft > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seconds++;
                int minutesPassed = seconds / 60;
                int secondsPassed = seconds % 60;
                localStorage.setTime(minutesPassed + " : " + secondsPassed);
                runOnUiThread(() -> timer.setText(minutesPassed + " : " + secondsPassed)
                );
            }
        }).start();

    }

    private void shootBullets() {
        Ray ray = camera.screenPointToRay(point.x / 2f, point.y / 2f);
        Node node = new Node();
        node.setRenderable(bullet);
        scene.addChild(node);
        new Thread(() -> {
            for (int i = 0; i < 200; i++) {
                int finalI = i;
                runOnUiThread(() -> {
                    Vector3 vector3 = ray.getPoint(finalI * 0.1f);
                    node.setWorldPosition(vector3);
                    Node nodeInContact = scene.overlapTest(node);
                    if (nodeInContact != null) {
                        targetsLeft--;
                        targetsRemaining.setText("Score: " + (20 - targetsLeft));
                        scene.removeChild(nodeInContact);
                        soundPool.play(sound, 1f, 1f, 1, 0, 1f);

                    }
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(() -> {
                        scene.removeChild(node);
                        if (targetsLeft == 0) {
                            Toast.makeText(this, "Hooray ! You saved the earth !!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
            );

        }).start();
    }

    private void addShipsToScreen() {
        ModelRenderable.builder().setSource(MainActivity.this, Uri.parse("model.sfb")).build()
                .thenAccept(renderable -> {
                    for (int i = 0; i < 20; i++) {
                        Node node = new Node();
                        node.setRenderable(renderable);
                        scene.addChild(node);
                        Random random = new Random();
                        int x = random.nextInt(10);
                        int z = random.nextInt(10);
                        int y = random.nextInt(20);
                        z = -z;
                        node.setWorldPosition(new Vector3((float) x, (float) y / 10f, (float) z));
                    }


                });
    }

    private void buildLasers() {
        Texture.builder().setSource(MainActivity.this, R.drawable.texture).build()
                .thenAccept(texture -> {
                    MaterialFactory.makeOpaqueWithTexture(MainActivity.this, texture).thenAccept(material -> {
                        bullet = ShapeFactory.makeSphere(0.01f, new Vector3(0f, 0f, 0f), material);
                    });
                });
    }
}
