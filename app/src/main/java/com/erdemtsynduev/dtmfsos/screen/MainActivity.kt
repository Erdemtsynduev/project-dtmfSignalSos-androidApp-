package com.erdemtsynduev.dtmfsos.screen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erdemtsynduev.dtmfsos.R
import com.redmadrobot.inputmask.MaskedTextChangedListener
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    companion object {
        const val APP_PREFERENCES = "APP_PREFERENCES"
        const val APP_PREFERENCES_PHONE = "APP_PREFERENCES_PHONE"
        const val APP_PREFERENCES_AUTO_CALL = "APP_PREFERENCES_AUTO_CALL"
    }

    private var sharedPreferences: SharedPreferences? = null
    private var isNeedAutoCall: Boolean? = false
    private val timerTask: Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.d("onCreate")

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)


        btn_sos.isEnabled = false

        val maskedTextChangedListener = MaskedTextChangedListener.installOn(
            phone_number,
            "+7 ([000]) [000]-[00]-[00]",
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                    btn_sos.isEnabled = maskFilled
                    if (maskFilled) {
                        savePhoneNumber(extractedValue)
                    }
                }
            }
        )

        phone_number.hint = maskedTextChangedListener.placeholder()

        btn_sos.setOnClickListener {
            getSharedPreferensCallNumber()
        }

        switch_start_without_btn.setOnCheckedChangeListener { _, isChecked ->
            changeStateAutoCall(isChecked)
            if (!isChecked) {
                timerTask.cancel()
            }
        }

        initPhoneNumber()
        initStateAutoCall()
    }

    private fun initPhoneNumber() {
        Timber.d("initPhoneNumber")
        if (sharedPreferences?.contains(APP_PREFERENCES_PHONE)!!) {
            val textTemp = sharedPreferences?.getString(APP_PREFERENCES_PHONE, "")
            phone_number.setText(textTemp)
        }
    }

    private fun initStateAutoCall() {
        Timber.d("initStateAutoCall")
        if (sharedPreferences?.contains(APP_PREFERENCES_AUTO_CALL)!!) {
            isNeedAutoCall = sharedPreferences?.getBoolean(APP_PREFERENCES_AUTO_CALL, false)
            switch_start_without_btn.isChecked = isNeedAutoCall!!
            startAutoCall(isNeedAutoCall)
        }
    }

    private fun startAutoCall(isNeedAutoCall: Boolean?) {
        Timber.d("startAutoCall")
        if (isNeedAutoCall == true) {
            timerTask.schedule(5000) {
                getSharedPreferensCallNumber()
            }
        }
    }

    private fun savePhoneNumber(phoneString: String) {
        Timber.d("savePhoneNumber")
        sharedPreferences?.edit()?.putString(APP_PREFERENCES_PHONE, phoneString)?.apply()
    }

    private fun changeStateAutoCall(isNeedAutoCall: Boolean) {
        Timber.d("changeStateAutoCall")
        sharedPreferences?.edit()?.putBoolean(APP_PREFERENCES_AUTO_CALL, isNeedAutoCall)?.apply()
    }

    private fun getSharedPreferensCallNumber() {
        Timber.d("getSharedPreferensCallNumber")
        if (sharedPreferences?.contains(APP_PREFERENCES_PHONE)!!) {
            startCaller(sharedPreferences?.getString(APP_PREFERENCES_PHONE, ""))
        }
    }

    private fun startCaller(phoneNumber: String?) {
        Timber.d("startCaller")
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, phoneNumber, Toast.LENGTH_LONG).show()
            return
        }
        val numberString = "+7" + phoneNumber + getString(R.string.sos_dtmf_signal)
        val number: Uri = Uri.parse(getString(R.string.prefix_phone_call) + numberString)
        val dial = Intent(Intent.ACTION_CALL, number)
        startActivity(dial)
    }
}