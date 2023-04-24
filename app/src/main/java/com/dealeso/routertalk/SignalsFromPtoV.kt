package com.dealeso.routertalk

sealed class SignalsFromPtoV{
    class activity_main_feedbackUpdate( val value: String ) : SignalsFromPtoV()
    class toast_reboot() : SignalsFromPtoV()
}