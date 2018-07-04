package net.beerfekt.bouncingbenno.manager;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;

import net.beerfekt.bouncingbenno.BouncingBennoView;
import net.beerfekt.bouncingbenno.R;
import net.beerfekt.bouncingbenno.objekts.AbstractObject;
import net.beerfekt.bouncingbenno.objekts.game.Player;

import java.util.ArrayList;

public class RunTimeManager extends Thread {


    public final static float SCREEN_WIDTH = 1920, SCREEN_HEIGHT = 1080;
    public final static Rect SCREEN_RECT = new Rect(0, 0, (int) SCREEN_WIDTH, (int) SCREEN_HEIGHT);

    private SurfaceHolder surfaceHolder;
    private BouncingBennoView bouncingBennoView;

    //Attributes for method run
    private long msPerFrame = 1000 / 25;
    private boolean running;
    private int fpsSamples[] = new int[50];
    private int samplePos = 0;
    private int samplesSum = 0;

    //Game Objects
    public BackgroundManager backgroundManager;
    public Player player;
    public MonsterManager monsterManager;

    private String renderedScoreString;
    private int renderedScore;
    private Paint paint;
    public boolean newGameCreated;

    private boolean collision = false;

    public RunTimeManager(SurfaceHolder surfaceHolder, BouncingBennoView gamePanel, BackgroundManager backgroundManager, Player player, MonsterManager monsterManager) {
        this.surfaceHolder = surfaceHolder;
        this.bouncingBennoView = gamePanel;
        this.backgroundManager = backgroundManager;
        this.player = player;
        this.monsterManager = monsterManager;
        paint = getPaint(gamePanel);
    }

    @Override
    public void run() {
        Canvas canvas = null;
        long thisFrameTime;
        long lastFrameTime = System.currentTimeMillis();
        float framesSinceLastFrame;

        while (running) {
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas == null) continue;
                synchronized (surfaceHolder) {
                    draw(canvas);
                }

                if (player.isDied() && player.getAnimation().wasPlayedOnce()){
                    monsterManager.removeAllMonster();
                    player.revive();
                    player.setPlaying(false);
                } else {

                    thisFrameTime = System.currentTimeMillis();
                    framesSinceLastFrame = (float) (thisFrameTime - lastFrameTime) / msPerFrame;

                    float fps = 1000.0f / ((float) samplesSum / fpsSamples.length);
                    canvas.drawText(String.format("FPS: %f", fps), 10, 10, new Paint());

                    update(framesSinceLastFrame);

                    thisFrameTime = System.currentTimeMillis();
                    int timeDelta = (int) (thisFrameTime - lastFrameTime);

                    samplesSum -= fpsSamples[samplePos];
                    fpsSamples[samplePos++] = timeDelta;
                    samplesSum += timeDelta;
                    samplePos %= fpsSamples.length;

                    lastFrameTime = thisFrameTime;

                    if (timeDelta < msPerFrame)
                        sleep(msPerFrame - timeDelta);

                }
            } catch (InterruptedException e) {

            } finally {
                if (canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void draw(Canvas canvas) {
        float scaleFactorX = bouncingBennoView.getWidth() / (SCREEN_WIDTH * 1.f);
        float scaleFactorY = bouncingBennoView.getHeight() / (SCREEN_HEIGHT * 1.f);
        canvas.scale(scaleFactorX, scaleFactorY);

        canvas.drawColor(Color.WHITE);
        backgroundManager.draw(canvas);
        monsterManager.draw(canvas);
        player.draw(canvas);

        checkForCollision(monsterManager.getOnScreenMonster(), canvas);

        int score = player.getScore();
        if (score % 20 == 0) {
            if (score != this.renderedScore || this.renderedScoreString == null) {
                this.renderedScore = score;
                this.renderedScoreString = Integer.toString(this.renderedScore);
            }
        }
        canvas.drawText("Score: " + this.renderedScoreString, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 8, paint);
    }

    private Paint getPaint(BouncingBennoView view) {
        Paint paint = new Paint();
        AssetManager assetManager = view.getContext().getAssets();
        Typeface army = Typeface.createFromAsset(assetManager, "fonts/army_rust.ttf");
        paint.setTypeface(army);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        return paint;
    }

    public void update(float numberOfFrames) {
        if (player.getPlaying()) {
            backgroundManager.update(numberOfFrames);
            monsterManager.update(numberOfFrames);
            player.update(numberOfFrames);
        } else {
            newGameCreated = false;
        }
    }

    public void newGame() {
        player.resetDY();
        player.resetScore();
        this.renderedScoreString = Integer.toString(player.getScore());
        player.resetStartPosition();
        newGameCreated = true;
    }

    public void stopGame() {
        running = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        running = true;
        start();
    }

    public boolean onTouchEvent() {
        if (!player.getPlaying() && !newGameCreated) {
            newGame();
            player.setPlaying(true);
        }
        player.setUpTrue();
        return true;
    }

    private boolean collision(AbstractObject a, AbstractObject b) {
        Rect collisionBoxA = a.getHitbox();
        Rect collisionBoxB = b.getHitbox();

        if (Rect.intersects(collisionBoxA, collisionBoxB)) {
            return true;
        }
        return false;
    }

    private <T extends AbstractObject> void checkForCollision(ArrayList<T> objects, Canvas canvas)  {
        for (int i = 0; i < objects.size(); i++) {
            if (collision(objects.get(i), player)) {
                player.deathAnimation();
                break;
            }
        }
    }
}