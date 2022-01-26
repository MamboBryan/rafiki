package com.mambo.rafiki.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mambo.rafiki.R;
import com.mambo.rafiki.utils.ParedViewUtils;

public class ParedView extends View {
    public static final int defaultMaxWidth = 500;
    public static final int defaultMaxHeight = 50;

    private Context mContext;
    private Paint paint_left;//进度左边画笔
    private Paint paint_right;//进度右边画笔
    private Paint paint_left_top_text;//进度左边上部分文字
    private Paint paint_right_top_text;//进度右边上部分文字
    private Paint paint_bottom_text;//进度下边文字
    private double maxCount = 100;//最大值
    private double sussicCount = 0;//成功值
    private int maxWidth;//最大宽度
    private int maxHeight;//最大高度
    private int paredheight = 10;//默认进度条的高度
    private float fontSize = 14f;//文本大小
    private String bottomText = "";
    private String top_lable_unit = "points";//顶部文字显示单位
    private int sussicColor = Color.YELLOW;//成功进度颜色
    private int no_sussiColor = Color.GRAY;//剩余进度颜色
    private int fontColor = Color.GRAY;//字体颜色
    private int bottomTextMargBotm = 10;//底部文字距离底部的距离
    private int paredMargBotm = 10;//进度条距离底部的距离
    private int labtextMargBotm = 20;//文本距离进度条的距离
    private int bottomTextMargRight = 10;//底部文字距离右边距离
    private boolean isAnimation = true;

    public ParedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ParedView(Context context) {
        super(context);
        initView(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            //获取自定义属性
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Paredview);
            //获取sussic颜色
            sussicColor = a.getColor(R.styleable.Paredview_sussic_color, Color.YELLOW);
            no_sussiColor = a.getColor(R.styleable.Paredview_no_sussic_color, Color.GRAY);
            fontSize = a.getDimensionPixelSize(R.styleable.Paredview_label_text_size, 50);
            fontColor = a.getColor(R.styleable.Paredview_label_text_color, Color.GRAY);
            paredheight = a.getDimensionPixelSize(R.styleable.Paredview_pared_height, 10);
            bottomText = a.getString(R.styleable.Paredview_bottom_text);
            top_lable_unit = a.getString(R.styleable.Paredview_unit_text);

            //最后记得将TypedArray对象回收
            a.recycle();
        }
        mContext = context;
        //初始化左边视图画笔
        paint_left = new Paint();
        paint_left.setColor(sussicColor);
        paint_left.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        paint_left.setStrokeWidth(5f);         //设置画笔宽度为5
        //初始化右边视图画笔
        paint_right = new Paint();
        paint_right.setColor(no_sussiColor);
        paint_right.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        paint_right.setStrokeWidth(5f);         //设置画笔宽度为5

        //初始化文本大小
        paint_bottom_text = new Paint();
        paint_bottom_text.setColor(fontColor);
        paint_bottom_text.setTextSize(fontSize);

        //初始成功文本画笔
        paint_left_top_text = new Paint();
        paint_left_top_text.setColor(sussicColor);
        paint_left_top_text.setTextSize(fontSize);

        //初始未完成文本画笔
        paint_right_top_text = new Paint();
        paint_right_top_text.setColor(no_sussiColor);
        paint_right_top_text.setTextSize(fontSize);
    }

    public double getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(double maxCount) {
        this.maxCount = maxCount;
    }

    public double getSussicCount() {
        return sussicCount;
    }

    public void setSussicCount(double sussicCount) {
        this.sussicCount = sussicCount;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public String getTop_lable_unit() {
        return top_lable_unit;
    }

    public void setTop_lable_unit(String top_lable_unit) {
        this.top_lable_unit = top_lable_unit;
    }

    public void upSussicCount(double sussicCount, double maxCount) {
        this.sussicCount = sussicCount;
        this.maxCount = maxCount;
        invalidate();
    }

    public void upView(double sussicCount, double noSussicCount) {
        this.sussicCount = sussicCount;
        this.maxCount = sussicCount + noSussicCount;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量试图大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        //根据模式进行大小设置
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED: //如果没有指定大小，就设置为默认大小
                maxWidth = defaultMaxWidth;
                break;

            case MeasureSpec.AT_MOST: //如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                maxWidth = widthSize;
                break;

            case MeasureSpec.EXACTLY: //如果是固定的大小，那就不要去改变它
                maxWidth = widthSize;
                break;

        }
        //通过计算得到最大高度
        //计算底部文字高度
        Rect rect = new Rect();
        paint_bottom_text.getTextBounds(bottomText, 0, bottomText.length(), rect);
        int height = rect.height();//文本的高度
        int bottom_height = height + bottomTextMargBotm;
        //进度高度
        int parH = paredheight + paredMargBotm;
        int lab_H = bottom_height + labtextMargBotm;
        maxHeight = bottom_height + parH + lab_H;

        //设置控件大小
        setMeasuredDimension(maxWidth, maxHeight);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //开始画视图
        //1 绘制 底部文字
        Rect rect = new Rect();
        paint_bottom_text.getTextBounds(bottomText, 0, bottomText.length(), rect);
        int width = rect.width();//文本的宽度
        int height = rect.height();//文本的高度
        canvas.drawText(bottomText, maxWidth - width - bottomTextMargRight, maxHeight - bottomTextMargBotm, paint_bottom_text);
        int bottom_height = height + bottomTextMargBotm;


        //绘制进度对比进度条

        double unitw = maxWidth / maxCount;//得到单位长度
        Log.d("onDraw", "unitw=" + unitw);
        int leftW = (int) (unitw * sussicCount);
        Log.d("onDraw", "leftW=" + leftW);
        int rect_top = maxHeight - height - bottomTextMargBotm - paredheight - paredMargBotm;
        int rect_botm = maxHeight - height - bottomTextMargBotm - paredMargBotm;
        Rect rect_sussic = new Rect(0, rect_top, leftW, rect_botm);
        canvas.drawRect(rect_sussic, paint_left);
        Rect rect_right = new Rect(leftW, rect_top, maxWidth, rect_botm);
        canvas.drawRect(rect_right, paint_right);
        int parH = paredheight + paredMargBotm;

        //绘制上部文字
        //1 sussic=100%
        if (sussicCount == maxCount) {
            String sussic_text = ParedViewUtils.doubleTrans1(sussicCount) + top_lable_unit;
            Rect rect2 = new Rect();
            paint_left_top_text.getTextBounds(sussic_text, 0, sussic_text.length(), rect2);
            width = rect2.width();//文本的宽度
            //将文本画到进度条上方 并居中
            float sussic_x = leftW / 2 - width / 2;
            float suusic_y = maxHeight - parH - bottom_height - labtextMargBotm;
            canvas.drawText(sussic_text, sussic_x, suusic_y, paint_left_top_text);

        } else if (sussicCount != 0) {
            //画文字
            //判断是否可以转化为整数

            String sussic_text = ParedViewUtils.doubleTrans1(sussicCount) + top_lable_unit;
            String nosussic_text = ParedViewUtils.doubleTrans1(maxCount - sussicCount) + top_lable_unit;
            Rect rect2 = new Rect();
            paint_left_top_text.getTextBounds(sussic_text, 0, sussic_text.length(), rect2);
            int width_sussic = rect2.width();//文本的宽度
            //将文本画到进度条上方 并居中
            float sussic_x = leftW / 2 - width_sussic / 2;
            float suusic_y = maxHeight - parH - bottom_height - labtextMargBotm;


            Rect rect3 = new Rect();
            paint_right_top_text.getTextBounds(nosussic_text, 0, nosussic_text.length(), rect3);
            int no_sussic_width = rect3.width();//文本的宽度

            float no_sussic_x = leftW + (maxWidth - leftW) / 2 - no_sussic_width / 2;
            //判断是否能够完成绘制文字
            if (leftW / 2 >= width_sussic) {
                canvas.drawText(sussic_text, sussic_x, suusic_y, paint_left_top_text);
            } else {
                //画在最左侧
                canvas.drawText(sussic_text, 0 + bottomTextMargRight, suusic_y, paint_left_top_text);
            }
            if ((maxWidth - leftW) / 2 >= no_sussic_width) {
                canvas.drawText(nosussic_text, no_sussic_x, suusic_y, paint_right_top_text);
            } else {
                //画在最右侧
                canvas.drawText(nosussic_text, maxWidth - bottomTextMargRight - no_sussic_width, suusic_y, paint_right_top_text);
            }


        } else {
            String nosussic_text = ParedViewUtils.doubleTrans1(maxCount - sussicCount) + top_lable_unit;
            Rect rect2 = new Rect();
            paint_right_top_text.getTextBounds(nosussic_text, 0, nosussic_text.length(), rect2);
            width = rect2.width();//文本的宽度
            //将文本画到进度条上方 并居中
            float sussic_x = maxWidth / 2 - width / 2;
            float suusic_y = maxHeight - parH - bottom_height - labtextMargBotm;
            canvas.drawText(nosussic_text, sussic_x, suusic_y, paint_right_top_text);
        }


    }
}
