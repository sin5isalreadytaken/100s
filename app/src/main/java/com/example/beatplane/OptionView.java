package com.example.beatplane;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/*游戏开始前的界面*/
public class OptionView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private Bitmap background;
    private Bitmap soundsOpen;
    private Bitmap fly;
    private Bitmap soundsOff;
    private Canvas canvas;			// 画布资源
    private Paint paint; 			// 画笔
    private SurfaceHolder sfh;
    private Thread thread;			// 绘图线程
    private int currentFrame;
    private float fly_x;
    private float fly_y;
    private float fly_height;
    private double button_x1 = 0.115;
    private double button_x2 = 0.055;
    private double button_y = 0.055;
    private double button_y1 = 0.46;
    private double button_y2 = 0.61;
    private double button_y3 = 0.89;
    private double button_ys = 0.73;
    private float object_x;
    private float scalex;
    private float scaley;
    private float screen_width;
    private float screen_height;
    private boolean threadFlag;
    private boolean soundsOn;
    private boolean goodsOn;
    private MainActivity mainActivity;
    private GameSoundPool sounds;
    public OptionView(Context context,boolean goodsOno,boolean soundsOno) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mainActivity = (MainActivity) context;
        soundsOn = soundsOno;
        goodsOn = goodsOno;
        sounds = new GameSoundPool(mainActivity);
        sounds.setOpen(soundsOn);
        sounds.initSysSound();
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        thread = new Thread(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        screen_width = this.getWidth();
        screen_height = this.getHeight();
        initBitmap(); // 初始化图片资源
        threadFlag = true;
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        release();
        threadFlag = false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1){
            float x = event.getX();
            float y = event.getY();
            if(x > screen_width/2 - button_x1*screen_width && x < screen_width/2 + button_x1*screen_width
                    && y > button_y1*screen_height && y < (button_y1 + button_y)*screen_height)
            {
                sounds.playSound(1, 0);
                goodsOn = true;
                drawSelf();
            }
            else if(x > screen_width/2 - button_x1*screen_width && x < screen_width/2 + button_x1*screen_width
                    && y > button_y2*screen_height && y < (button_y2 + button_y)*screen_height)
            {
                sounds.playSound(1, 0);
                goodsOn = false;
                drawSelf();
            }
            else if(x > object_x && x < screen_width - object_x
                    && y > button_ys*screen_height && y < button_ys*screen_height + screen_width -object_x*2)
            {
                sounds.playSound(1, 0);
                soundsOn = !soundsOn;
                drawSelf();
            }
            else if(x > screen_width/2 - button_x2*screen_width && x < screen_width/2 + button_x2*screen_width
                    && y > button_y3*screen_height && y < (button_y3 + button_y)*screen_height)
            {
                sounds.playSound(1, 0);
                drawSelf();
                mainActivity.toReadyView(goodsOn,soundsOn);
            }
            return true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            //isBtChange = false;
            //isBtChange2 = false;
            return true;
        }
        return false;
    }
    // 初始化图片
    public void initBitmap() {
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg_op);
        fly = BitmapFactory.decodeResource(getResources(), R.drawable.fly);
        soundsOpen = BitmapFactory.decodeResource(getResources(), R.drawable.soundson);
        soundsOff = BitmapFactory.decodeResource(getResources(), R.drawable.soundsoff);
        scalex = screen_width / background.getWidth();
        scaley = screen_height / background.getHeight();
        fly_x = (float)screen_width/2 - fly.getWidth()/2/scalex - (float)button_x1*screen_width;
        fly_height = fly.getHeight()/3;
        object_x = screen_width/2 - soundsOff.getWidth()/2;
    }
    // 绘图函数
    public void drawSelf() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawColor(Color.BLACK); // 绘制背景色
            canvas.save();
            // 计算背景图片与屏幕的比例
            canvas.scale(scalex, scaley, 0, 0);
            canvas.drawBitmap(background, 0, 0, paint);   // 绘制背景图
            canvas.restore();
            if (goodsOn)
                fly_y = (float) button_y1 * screen_height;
            else
                fly_y = (float) button_y2 * screen_height;
            canvas.save();
            canvas.clipRect(fly_x, fly_y, fly_x + fly.getWidth(), fly_y + fly_height);
            canvas.drawBitmap(fly, fly_x, fly_y, paint);
            canvas.restore();
            float sounds_x = object_x;
            float sounds_y = (float) button_ys * screen_height;
            canvas.save();
            if (soundsOn) {
                canvas.drawBitmap(soundsOpen, sounds_x, sounds_y, paint);
            } else {
                canvas.drawBitmap(soundsOff, sounds_x, sounds_y, paint);
            }
            canvas.restore();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }
    public void release(){
        if(!background.isRecycled()){
            background.recycle();
        }
        if(!soundsOff.isRecycled()){
            soundsOff.recycle();
        }
        if(!soundsOpen.isRecycled()){
            soundsOpen.recycle();
        }
        if(!fly.isRecycled()){
            fly.recycle();
        }
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (threadFlag) {
            long startTime = System.currentTimeMillis();
            drawSelf();
            long endTime = System.currentTimeMillis();
            try {
                if (endTime - startTime < 500)
                    Thread.sleep(500 - (endTime - startTime));
            } catch (InterruptedException err) {
                err.printStackTrace();
            }
        }
    }
}
