package net.beerfekt.bouncingbenno;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import net.beerfekt.bouncingbenno.manager.BackgroundManager;
import net.beerfekt.bouncingbenno.manager.MonsterManager;
import net.beerfekt.bouncingbenno.manager.RunTimeManager;
import net.beerfekt.bouncingbenno.objekts.game.Death;
import net.beerfekt.bouncingbenno.objekts.game.Player;
import net.beerfekt.bouncingbenno.objekts.properties.Animation;

import java.util.ArrayList;


public class BouncingBennoView extends SurfaceView implements SurfaceHolder.Callback {
    private RunTimeManager runTimeManager;

    //BackgroundManager
    private Bitmap background_sky;
    private Bitmap background_landscape1;
    private Bitmap background_landscape2;
    private Bitmap background_street;
    private Bitmap background_baum;
    private Bitmap background_haus1;
    private Bitmap background_haus2;
    private Bitmap overlay;

    //Player
    private Bitmap[] benno = new Bitmap[8];

    //MonsterManager
    private Bitmap[] emy = new Bitmap[6];
    private Bitmap[] flo = new Bitmap[4];
    private Bitmap[] hans = new Bitmap[4];
    private Bitmap rio;
    private Bitmap[] rolph = new Bitmap[5];

    //Death Explosion
    private Bitmap[] explosion = new Bitmap[6];

    //start screen
    private Bitmap start;

    public BouncingBennoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);

        //BackgroundManager
        background_sky = getBitmap(R.drawable.ic_himmel);
        background_landscape1 = getBitmap(R.drawable.ic_berge1);
        background_landscape2 = getBitmap(R.drawable.ic_berge2);
        background_street = getBitmap(R.drawable.ic_strasse);
        background_baum = getBitmap(R.drawable.ic_baum);
        background_haus1 = getBitmap(R.drawable.ic_haus);
        background_haus2 = getBitmap(R.drawable.ic_haus2);
        overlay = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_paper_opacity_50);

        //Player
        benno[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_0);
        benno[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_40);
        benno[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_80);
        benno[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_120);
        benno[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_160);
        benno[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_200);
        benno[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_240);
        benno[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_320);


        //MonsterManager
        emy[0] = getBitmap(R.drawable.ic_emy_einhorn);
        emy[1] = getBitmap(R.drawable.ic_emy_einhorn_2);
        emy[2] = getBitmap(R.drawable.ic_emy_einhorn_3);
        emy[3] = getBitmap(R.drawable.ic_emy_einhorn_4);
        emy[4] = getBitmap(R.drawable.ic_emy_einhorn_5);
        emy[5] = getBitmap(R.drawable.ic_emy_einhorn_6);

        flo[0] = getBitmap(R.drawable.ic_flying_flo2);
        flo[1] = getBitmap(R.drawable.ic_flying_flo3);
        flo[2] = getBitmap(R.drawable.ic_flying_flo4);
        flo[3] = getBitmap(R.drawable.ic_flying_flo5);

        hans[0] = getBitmap(R.drawable.ic_hans_horny);
        hans[1] = getBitmap(R.drawable.ic_hans_horny_1);
        hans[2] = getBitmap(R.drawable.ic_hans_horny_3);
        hans[3] = getBitmap(R.drawable.ic_hans_horny_4);

        rio = getBitmap(R.drawable.ic_rio_reisnagel);

        rolph[0] = getBitmap(R.drawable.ic_rolph_ruessel);
        rolph[1] = getBitmap(R.drawable.ic_rolph_ruessel_2);
        rolph[2] = getBitmap(R.drawable.ic_rolph_ruessel_3);
        rolph[3] = getBitmap(R.drawable.ic_rolph_ruessel_4);
        rolph[4] = getBitmap(R.drawable.ic_rolph_ruessel_5);

        explosion[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_platzt1);
        explosion[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_platzt2);
        explosion[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_platzt3);
        explosion[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_platzt4);
        explosion[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_platzt5);
        explosion[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.benno_platzt5);

        start = BitmapFactory.decodeResource(context.getResources(), R.drawable.start);
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (this) {
            //BackgroundManager
            ArrayList<Bitmap> backgroundObjects = new ArrayList<>();
            backgroundObjects.add(background_baum);
            backgroundObjects.add(background_haus1);
            backgroundObjects.add(background_haus2);
            BackgroundManager backgroundManager = new BackgroundManager(overlay ,background_sky, background_landscape1, background_landscape2, background_street, backgroundObjects);

            //MonsterManager
            ArrayList<Bitmap> monster = new ArrayList<>();
            monster.add(rio);
            MonsterManager monsterManager = new MonsterManager(monster, emy, flo, hans, rolph);

            //Player
            //Animation roll = new Animation(benno, 100);
            Animation roll = new Animation(benno, 100);
            Player player = new Player(145.6f,132.6f, roll);
            Animation dead = new Animation(explosion, 20, true);
            Death death = new Death(204f,102.6f, dead);


            runTimeManager = new RunTimeManager(holder, this, backgroundManager, player, monsterManager, death, start);
            runTimeManager.startGame();
            runTimeManager.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this) {
            if (runTimeManager != null) {
                runTimeManager.stopGame();
            }
            runTimeManager = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return runTimeManager.onTouchEvent();
        }
        return false;
    }
}