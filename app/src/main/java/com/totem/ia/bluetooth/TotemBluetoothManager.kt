package com.totem.ia.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TotemBluetoothManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _uiConnectedDeviceName()

    private fun _uiConnectedDeviceName(): StateFlow<String?> {
        val flow = MutableStateFlow<String?>(null)
        
        val hasPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.BLUETOOTH_CONNECT
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission is granted via Manifest on older versions
        }

        if (hasPermission) {
            try {
                bluetoothAdapter?.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
                    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                        if (profile == BluetoothProfile.A2DP) {
                            @SuppressLint("MissingPermission")
                            val devices = proxy.connectedDevices
                            if (devices.isNotEmpty()) {
                                flow.value = devices[0].name
                            }
                        }
                        bluetoothAdapter.closeProfileProxy(profile, proxy)
                    }
                    override fun onServiceDisconnected(profile: Int) {}
                }, BluetoothProfile.A2DP)
            } catch (e: SecurityException) {
                // Should not happen since we checked, but safe fallback
                flow.value = null
            }
        }
        return flow
    }
    
    fun isBluetoothAudioConnected(): Boolean {
        return _connectedDeviceName.value != null
    }
}
