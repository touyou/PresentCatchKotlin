package com.dev.touyou.presentcatch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*

/**
 * Created by touyou on 16/03/28.
 */
class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {

    var presentImage: Bitmap? = null
    var present: Present? = null
    var playerImage: Bitmap? = null
    var player: Player? = null
    var thread: Thread? = null
    var surfaceHolder: SurfaceHolder? = null
    var screenWidth: Int? = null
    var screenHeight: Int? = null

    var score: Int = 0
    var life: Int = 10

    val FPS: Long = 30
    val FRAME_TIME: Long = 1000 / FPS

    init {
        holder.addCallback(this)
        val resources = context.resources
        presentImage = BitmapFactory.decodeResource(resources, R.drawable.img_present0)
        playerImage = BitmapFactory.decodeResource(resources, R.drawable.img_player)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        surfaceHolder = holder
        thread = Thread(this)
        thread?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        thread = null
    }

    override fun run() {
        present = Present(screenWidth!!, screenHeight!!)
        player = Player(screenHeight!!, screenWidth!!)

        val textPaint = Paint()
        textPaint.color = Color.BLACK
        textPaint.isFakeBoldText = true
        textPaint.textSize = 100.0f
        while (thread != null) {
            val canvas = surfaceHolder?.lockCanvas()
            canvas?.drawColor(Color.WHITE)
            canvas?.drawBitmap(presentImage, present?.x!!, present?.y!!, null)
            canvas?.drawBitmap(playerImage, player?.x!!, player?.y!!, null)
            canvas?.drawText("SCORE: "+score.toString(), 50.0f, 150.0f, textPaint)
            canvas?.drawText("LIFE: "+life.toString(), 50.0f, 300.0f, textPaint)

            if (life <= 0) {
                canvas?.drawText("Game Over", (screenWidth as Int).toFloat() / 3.0f, (screenHeight as Int).toFloat() / 2.0f, textPaint)
                surfaceHolder?.unlockCanvasAndPost(canvas)
                break
            }

            if (player?.isEnter(present!!)!!) {
                present?.reset()
                score += 10
            } else if (present?.y!! > screenHeight!!) {
                present?.reset()
                life--
            } else {
                present?.update()
            }
            surfaceHolder?.unlockCanvasAndPost(canvas)
            try {
                Thread.sleep(FRAME_TIME)
            } catch(e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    class Present(screenWidth: Int, screenHeight: Int) {
        val WIDTH: Int = 100
        val HEIGHT: Int = 100
        var x: Float? = null
        var y: Float? = null
        var scWidth: Int? = null
        init {
            val random = Random()
            x = random.nextInt(screenWidth - WIDTH).toFloat()
            y = 0.0f
            scWidth = screenWidth
        }
        fun update() {
            y = y!! + 15.0f
        }
        fun reset() {
            val random = Random()
            x = random.nextInt(scWidth!! - WIDTH).toFloat()
            y = 0.0f
        }
    }

    class Player(screenHeight: Int, screenWidth: Int) {
        val WIDTH: Int = 200
        val HEIGHT: Int = 200
        var x: Float? = null
        var y: Float? = null
        var scWidth: Int? = null
        init {
            x = 0.0f
            y = (screenHeight - HEIGHT).toFloat()
            scWidth = screenWidth
        }
        fun move(diffX: Float) {
            this.x = this.x!! + diffX
            this.x = Math.max(0.0f, x!!)
            this.x = Math.min((scWidth!! - WIDTH).toFloat(), x!!)
        }
        fun isEnter(present: Present): Boolean {
            return present.x!! + present.WIDTH > x!! && present.x!! < x!! + WIDTH && present.y!! + present.HEIGHT > y!! && present.y!! < y!! + HEIGHT
        }
    }
}
