package az.khayalsharifli.firebaseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import az.khayalsharifli.firebaseapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        auth = Firebase.auth
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        firebaseAnalytics = Firebase.analytics

        // Firebase logger
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Main")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        }

        binding.buttonEmail.setOnClickListener {
            val params = Bundle()
            params.putString("sign_in_with_email", "Sign in with Email")
            firebaseAnalytics.logEvent("SignInEmail", params)
            signInWithEmailAndPassword()
        }

        binding.buttonGoogle.setOnClickListener {
            val params = Bundle()
            params.putString("sign_in_with_google", "Sign in with Google")
            firebaseAnalytics.logEvent("SignInGoogle", params)
            signInWithGoogle()
        }

        binding.buttonCrash.setOnClickListener {
            val params = Bundle()
            params.putString("crash_event", "App do Crash")
            firebaseAnalytics.logEvent("Crash", params)
            throw RuntimeException("Test Crash")
        }
    }

    private fun signInWithEmailAndPassword() {
        auth.signInWithEmailAndPassword("test@gmail.com", "12345678")
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Toast.makeText(this, "Success sign in:${currentUser.email}", Toast.LENGTH_LONG).show()
        }
    }

    private fun signInWithGoogle() {
        val intent = gsc.signInIntent
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val result = task.getResult(ApiException::class.java)
                Toast.makeText(this, "Success sign in:${result.email}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}