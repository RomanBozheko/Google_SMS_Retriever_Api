package com.example.kotlinautomaticsmsverification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.subjects.BehaviorSubject

import java.util.*

class MySMSBroadcastReceiver : ObservableOnSubscribe<IntentFilter> {

    companion object {
        const val TAG = "TAG_MySMSBroadcastReceiver -> "
    }

    val behaviorSubject: BehaviorSubject<String> = BehaviorSubject.create()

    fun create(context: Context, intentFilter: IntentFilter?): Observable<Intent?>? {

        val appContext = context.applicationContext

        return Observable.create { emitter: ObservableEmitter<Intent?> ->
            val receiver: BroadcastReceiver = object : BroadcastReceiver() {
                @SuppressLint("LongLogTag")
                override fun onReceive(context: Context, intent: Intent) {
                    emitter.onNext(intent)

                    Log.e(TAG, " onReceive >_  ${Date()}")
                    if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {

                        val extras = intent.extras
                        val mStatus = extras!![SmsRetriever.EXTRA_STATUS] as Status?

                        when (mStatus!!.statusCode) {
                            CommonStatusCodes.SUCCESS -> {

                                val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?

                                val otp: String = message!!.replace("<#> Your otp code is: ", "")
                                val cod: String = otp.replace(" CURIiSc/cLG", "")

                                Log.e(TAG, "$message")
                                behaviorSubject.onNext(cod)
                            }
                            CommonStatusCodes.TIMEOUT -> {
                                Log.e(MainActivity.TAG, "TIMEOUT ${Date()}")

                            }
                        }

                    }
                }
            }
            appContext.registerReceiver(receiver, intentFilter)
            emitter.setCancellable { appContext.unregisterReceiver(receiver) }
        }
    }

    override fun subscribe(emitter: ObservableEmitter<IntentFilter>?) {
        TODO("Not yet implemented")
    }
}





