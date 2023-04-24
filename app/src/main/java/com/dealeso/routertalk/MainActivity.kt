package com.dealeso.routertalk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mainPresenter = MainPresenter(lifecycleScope) { justListeningToP(it) }
    private var router_ip = LOCAL_IP
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                .EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity_main_feedback.movementMethod = ScrollingMovementMethod()
        activity_main_feedback.text = "$welcome$router_ip"
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        btn_on.setOnClickListener {
            mainPresenter.routerGuestWifiOn(lifecycleScope)
        }
        btn_off.setOnClickListener {
            mainPresenter.routerGuestWifiOff(lifecycleScope)
        }
        btn_reboot.setOnClickListener {
            mainPresenter.routerReboot(lifecycleScope)
        }
    }


    private fun justListeningToP(sign: SignalsFromPtoV): Unit  {

        when (sign) {
            is SignalsFromPtoV.activity_main_feedbackUpdate ->
                //binding.progress.visibility = if (sign1.value) View.VISIBLE else View.GONE
           activity_main_feedback.append(System.lineSeparator() + sign.value )
            is SignalsFromPtoV.toast_reboot ->
                 toast ("$reboot_wait")

        }
        while (activity_main_feedback.canScrollVertically(1)) {
            activity_main_feedback.scrollBy(0, 10);
        }
    }
    override fun onStart() {
        super.onStart()
       // registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private fun disconnected() {
       btn_on.visibility = View.INVISIBLE
       btn_off.visibility = View.INVISIBLE
       btn_reboot.visibility = View.INVISIBLE

        activity_main_feedback.append(System.lineSeparator())
        activity_main_feedback.append("$connection_down")

        //  imageView.visibility = View.VISIBLE
    }

    private fun connected() {

        btn_on.visibility = View.VISIBLE
        btn_off.visibility = View.VISIBLE
        btn_reboot.visibility = View.VISIBLE

        activity_main_feedback.append(System.lineSeparator())
        activity_main_feedback.append("$connection_up")

        //   recyclerView.visibility = View.VISIBLE
     //   imageView.visibility = View.INVISIBLE
       // fetchFeeds()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val filter = when (item.itemId) {
            R.id.remote -> router_ip = REMOTE_IP
            R.id.local ->  router_ip = LOCAL_IP
            else ->  router_ip = LOCAL_IP

        }
        activity_main_feedback.text = "$welcome$router_ip"
        activity_main_feedback.append(System.lineSeparator())


        return super.onOptionsItemSelected(item)
    }
}
