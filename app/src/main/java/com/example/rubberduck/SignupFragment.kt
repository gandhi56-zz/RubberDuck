package com.example.rubberduck

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.fragment_signup.view.*

class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("users")

        view.signupBtn.setOnClickListener {
            if (view.emailText.toString().isEmpty() or passwordText.text.toString().isEmpty())
                return@setOnClickListener
            handleText.isEnabled = false
            emailText.isEnabled = false
            passwordText.isEnabled = false
            activity?.let {
                auth.createUserWithEmailAndPassword(view.emailText.text.toString(), view.passwordText.text.toString())
                    .addOnCompleteListener(it){ task->
                        if (task.isSuccessful){
                            Log.d(TAG, "createUserWithEmail:success")
                            Toast.makeText(context, "Authentication successful", Toast.LENGTH_SHORT).show()
                            saveUserToFirebaseDatabase(view.handleText.text.toString(), view.emailText.text.toString())
                        }
                        else{
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                        handleText.text.clear()
                        emailText.text.clear()
                        passwordText.text.clear()
                    }
            }
            handleText.isEnabled = true
            emailText.isEnabled = true
            passwordText.isEnabled = true
        }
        return view
    }

    private fun saveUserToFirebaseDatabase(handle: String, email: String){
        val userId = ref.push().key.toString()
        val user = UserData(userId, handle, email)
        ref.child(userId).setValue(user).addOnCompleteListener {
            Toast.makeText(context, "User saved to database successfully", Toast.LENGTH_LONG).show()
        }
    }

}
