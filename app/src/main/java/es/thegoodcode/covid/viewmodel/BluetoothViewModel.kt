package es.thegoodcode.covid.viewmodel

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.thegoodcode.covid.repository.BluetoothRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class BluetoothViewModel: ViewModel() {
    private var bluetoothRepository = BluetoothRepository()
    var i: Int = 1;


    fun getNewDevice(): LiveData<String> {
        return bluetoothRepository.getExposures()
    }

    fun registerExposure(device: BluetoothDevice) {
        var result = viewModelScope.async {
            Log.i("EXPO", "0. Entra en viewModelScope - Nº $i")
            bluetoothRepository.registerExposure(device)
        }

        Log.i("EXPO", "Result Nº $i: $result")
        i++
    }

    fun registerDevice(macAddress: String) {
        bluetoothRepository.registerDevice(macAddress)
    }
}