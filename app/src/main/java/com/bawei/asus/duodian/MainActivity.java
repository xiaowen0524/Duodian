package com.bawei.asus.duodian;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    ImageView imageView, image;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 3;

    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private int mode = NONE;
    private float oldDist;
    private Matrix matrix = new Matrix();
    private boolean isFirst = false;
    private float scale;
    private float tempWidth, tempHeight, imageWidth, imageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutParams pl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(pl);
        imageView=(ImageView)findViewById(R.id.imageView);
        image=new ImageView(this);

        image.setLayoutParams(pl);
        image.setScaleType(ImageView.ScaleType.MATRIX);
        image.setImageResource(R.drawable.ic_launcher);
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        if (isFirst) {
                            tempWidth *= scale;
                            tempHeight *= scale;

                            if (tempWidth < imageWidth || tempHeight < imageHeight) {
                                matrix.postScale(imageWidth / tempWidth,
                                        imageHeight / tempHeight, mid.x, mid.y);

                                tempWidth = imageWidth;
                                tempHeight = imageHeight;
                            }

                        }
                        isFirst = false;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - start.x,
                                    event.getY() - start.y);
                        } else
                        if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                scale = newDist / oldDist;
                                System.out.println("scale : 缩放大小：" + scale);
                                matrix.postScale(scale, scale, mid.x, mid.y);
                                isFirst = true;
                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);
                return true;
            }

            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });
        ll.addView(image);
        this.setContentView(ll);
    }
}
