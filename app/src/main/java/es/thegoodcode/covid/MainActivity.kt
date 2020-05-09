package es.thegoodcode.covid

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.thegoodcode.covid.app.Constants
import es.thegoodcode.covid.app.MyApp
import es.thegoodcode.covid.bluetooth.BluetoothReceiver
import es.thegoodcode.covid.viewmodel.BluetoothViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: BluetoothViewModel
    private lateinit var bluetoothReceiver: BluetoothReceiver
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var open = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        viewModel = ViewModelProvider(this).get(BluetoothViewModel::class.java)

        // Observer
        viewModel.getNewDevice().observe(this,  Observer {
            Toast.makeText(this, "Nuevo dispositivo: ${it}", Toast.LENGTH_LONG).show()
        })

        bluetoothReceiver = BluetoothReceiver()
        bluetoothReceiver.getData().observe(this, Observer {
            viewModel.registerExposure(it)
        })

        // Bluetooth
        startBluetooth()
    }

    private fun startBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        } else {
            if (!bluetoothAdapter?.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT)
            } else {
                startDiscover()
                allowBluetoothVisibility()
                registerDevice()
            }
        }
    }

    private fun registerDevice() {
        val sharedPref = MyApp.instance.getSharedPreferences(
            Constants.SHARED_PREF_APP,
            Context.MODE_PRIVATE)

        val macAddressOwner = sharedPref.getString(Constants.SHARED_PREF_MAC_ADDRESS, "")

        if(macAddressOwner == "")
            viewModel.registerDevice(bluetoothAdapter.address)

    }

    private fun startDiscover() {
        if(!bluetoothAdapter.isDiscovering) {
            checkBTPermissions()
            bluetoothAdapter.startDiscovery()
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(bluetoothReceiver, filter)
    }

    private fun allowBluetoothVisibility() {
        // Habilitar la visibilidad del dispositivo
        // Para que el dispositivo permanezca visible siempre: EXTRA_DISCOVERABLE_DURATION = 0
        // No se recomienda esta configuración debido a su alto nivel de inseguridad, pero en
        // este caso lo necesitamos.
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0)
        }
        startActivity(discoverableIntent)

        val filter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        registerReceiver(bluetoothReceiver, filter)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            Constants.REQUEST_ENABLE_BT -> {
                if(resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "BT on", Toast.LENGTH_LONG).show()
                    allowBluetoothVisibility()
                } else {
                    Toast.makeText(this, "BT denegado", Toast.LENGTH_LONG).show()
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT)
                }
            }
        }
    }

    private fun checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            var permissionCheck = ContextCompat.checkSelfPermission(this,"Manifest.permission.ACCESS_FINE_LOCATION")
            permissionCheck += ContextCompat.checkSelfPermission(this,"Manifest.permission.ACCESS_COARSE_LOCATION")
            if (permissionCheck != 0) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), Constants.REQUEST_PERMISSIONS_BLUETOOTH
                )
            }
        } else {
            Log.d("COVID", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.")
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(bluetoothReceiver)
    }
}
