package com.dealeso.routertalk

import android.content.Context
import android.widget.Toast

//val cookie_first = "Basic%20YnJ1bm86YjFmODU2NTNjY2Y2ZWMyNGIzY2RjMGIwZjZiMzI2ZDA%3D"
val cookie = "Basic%20YWRtaW46NmQyMzE1MmJiYTczNGFhZWIzZTcyMmYxYWNkMzlkMWM%3D"// TODO= Explain how to sniff this cookie.
//It's the cipher combination of your user and password in the file encrypt.js

val ssid = "your_guest_wifi_name"
val LOCAL_IP = "192.168.0.1"
val REMOTE_IP = "your_remote_ip"
val BAD_AUTH = "Incorrect authentication.Try again"
val GOOD_AUTH = "Authentication: "
val ON_GUEST_WIFI = "Setting on Guest Wifi is: "
val OFF_GUEST_WIFI ="Setting off Guest Wifi is: "

val welcome = "Target: "
val connection_down =  "Wireless conection is not available"
val connection_up =  "Wireless conection is available"
val reboot_wait = "Router is rebooting to apply changes"
val ROUTER_REBOOT = "Router reboot is going: "

fun Context.toast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}