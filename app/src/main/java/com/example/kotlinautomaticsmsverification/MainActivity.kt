package com.example.kotlinautomaticsmsverification


import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*


class MainActivity : AppCompatActivity() {

    //<#> Your otp code is: 9999 CURIiSc/cLG

    companion object {
        const val CREDENTIAL_PICKER_REQUEST = 200
        const val TAG = "TAG_MainActivity -> "
    }

    private lateinit var editTextUserPhone: EditText
    private lateinit var userPhoneTextView: TextView
    private lateinit var textMsgOtp: TextView
    private lateinit var btnGetOtpMsg: Button

    private val mySMSBroadcastReceiver = MySMSBroadcastReceiver()
    private val intentFilter = IntentFilter()

    private lateinit var disposable: Disposable

    private fun initView() {
        editTextUserPhone = findViewById(R.id.editTextPhone)
        userPhoneTextView = findViewById(R.id.userPhone)
        textMsgOtp = findViewById(R.id.otpMsgText)
        btnGetOtpMsg = findViewById(R.id.btnOtpMsg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        requestHint()
    }

    override fun onResume() {
        super.onResume()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        disposable = mySMSBroadcastReceiver.create(this, intentFilter)
                ?.subscribeOn(Schedulers.newThread())
                ?.subscribe()!!

        mySMSBroadcastReceiver.behaviorSubject.subscribe { v: String ->
            textMsgOtp.text = v
        }

        btnGetOtpMsg.setOnClickListener {
            listenerSMS()
        }
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
        mySMSBroadcastReceiver.behaviorSubject.subscribe().dispose()
    }

    private fun listenerSMS() {

        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()

        task.addOnSuccessListener {
            Toast.makeText(this@MainActivity, "SMS Retriever starts ${Date()}", Toast.LENGTH_LONG)
                    .show()
            Log.e(TAG, "SMS Retriever starts ${Date()}")
        }
        task.addOnFailureListener {
            Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()
        val credentialsClient = Credentials.getClient(this)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST,
                null, 0, 0, 0
        )
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->
                // Obtain the phone number from the result
                if (resultCode == RESULT_OK && data != null) {
                    val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    Log.e(TAG, credential!!.id)
                    editTextUserPhone.setText(credential.id)
                    userPhoneTextView.text = credential.id
                } else {
                    editTextUserPhone.setText("")
                    Log.e(TAG, "NO PHONE")
                }

        }
    }

}