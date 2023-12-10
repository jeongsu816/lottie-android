package com.airbnb.lottie;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class LottieDrawableTestView extends View {
  private static final String TAG = "LottieDrawableTestView";

  private final LottieDrawable lottieDrawable;
  private int width = 0;
  private int height = 0;

  private float ratio = 1.0f;

  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final RectF rectF = new RectF();
  private final RectF mappedRectF = new RectF();

  public LottieDrawableTestView(Context context) {
    this(context, null);
  }

  public LottieDrawableTestView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LottieDrawableTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public LottieDrawableTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    lottieDrawable = new LottieDrawable();
    lottieDrawable.setCallback(this);
    lottieDrawable.setRepeatCount(LottieDrawable.INFINITE);
    lottieDrawable.addAnimatorUpdateListener(animation -> invalidate());

    paint.setStrokeWidth(5.0f);
    paint.setColor(Color.RED);
    paint.setStyle(Paint.Style.STROKE);
  }


  public void setComposition(@NotNull LottieComposition composition) {
    lottieDrawable.setComposition(composition);
  }

  @Override protected void onDraw(@NonNull Canvas canvas) {
    super.onDraw(canvas);

    int count = canvas.save();

    // int width = getMeasuredWidth();
    // int height = getMeasuredHeight();

    int lottieWidth = lottieDrawable.getIntrinsicWidth();
    int lottieHeight = lottieDrawable.getIntrinsicHeight();
    float lottieRatio = (float) lottieWidth / (float) lottieHeight;

/*
    Log.i(TAG, "view:" + width + "x" + height + "(" + ratio + ")"
        + " drawable:" + lottieWidth + "x" + lottieHeight + "(" + lottieRatio + ")");
*/
    int boundsWidth;
    int boundsHeight;
    if (Float.compare(lottieRatio, ratio) > 0) {
      boundsWidth = width;
      boundsHeight = (int) (width / lottieRatio);
    } else {
      boundsWidth = (int) (height / lottieRatio);
      boundsHeight = height;
    }

    lottieDrawable.setBounds(0, 0, boundsWidth, boundsHeight);


    rectF.set(0, 0, boundsWidth, boundsHeight);

    Matrix canvasMatrix = canvas.getMatrix();
    canvasMatrix.mapRect(mappedRectF, rectF);

    float scaleWidth = 300.0f / boundsWidth;
    float scaleHeight = 300.0f / boundsHeight;

    canvas.scale(scaleWidth, scaleHeight);
    canvas.translate(600, 600);

    lottieDrawable.draw(canvas);
    canvas.drawRect(mappedRectF, paint);

    canvas.restoreToCount(count);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    lottieDrawable.playAnimation();
  }

  public void setRenderMode(@NotNull RenderMode renderMode) {
    lottieDrawable.setRenderMode(renderMode);
  }

  @Override public void invalidate() {
    super.invalidate();
    if (lottieDrawable.getRenderMode() == RenderMode.SOFTWARE) {
      // This normally isn't needed. However, when using software rendering, Lottie caches rendered bitmaps
      // and updates it when the animation changes internally.
      // If you have dynamic properties with a value callback and want to update the value of the dynamic property, you need a way
      // to tell Lottie that the bitmap is dirty and it needs to be re-rendered. Normal drawables always re-draw the actual shapes
      // so this isn't an issue but for this path, we have to take the extra step of setting the dirty flag.
      lottieDrawable.invalidateSelf();
    }
  }

  @Override public void invalidateDrawable(@NonNull Drawable dr) {
      // We always want to invalidate the root drawable so it redraws the whole drawable.
      // Eventually it would be great to be able to invalidate just the changed region.
      super.invalidateDrawable(lottieDrawable);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    width = w;
    height = h;
    ratio = (float) w / (float) h;
  }
}
