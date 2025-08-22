package com.angeluz.freyja

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Environment

class MainActivity : ComponentActivity() {

    private val REQ_PERMS = 1226

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestDangerousPermissions()
        askSpecialGrants()

        // Inicia el servicio en primer plano (clase en el MISMO paquete)
        ContextCompat.startForegroundService(
            this, Intent(this, FreyjaService::class.java)
        )

        Toast.makeText(this, "Freyja en guardia…", Toast.LENGTH_SHORT).show()
        finish() // Activity solo como lanzador
    }

    private fun requestDangerousPermissions() {
        val need = mutableListOf<String>()

        // Micro / Cámara
        need += Manifest.permission.RECORD_AUDIO
        need += Manifest.permission.CAMERA

        // Ubicación
        need += Manifest.permission.ACCESS_COARSE_LOCATION
        need += Manifest.permission.ACCESS_FINE_LOCATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            need += Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }

        // Notificaciones (13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            need += Manifest.permission.POST_NOTIFICATIONS
        }

        // Medios (13+) o legacy (<13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            need += Manifest.permission.READ_MEDIA_IMAGES
            need += Manifest.permission.READ_MEDIA_VIDEO
            need += Manifest.permission.READ_MEDIA_AUDIO
        } else {
            need += Manifest.permission.READ_EXTERNAL_STORAGE
        }

        // Teléfono/SMS (si los usarás)
        need += Manifest.permission.READ_PHONE_STATE
        need += Manifest.permission.CALL_PHONE
        need += Manifest.permission.SEND_SMS

        // Bluetooth (12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            need += Manifest.permission.BLUETOOTH_CONNECT
            need += Manifest.permission.BLUETOOTH_SCAN
            need += Manifest.permission.BLUETOOTH_ADVERTISE
        }

        val toAsk = need.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (toAsk.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toAsk.toTypedArray(), REQ_PERMS)
        }
    }

    private fun askSpecialGrants() {
        // All files (Android 11+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            !Environment.isExternalStorageManager()
        ) {
            val uri = Uri.parse("package:$packageName")
            startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
        }

        // Overlay (HUD / botón)
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        }

        // Ignorar optimización de batería
        val pm = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            startActivity(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .setData(Uri.parse("package:$packageName"))
            )
        }

        // Exact alarms (12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!am.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }

        // (Opcional) Acceso a uso de apps / DND / Noti listener los habilitamos después
        // startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        // startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        // startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }
}