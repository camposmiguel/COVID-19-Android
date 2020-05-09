package es.thegoodcode.covid.repository

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import es.thegoodcode.covid.data.FirestoreRemoteSource
import kotlinx.coroutines.runBlocking

class BluetoothRepository {
    private val newDevice: MediatorLiveData<String> = MediatorLiveData<String>()
    private var remoteSource: FirestoreRemoteSource = FirestoreRemoteSource()

    init {

    }

    fun getExposures(): LiveData<String> {
        return newDevice
    }

    suspend fun registerExposure(device: BluetoothDevice): DocumentReference? {
        return remoteSource.addExposure(device)
    }

    fun registerDevice(macAddress: String) {
        remoteSource.addDevice(macAddress)
    }
}