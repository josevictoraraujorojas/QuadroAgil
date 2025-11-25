package com.example.quadroagil.ui.projeto

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.quadroagil.R

class DonutView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var percentage: Float = 0f
    private var label: String = ""

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    private val rect = RectF()

    init {
        backgroundPaint.style = Paint.Style.STROKE
        progressPaint.style = Paint.Style.STROKE

        backgroundPaint.color = ContextCompat.getColor(context, R.color.donut_bg)
        progressPaint.color = ContextCompat.getColor(context, R.color.donut_progress)
        textPaint.color = ContextCompat.getColor(context, R.color.donut_text)
        labelPaint.color = ContextCompat.getColor(context, R.color.donut_label)
    }

    fun setPercentage(p: Float) {
        percentage = p.coerceIn(0f, 100f)
        invalidate()
    }

    fun setLabel(text: String) {
        label = text
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
            .coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))

        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val stroke = (w * 0.12f).coerceAtLeast(8f)

        backgroundPaint.strokeWidth = stroke
        progressPaint.strokeWidth = stroke

        rect.set(stroke / 2f, stroke / 2f, w - stroke / 2f, h - stroke / 2f)

        textPaint.textSize = w * 0.20f
        labelPaint.textSize = w * 0.13f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f

        // Círculo base
        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)

        // Progresso
        val sweep = 360f * (percentage / 100f)
        canvas.drawArc(rect, -90f, sweep, false, progressPaint)

        // Texto de porcentagem central
        canvas.drawText("${percentage.toInt()}%", cx, cy, textPaint)

        // Label (mês) — corrigido para não cortar
        val labelY =
            height - (labelPaint.descent() + labelPaint.ascent()) - (height * 0.10f)

        canvas.drawText(label, cx, labelY, labelPaint)
    }
}