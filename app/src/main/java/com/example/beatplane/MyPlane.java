package com.example.beatplane;

import java.util.ArrayList;
import java.util.List;

import com.example.beatplane.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
/*玩家飞机的类*/
public class MyPlane extends GameObject {
	private Bitmap myplane;
	private Bitmap myplane2;
	private Bitmap myplane3;
	private float middle_x;
	private float middle_y;
	private float object_width2;
	private int shielded;
	private boolean phenomenal;
	private List<GameObject> bullets;	//子弹类
	private MainView mainView;
	MyPlane(MainView mainView,Resources resources) {
		super(resources);
		// TODO Auto-generated constructor stub
		initBitmap();
		this.speed = 21;
		this.mainView = mainView;
		bullets = new ArrayList<GameObject>();

	}

	// 初始化数据
	@Override
	public void setScreenWH(float screen_width, float screen_height) {
		super.setScreenWH(screen_width, screen_height);
		object_x = screen_width/2 - object_width/2;
		object_y = screen_height - object_height;
		middle_x = object_x + object_width/2;
		middle_y = object_y + object_height/2;
	}

	// 初始化图片
	@Override
	public void initBitmap() {
		// TODO Auto-generated method stub
		myplane = BitmapFactory.decodeResource(resources, R.drawable.myplane);
		myplane2 = BitmapFactory.decodeResource(resources, R.drawable.myplaneexplosion);
		myplane3 = BitmapFactory.decodeResource(resources, R.drawable.myplane1);
		object_width = myplane.getWidth() / 2; // 获得每一帧位图的宽
		object_width2 = myplane2.getWidth() / 2;
		object_height = myplane.getHeight(); // 获得每一帧位图的高
		shielded = 0;
	}

	// 绘图函数
	@Override
	public void drawSelf(Canvas canvas) {
		// TODO Auto-generated method stub
		if(isAlive){
			int x = (int) (currentFrame * object_width); // 获得当前帧相对于位图的Y坐标
			canvas.save();
			canvas.clipRect(object_x, object_y, object_x + object_width, object_y + object_height);
			if(shielded==0){
				canvas.drawBitmap(myplane, object_x - object_width, object_y, paint);
			}
			else{
				canvas.drawBitmap(myplane, object_x, object_y, paint);
			}
			if(phenomenal){
				canvas.drawBitmap(myplane, object_x - x, object_y, paint);
			}
			canvas.restore();
			currentFrame++;
			if (currentFrame >= 2) {
				currentFrame = 0;
			}
		}
		else{
			int x = (int) (currentFrame * object_width); // 获得当前帧相对于位图的Y坐标
			canvas.save();
			canvas.clipRect(object_x, object_y, object_x + object_width, object_y
					+ object_height);
			canvas.drawBitmap(myplane2, object_x - object_width2 + 10, object_y, paint);
			canvas.restore();
			currentFrame++;
			if (currentFrame >= 2) {
				currentFrame = 1;
			}
		}
	}

	// 释放资源
	@Override
	public void release() {
		// TODO Auto-generated method stub
		for(GameObject obj:bullets){
			obj.release();
		}
		if(!myplane.isRecycled()){
			myplane.recycle();
		}
		if(!myplane2.isRecycled()){
			myplane2.recycle();
		}
		if(!myplane3.isRecycled()){
			myplane3.recycle();
		}
	}

	public int getSpeed(){
		return speed;
	}

	public float getMiddle_x() {
		return middle_x;
	}

	public void setMiddle_x(float middle_x) {
		this.middle_x = middle_x;
		this.object_x = middle_x - object_width/2;
	}

	public float getMiddle_y() {
		return middle_y;
	}

	public void setMiddle_y(float middle_y) {
		this.middle_y = middle_y;
		this.object_y = middle_y - object_height/2;
	}

	public void setAlive(boolean isAlive){
		this.isAlive = isAlive;
	}

	public void setShielded(int isShielded){this.shielded = isShielded;}

	public int getShielded(){
		return shielded;
	}

	public void setPhenomenal(boolean ephemeral){this.phenomenal = ephemeral;}

	public boolean getPhenomenal(){return phenomenal;}
}
