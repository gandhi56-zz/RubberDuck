package com.example.rubberduck

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.max


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var data: HashMap<String, Any>
    var handleValue: String = ""
    var emailValue: String = ""
    val user = User()

    @SuppressLint("StaticFieldLeak")
    internal inner class UserProfileRequest : AsyncTask<Context, Void, Boolean>(){
        override fun onPreExecute() {
            super.onPreExecute()
            emailLogin.isEnabled = false
            passwordLogin.isEnabled = false
            loginBtn.isEnabled = false
            activity!!.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Context): Boolean {
//            handleValue = getHandle("gandhi21299@gmail.com")
            handleValue = data["handle"].toString()
            emailValue = data["email"].toString()
            if (!userInfo()){
                println("User info could not be received")
                return false
            }
            if (!userStatus())  return false
            if (!userRating())  return false
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result){
//                emailLogin.text.clear()
//                passwordText.text.clear()
                startMainActivity()
            }
            else{
                Toast.makeText(context, "Error receiving info from Codeforces", Toast.LENGTH_SHORT).show()
                return
            }
            emailLogin.isEnabled = true
            passwordLogin.isEnabled = true
            loginBtn.isEnabled = true
            activity!!.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
        }

        private fun sendHTTPRequest(url: String): String{
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            return response.body()?.string().toString()
        }

        private fun userInfo(): Boolean{
            println("HANDLE = $handleValue")
            val json = sendHTTPRequest("https://codeforces.com/api/user.info?handles="
                    + handleValue)
            val jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED"){
                println("Status: FAILED")
                return false
            }
            val resultArray = jsonObj.getJSONArray("result")
            user.setHandle(resultArray.getJSONObject(0).getString("handle"))
            user.setTitlePhoto("https:" + resultArray.getJSONObject(0)
                .getString("titlePhoto"))

            if (resultArray.getJSONObject(0).has("rank")){
                user.setRank(resultArray.getJSONObject(0).getString("rank"))
            }
            println("User info received")
            return true
        }

        private fun userStatus(): Boolean{
            val json = sendHTTPRequest("https://codeforces.com/api/user.status?handle="
                    + handleValue)
            val jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED")    return false
            val resultArray = jsonObj.getJSONArray("result")

            for (i in 0 until resultArray.length()) {
                val sub = Submission()
                sub.id = resultArray.getJSONObject(i).getInt("id")
                if (!resultArray.getJSONObject(i).getJSONObject("problem").has("contestId"))    continue
                if (!resultArray.getJSONObject(i).has("verdict"))   continue
                sub.verdict = resultArray.getJSONObject(i).getString("verdict")
                sub.problem.contestId = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getInt("contestId")
                sub.problem.index = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getString("index")
                sub.problem.name = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getString("name")
                if (resultArray.getJSONObject(i).getJSONObject("problem").has("rating")){
                    sub.problem.rating = resultArray.getJSONObject(i).getJSONObject("problem")
                        .getInt("rating")
                }
                else{
                    sub.problem.rating = 0
                }
                val tags = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getJSONArray("tags")
                (0 until tags.length()).forEach{j ->
                    sub.problem.tags.add(tags[j].toString())
                    user.addClass(tags[j].toString())
                }

                user.addSubmission(sub.problem.contestId.toString() + sub.problem.index, sub)
                user.lastSubmId = max(user.lastSubmId, sub.id)
            }
            println("user status received")
            return true
        }

        private fun userRating(): Boolean{
            val json = sendHTTPRequest("https://codeforces.com/api/user.rating?handle="
                    + user.getHandle())
            val jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED")    return false
            val resultArray = jsonObj.getJSONArray("result")
            (0 until resultArray.length()).forEach {i ->
                resultArray.getJSONObject(i)
                val ratingChangeObj = RatingChange()
                ratingChangeObj.contestId = resultArray.getJSONObject(i).getInt("contestId")
                ratingChangeObj.contestName = resultArray.getJSONObject(i).getString("contestName")
                ratingChangeObj.newRating = resultArray.getJSONObject(i).getInt("newRating")
                ratingChangeObj.rank = resultArray.getJSONObject(i).getInt("rank")
                user.ratingChangeList.add(ratingChangeObj)
            }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

            }

        })

        view.loginBtn.setOnClickListener {
            println("HIT")
            val email = emailLogin.text.toString()
            val password = passwordLogin.text.toString()
            if (email.isEmpty() or password.isEmpty())  return@setOnClickListener
            activity?.let {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(it){ task->
                    if (task.isSuccessful){
                        Log.d(TAG, "createUserWithEmail:success")
                        UserProfileRequest().execute()
                    }
                    else{
                        Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        view.passwordReset.setOnClickListener {
            val email = emailLogin.text.toString()
            if (email.isEmpty())    return@setOnClickListener
            auth.sendPasswordResetEmail(email).addOnCompleteListener{
                task ->
                if (task.isSuccessful){
                    Toast.makeText(context, "Check your inbox", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context, "Password reset email could not be sent", Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
    }

    private fun startMainActivity(){
        val intent = Intent(activity, MainActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_USER, user)
        }
        startActivity(intent)
    }

}
