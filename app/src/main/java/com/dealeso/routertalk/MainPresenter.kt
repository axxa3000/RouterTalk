package com.dealeso.routertalk

import android.util.Log
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.ext.asString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainPresenter(private val scope: CoroutineScope, private val listener : (SignalsFromPtoV) -> Unit ) {
  private  var router_ip = LOCAL_IP
   private lateinit var authenticationKey : String
    fun routerGuestWifiOn(scope: CoroutineScope) {
        scope.launch {
            authenticationKey = withContext(Dispatchers.IO) { connect() }
            if(authenticationKey=="" || authenticationKey.contains("script") ){
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( BAD_AUTH ))
            }else{
                //Sending Command to set Guest wifi on
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( GOOD_AUTH + authenticationKey ))
                var msg = withContext(Dispatchers.IO) { setGuestWifiOn( authenticationKey ) }
              // var msg = "testing"
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( ON_GUEST_WIFI + msg ))
                listener(SignalsFromPtoV.toast_reboot())
            }
        }
    }

    fun routerGuestWifiOff(scope: CoroutineScope) {
        scope.launch {
            authenticationKey = withContext(Dispatchers.IO) { connect() }
            if(authenticationKey=="" || authenticationKey.contains("script") ){
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( BAD_AUTH ))
            }else{
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( GOOD_AUTH + authenticationKey ))
                var msg = withContext(Dispatchers.IO) { setGuestWifiOff( authenticationKey ) }
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( OFF_GUEST_WIFI + msg ))
                listener(SignalsFromPtoV.toast_reboot())
            }
        }
    }

     private fun connect():String{
         var idSession = ""
             httpGet {
                 host = router_ip
                 path = "/userRpm/LoginRpm.htm"
                 param {
                     "Save" to "Save"
                 }
                 header {
                     "Referer" to "http://$router_ip"
                     cookie {
                         "Authorization" to cookie
                     }
                 }
             }.use { response ->
                 if (response.isSuccessful) {
                     Log.d("Kcon","Response status ok")
                     val dataAsString: String? = response.asString()
                     //  val dataTrim1 = dataAsString?.substringAfter(delimiter = "192.168.0.1/", missingDelimiterValue = "Extension Not found")
                     //  val dataTrim1Count = dataAsString?.count()
                     //  Log.d("Kcon","dataTrim1Count: $dataTrim1Count")
                      idSession = dataAsString?.substring(86,102).toString()
                     Log.d("Kcon","id_Session: $idSession")
                 } else {
                     Log.d("Kcon","Response status bad${response.message()}")
                 }
             }
         return idSession
     }

    private fun setGuestWifiOn(idSession:String):String{
        var msg : String
                Log.d("Kcon","setGuestWifiOn withContext ok")
                httpGet {
                    host = router_ip
                    path = "/$idSession/userRpm/GuestNetWirelessCfgRpm.htm"
                    param {
                        "up_bandWidth" to "256"
                        "down_bandWidth" to "1024"
                        "guestNetMode" to "1"
                        "ssid" to ssid
                        "SecOpt" to "0"
                        "pskSecOpt" to "2"
                        "pskCipher" to "3"
                        "pskSecret" to "70885132"
                        "interval" to "0"
                        "AccessTime" to "1"
                        "timeouthour" to "0"
                        "timeoutmin" to "0"
                        "scheDay" to "0"
                        "starttime" to "0700"
                        "endtime" to "2300"
                        "Save" to "Save"
                    }
                    header {
                        "Referer" to "http://$router_ip"
                        cookie {
                            "Authorization" to cookie
                        }
                    }
                }.use { response ->
                    if (response.isSuccessful) {
                        Log.d("Kcon", "setGuestWifiOn Response status ok ${response.message()}")
                     //  logOut( idSession )

                        msg = response.message()

                    } else {
                        Log.d("Kcon", "setGuestWifiOn status bad${response.message()}")
                      //  logOut( idSession )
                        msg = response.message()
                    }
                }
        return msg
    }

    private fun setGuestWifiOff(idSession:String):String {
        var msg : String
        Log.d("Kcon", "setGuestWifiOn withContext ok")
        httpGet {
            host = router_ip
            path = "/$idSession/userRpm/GuestNetWirelessCfgRpm.htm"
            param {
                "up_bandWidth" to "256"
                "down_bandWidth" to "1024"
                "ssid" to ssid
                "SecOpt" to "0"
                "pskSecOpt" to "2"
                "pskCipher" to "3"
                "pskSecret" to "70885132"
                "interval" to "0"
                "AccessTime" to "1"
                "timeouthour" to "0"
                "timeoutmin" to "0"
                "scheDay" to "0"
                "daysAll" to "0"
                "Save" to "Save"
            }
            header {
                "Referer" to "http://$router_ip"
                cookie {
                    "Authorization" to cookie
                }
            }
        }.use { response ->
            if (response.isSuccessful) {
                Log.d("Kcon", "setGuestWifiOff Response status ok: ${response.message()}")
              // logOut( idSession )
                msg = response.message()

            } else {
                Log.d("Kcon", "setGuestWifiOff status bad: ${response.message()}")
               // logOut( idSession )
                msg = response.message()

            }
        }
        return msg
    }

    private fun logOut(idSession: String ):String {
        var msg : String
        Log.d("Kcon","Entering Log Out... ")
                httpGet {
                    // Log.d("Kcon","Entering Log Out httpGet... ")
                    host = router_ip
                    path = "/$idSession/userRpm/LogoutRpm.htm"
                    header {
                        "Referer" to "http://$router_ip/$idSession/userRpm/MenuRpm.htm"
                        cookie {
                            "Authorization" to cookie
                        }
                    }
                }.use { response ->
                    if (response.isSuccessful) {
                        Log.d("Kcon","Log Out Ok: ${response.message()}")
                        msg = response.message()
                    } else {
                        Log.d("Kcon","Log Out Bad: ${response.message()}")
                        msg = response.message()
                    }
                }
        return msg
    }

    fun routerReboot(scope: CoroutineScope) {
        scope.launch {
            authenticationKey = withContext(Dispatchers.IO) { connect() }
            if(authenticationKey=="" || authenticationKey.contains("script") ){
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( BAD_AUTH ))
            }else{
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( GOOD_AUTH + authenticationKey ))
                var msg = withContext(Dispatchers.IO) { setRouterReboot( authenticationKey ) }
                listener(SignalsFromPtoV.activity_main_feedbackUpdate( ROUTER_REBOOT + msg ))
                listener(SignalsFromPtoV.toast_reboot())
            }
        }
    }

    private fun setRouterReboot(idSession: String ):String {
        var msg : String
        Log.d("Kcon","Entering reboot ... ")
        httpGet {
            host = router_ip
            // /userRpm/SysRebootRpm.htm
            path = "/$idSession/userRpm/SysRebootRpm.htm"
            param {
                "Reboot" to "Reboot"
            }
            header {
                "Referer" to "http://$router_ip/$idSession/userRpm/SysRebootRpm.htm"
                cookie {
                    "Authorization" to cookie
                }
            }
        }.use { response ->
            if (response.isSuccessful) {
                Log.d("Kcon","reboot Ok: ${response.message()}")
                msg = response.message()
            } else {
                Log.d("Kcon","reboot Bad: ${response.message()}")
                msg = response.message()
            }
        }
        return msg
    }

}
