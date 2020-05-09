package es.thegoodcode.covid.data

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.okhttp.Dispatcher
import es.thegoodcode.covid.app.Constants
import es.thegoodcode.covid.app.MyApp
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FirestoreRemoteSource {
    val db = FirebaseFirestore.getInstance()

    fun addDevice(macAdress: String) {
        val device = hashMapOf(
            "mac_address" to macAdress,
            "firebase_id" to FirebaseInstanceId.getInstance().id,
            "test_result" to false
        )

        db.collection(Constants.DB_COLLECTION_DEVICES)
            .add(device)
            .addOnSuccessListener { documentReference ->
                Log.d("", "DocumentSnapshot added with ID: ${documentReference.id}")

                val sharedPref = MyApp.instance.getSharedPreferences(
                    Constants.SHARED_PREF_APP,
                    Context.MODE_PRIVATE
                )

                with(sharedPref.edit()) {
                    putString(Constants.SHARED_PREF_MAC_ADDRESS, macAdress)
                    commit()
                }
            }
            .addOnFailureListener { e ->
                Log.w("", "Error adding document", e)
            }
    }

    suspend fun addExposure(device: BluetoothDevice): DocumentReference? {

        val sharedPref = MyApp.instance.getSharedPreferences(
            Constants.SHARED_PREF_APP,
            Context.MODE_PRIVATE
        )

        val macAddressOwner = sharedPref.getString(Constants.SHARED_PREF_MAC_ADDRESS, "")

        Log.i("EXPO", "1. addExposure $macAddressOwner -> ${device.address}")

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()

        val resultadoCondicion = existExposure(macAddressOwner, device.address, sdf.format(date))

        Log.i("EXPO", "3. Resultado condición para ${device.address}: ${resultadoCondicion?.size()}")

        if (resultadoCondicion == null || resultadoCondicion?.size() == 0) {
            val exposure = hashMapOf(
                "mac_address_1" to macAddressOwner,
                "mac_address_2" to device.address,
                "name_2" to device.name,
                "day" to sdf.format(date)
            )

            Log.i("EXPO", "4. Entra en condición para ${device.address}")

            return db.collection(Constants.DB_COLLECTION_EXPOSURES)
                .add(exposure)
                .await()

        } else {
            Log.i("EXPO", "6. Exposure no added $macAddressOwner -> ${device.address}")
            return null
        }

    }

    suspend fun existExposure(macAddress1: String?, macAddress2: String, day: String): QuerySnapshot? {
        Log.i("EXPO", "2. Exist exposure $macAddress1 -> $macAddress2")

        return try{
            var result = db.collection(Constants.DB_COLLECTION_EXPOSURES)
                .whereEqualTo("mac_address_1", macAddress1)
                .whereEqualTo("mac_address_2", macAddress2)
                .whereEqualTo("day", day).get().await()
            result
        }catch (e : Exception){
            null
        }
    }

}