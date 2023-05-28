package good.damn.backgroundAnim

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.renderscript.Sampler.Value
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.Random
import kotlin.math.hypot
import kotlin.math.log

class AnimatedBackgroundView : View {

    private val TAG = "AnimatedBackgroundView";

    private val mRandom: Random = Random();

    private val mPaint: Paint = Paint();
    private val mDebugPaint: Paint = Paint();
    private val mAnimator: ValueAnimator = ValueAnimator();

    private var mBeginX: FloatArray = floatArrayOf(0.1f);
    private var mBeginY: FloatArray = floatArrayOf(1.0f);

    private var mProcessX: FloatArray = floatArrayOf(.0f);
    private var mProcessY: FloatArray = floatArrayOf(.0f);

    private var mEndX: FloatArray = floatArrayOf(.0f);
    private var mEndY: FloatArray = floatArrayOf(.0f);

    private var mLengthX: IntArray = intArrayOf(5);
    private var mLengthY: IntArray = intArrayOf(5);

    private var mDensity: Float = 1.0f;

    private var mCountRenders: Int = 100;


    private fun generatePosition(exp: Int): Float {
        return mRandom.nextFloat() * exp;
    }

    private fun createFrame() {
        val linesCount = 100;
        mBeginX = FloatArray(linesCount);
        mBeginY = FloatArray(linesCount);

        mEndX = FloatArray(linesCount);
        mEndY = FloatArray(linesCount);

        mProcessX = FloatArray(linesCount);
        mProcessY = FloatArray(linesCount);

        mLengthX = IntArray(linesCount);
        mLengthY = IntArray(linesCount);
    }

    private fun configFrame() {
        mPaint.strokeWidth = 3.0f * mDensity * mRandom.nextFloat();
        mPaint.strokeWidth += 3.0f;
        val lengthLimit = (mDensity * 35).toInt();

        mCountRenders = linesCount;

        if (mCountRenders <= 0) {
            mAnimator.pause();
            return;
        }

        for (i in 0 until mCountRenders) {
            val lineLength = mRandom.nextInt(lengthLimit) + 5;

            mBeginX[i] = -generatePosition((width * 0.8f).toInt());
            mBeginY[i] = -generatePosition((height * 0.8f).toInt());

            mEndX[i] = width * (1.1f+mRandom.nextFloat());
            mEndY[i] = height * (1.1f+mRandom.nextFloat());

            val xLength = (mEndX[i] - mBeginX[i]).toDouble();
            val yLength = (mEndY[i] - mBeginY[i]).toDouble();
            val h = hypot(xLength, yLength);

            Log.d(TAG, "onLayout: I: $i HYPOTENUSE $h direction: ${xLength / h}");

            mLengthX[i] = (xLength / h * lineLength).toInt();
            mLengthY[i] = (yLength / h * lineLength).toInt();

        }
    }

    var linesCount:Int = 100;

    init {

        mDensity = resources.displayMetrics.density;

        mPaint.style = Paint.Style.STROKE;
        mPaint.color = 0xFFFFFFFF.toInt();
        mPaint.strokeCap = Paint.Cap.ROUND;
        mPaint.strokeWidth = 3.0f;

        mDebugPaint.style = Paint.Style.STROKE;
        mDebugPaint.color = 0xFFFFFF00.toInt();
        mDebugPaint.strokeCap = Paint.Cap.ROUND;
        mDebugPaint.strokeWidth = 3.0f;

        mAnimator.setValues(PropertyValuesHolder.ofFloat("setValues", .0f,1.0f));
        mAnimator.duration = 4000;
        mAnimator.addUpdateListener {
            val frac: Float = mAnimator.animatedValue as Float;
            Log.d(TAG, "addUpdateListener: $frac");
            for (i in mBeginX.indices) {
                mProcessX[i] = mBeginX[i]+(mEndX[i]-mBeginX[i]) * frac;
                mProcessY[i] = mBeginY[i]+(mEndY[i]-mBeginY[i]) * frac;
            }
            invalidate()
        }

        mAnimator.addListener(object : AnimatorListener {
            override fun onAnimationRepeat(p0: Animator) {
                configFrame();
            }
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
        });

        createFrame();

        mAnimator.repeatMode = ValueAnimator.RESTART;
        mAnimator.repeatCount = ValueAnimator.INFINITE;
        mAnimator.startDelay = 1500;
        mAnimator.start();
    }

    constructor(ctx: Context): super(ctx);

    constructor(ctx: Context, attrs: AttributeSet): super(ctx,attrs);

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        configFrame();
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (i in 0 until mCountRenders) {

            //canvas!!.drawLine(mBeginX[i], mBeginY[i], mEndX[i], mEndY[i], mDebugPaint);

            canvas!!.drawLine(mProcessX[i]-mLengthX[i],mProcessY[i]-mLengthY[i],
                mProcessX[i], mProcessY[i], mPaint);
        }
    }

}