package com.chaitany.agewell

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.chaitany.agewell.databinding.ActivityLoginBinding
import kotlin.properties.Delegates

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var Otp by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLoginBinding.inflate(layoutInflater)


        setContentView(binding.root)

        binding.btnLoginNow.setOnClickListener {
            if(binding.btnVerifyOtp.isEnabled==false && binding.btnSendOtp.isEnabled==false){
                Toast.makeText(this,"Login SuccessFull",Toast.LENGTH_SHORT).show()
                val intent=Intent(this,Home::class.java)
                startActivity(intent)
                finish()

            }else{
                Toast.makeText(this,"Please Verify Otp",Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSendOtp.setOnClickListener {

            if(binding.etMobileNumber.length()==10){
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    val SMS_PERMISSION_CODE=123
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
                }

                sendOtp(binding.etMobileNumber.text.toString())
            }else{
                Toast.makeText(this,"Enter Valid Mobile",Toast.LENGTH_SHORT)
                    .show()
            }



        }
        binding.btnVerifyOtp.setOnClickListener {
            if(binding.etMobileNumber.length()==10 && binding.etOtp.length()==6){

                if(binding.etOtp.text.toString()==Otp.toString()){

                    Toast.makeText(this,"Verification Complete",Toast.LENGTH_SHORT).show()
                    binding.btnVerifyOtp.isEnabled=false

                }else{
                    Toast.makeText(this,"Otp is incorrect $Otp",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Enter Valid Otp and Mobile",Toast.LENGTH_SHORT).show()

            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun sendOtp(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            Otp = (100000..999999).random() // Generate 6-digit OTP
            val message = "Your OTP for login into Age Well : $Otp , Don't Share With Anyone."
            val smsManager = android.telephony.SmsManager.getDefault()

            try {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                startOtpTimer(binding.btnSendOtp,binding.tvTimer)




            } catch (e: Exception) {
                Toast.makeText(this, "Failed to send OTP: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "SMS permission is required to send OTP", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(
                    this,
                    "Permission granted! Try sending OTP again.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Permission denied! Can't send SMS.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    fun startOtpTimer(button: Button, timerTextView: TextView) {
        // Disable the button when clicked
        button.isEnabled = false

        // Set up the countdown timer for 30 seconds (30,000 milliseconds)
        val timer = object : CountDownTimer(30000, 1000) { // 30 seconds, 1-second intervals
            override fun onTick(millisUntilFinished: Long) {
                // Update the timer UI every second
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = "Resend Again:$secondsRemaining seconds remaining"
            }

            override fun onFinish() {
                // Re-enable the button when the timer finishes
                button.isEnabled = true
                timerTextView.text = "Send OTP"
            }
        }

        // Start the countdown timer
        timer.start()
    }
}