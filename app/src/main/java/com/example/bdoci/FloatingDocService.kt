package com.example.bdoci

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bdoci.database.AppDatabase
import kotlinx.coroutines.*
import kotlin.math.abs

class FloatingDocService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: View
    private lateinit var panelView: View
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var panelParams: WindowManager.LayoutParams

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var adapter: FloatingDocAdapter

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        createNotificationChannel()
        startForeground(1, createNotification())

        initBubbleView()
        initPanelView()

        windowManager.addView(bubbleView, bubbleParams)
    }

    private fun initBubbleView() {
        bubbleView = LayoutInflater.from(this).inflate(R.layout.layout_floating_bubble, null)
        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        bubbleView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = bubbleParams.x
                        initialY = bubbleParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(bubbleView, bubbleParams)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val diffX = abs(event.rawX - initialTouchX)
                        val diffY = abs(event.rawY - initialTouchY)
                        if (diffX < 10 && diffY < 10) {
                            showPanel()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun initPanelView() {
        panelView = LayoutInflater.from(this).inflate(R.layout.layout_floating_panel, null)
        panelParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            PixelFormat.TRANSLUCENT
        ).apply {
            dimAmount = 0.5f
            gravity = Gravity.CENTER
        }

        val etSearch = panelView.findViewById<EditText>(R.id.etSearch)
        val rvResults = panelView.findViewById<RecyclerView>(R.id.rvResults)
        val btnClose = panelView.findViewById<ImageView>(R.id.btnClose)
        val btnExit = panelView.findViewById<ImageView>(R.id.btnExitService)

        adapter = FloatingDocAdapter(emptyList())
        rvResults.layoutManager = LinearLayoutManager(this)
        rvResults.adapter = adapter

        btnClose.setOnClickListener {
            hidePanel()
        }

        btnExit.setOnClickListener {
            stopSelf()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = serviceScope.launch {
                    delay(300)
                    val query = s.toString()
                    val results = if (query.isEmpty()) {
                        emptyList()
                    } else {
                        AppDatabase.getDatabase(this@FloatingDocService).docDao().searchDocs("%$query%")
                    }
                    adapter.updateData(results)
                }
            }
        })
    }

    private fun showPanel() {
        windowManager.removeView(bubbleView)
        windowManager.addView(panelView, panelParams)
    }

    private fun hidePanel() {
        windowManager.removeView(panelView)
        windowManager.addView(bubbleView, bubbleParams)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "floating_service",
                "Floating Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "floating_service")
            .setContentTitle("Floating Search Active")
            .setContentText("Tap to open search bubble")
            .setSmallIcon(R.drawable.logo)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (::bubbleView.isInitialized && bubbleView.windowToken != null) windowManager.removeView(bubbleView)
        if (::panelView.isInitialized && panelView.windowToken != null) windowManager.removeView(panelView)
    }
}