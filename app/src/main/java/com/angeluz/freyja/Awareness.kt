package com.angeluz.freyja

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.BatteryManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Awareness: recolecta señales del entorno y ejecuta reglas simples.
 * - Señales: batería, red, movimiento (acelerómetro), ubicación (last known).
 * - Memoria: usa Pref (DataStore).
 * - Reglas: ejemplo -> si batería < 15% y sin red, mostrar alerta.
 */
object Awareness : SensorEventListener {

    data class Snapshot(
        val ts: Long = System.currentTimeMillis(),
        val batteryPct: Int = -1,
        val online: Boolean = false,
        val moving: Boolean = false,
        val lastLat: Double? = null,
        val lastLon: Double? = null
    )

    private var sensorManager: SensorManager? = null
    private var accelMagnitude = 0.0
    private var moving = false
    private var job: Job? = null

    fun start(ctx: Context) {
        stop()
        sensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Observador de red (cambio de conectividad)
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { /* no-op */ }
            override fun onLost(network: Network) { /* no-op */ }
        })

        // Bucle de muestreo liviano cada 20s
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val snap = snapshot(ctx)
                applyRules(ctx, snap)
                delay(20_000L)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        sensorManager?.unregisterListener(this)
        sensorManager = null
    }

    private fun batteryPct(ctx: Context): Int {
        val bm = ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val pct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return if (pct <= 0) -1 else pct
    }

    private fun online(ctx: Context): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun lastLocation(ctx: Context): Pair<Double?, Double?> {
        return runCatching {
            val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = lm.getProviders(true)
            var best: Location? = null
            for (p in providers) {
                val l = lm.getLastKnownLocation(p) ?: continue
                if (best == null || l.accuracy < best!!.accuracy) best = l
            }
            if (best != null) best!!.latitude to best!!.longitude else null to null
        }.getOrDefault(null to null)
    }

    suspend fun snapshot(ctx: Context): Snapshot = withContext(Dispatchers.Default) {
        val (lat, lon) = lastLocation(ctx)
        Snapshot(
            ts = System.currentTimeMillis(),
            batteryPct = batteryPct(ctx),
            online = online(ctx),
            moving = moving,
            lastLat = lat,
            lastLon = lon
        )
    }

    private suspend fun applyRules(ctx: Context, s: Snapshot) {
        // Ejemplo de regla: batería crítica + sin red => alerta local
        if (s.batteryPct in 1..15 && !s.online) {
            FreyjaNotifier.alert(ctx,
                title = "Batería crítica y sin red",
                text = "Activa modo ahorro / evalúa enviar SOS local",
                id = 3001
            )
        }

        // Guarda último estado en memoria (opcional)
        Pref.setLastStatus(ctx, statusText(s))
    }

    private fun statusText(s: Snapshot): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val whenStr = fmt.format(s.ts)
        val loc = if (s.lastLat != null) "(${s.lastLat}, ${s.lastLon})" else "sin loc"
        val mv = if (s.moving) "moviendo" else "quieto"
        val net = if (s.online) "online" else "offline"
        return "$whenStr · $net · bat:${s.batteryPct}% · $mv · $loc"
    }

    // ---------- SensorEventListener ----------
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            // magnitud aproximada
            accelMagnitude = Math.sqrt(x*x + y*y + z*z)
            moving = accelMagnitude > 12.0 // umbral simple
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}