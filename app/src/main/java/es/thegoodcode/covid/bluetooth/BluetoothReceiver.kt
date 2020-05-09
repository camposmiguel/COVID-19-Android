package es.thegoodcode.covid.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class BluetoothReceiver : BroadcastReceiver() {
    private val newDeviceAddress: MutableLiveData<BluetoothDevice> = MutableLiveData<BluetoothDevice>()

    override fun onReceive(context: Context?, intent: Intent) {
        when(intent.action) {
            BluetoothDevice.ACTION_FOUND -> {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device: BluetoothDevice =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                newDeviceAddress.value = device
            }
            BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                val mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR)

                when (mode) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> Log.d("COVID", "ACTION_SCAN_MODE_CHANGED: Discoverability Enabled.")
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> Log.d("COVID", "ACTION_SCAN_MODE_CHANGED: Discoverability Disabled. Able to receive connections.")
                    BluetoothAdapter.SCAN_MODE_NONE -> Log.d("COVID", "ACTION_SCAN_MODE_CHANGED: Discoverability Disabled. Not able to receive connections.")
                    BluetoothAdapter.STATE_CONNECTING -> Log.d("COVID", "ACTION_SCAN_MODE_CHANGED: Connecting....")
                    BluetoothAdapter.STATE_CONNECTED -> Log.d("COVID", "ACTION_SCAN_MODE_CHANGED: Connected.")
                }

            }
        }
    }

    fun getData(): LiveData<BluetoothDevice> {
        Log.i("COVID", "getData newDeviceAddress ${newDeviceAddress.value}")
        return newDeviceAddress
    }
}