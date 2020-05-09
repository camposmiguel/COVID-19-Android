package es.thegoodcode.covid.app

class Constants {

    companion object {
        val DB_COLLECTION_CONNECTIONS: String = "connections"
        val SHARED_PREF_APP: String? = "shared_pref_app"
        val SHARED_PREF_MAC_ADDRESS: String = "mac_address"
        val DB_COLLECTION_DEVICES: String = "devices"
        const val DB_COLLECTION_EXPOSURES: String = "exposures"
        const val REQUEST_ENABLE_BT: Int = 1
        const val REQUEST_PERMISSIONS_BLUETOOTH: Int = 2
    }
}