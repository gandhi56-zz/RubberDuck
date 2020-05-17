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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.fragment_signup.view.*

class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

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
        view.signupBtn.setOnClickListener {
            if (view.emailText.toString().isEmpty() or passwordText.text.toString().isEmpty())
                return@setOnClickListener
            activity?.let {
                auth.createUserWithEmailAndPassword(view.emailText.text.toString(), view.passwordText.text.toString())
                    .addOnCompleteListener(it){ task->
                        if (task.isSuccessful){
                            Log.d(TAG, "createUserWithEmail:success")
                            Toast.makeText(context, "Authentication successful", Toast.LENGTH_SHORT).show()
//                            saveUserToFirebaseDatabase(view.handleText.text.toString())
                        }
                        else{
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                        emailText.text.clear()
                        passwordText.text.clear()
                    }
            }
        }
        return view
    }

    private fun saveUserToFirebaseDatabase(handle: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.setValue(handle).addOnSuccessListener {
            Log.d(TAG, "User $handle saved to the firebase database")
        }
    }

}
