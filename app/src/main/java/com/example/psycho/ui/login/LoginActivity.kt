package com.example.psycho.ui.login

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.psycho.MainActivity
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.data.MyDatabaseHelper
import com.example.psycho.databinding.ActivityLogin2Binding
import com.example.psycho.simplePostUseTo
import java.io.IOException


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registerViewModel: LoginViewModel
    private lateinit var binding: ActivityLogin2Binding
    //var mysqlhelper: SQLiteOpenHelper? = null

    private val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS,
        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
    )

    override fun onKeyDown(keyCode: Int, event:KeyEvent): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.repeatCount ==0)
        {
            Log.d("Back","Catch")

            finish()
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, permissions, 321)

        val dbHelper=MyDatabaseHelper(this,"Pku-Eater.db",1)
        dbHelper.writableDatabase
        val db=dbHelper.writableDatabase
        val userRoot=ContentValues().apply {
            put("uid",1)
            put("name","Root")
            put("weight",50)
            put("height",170)
            put("state",0)
            put("birthday","1111-11-11")
            put("gender",1)
            put("avoidance",0)
        }
        db.insert("User",null,userRoot)
        val cursor=db.query("User",null,null,null,null,null,null)
        if (cursor.moveToFirst()){
            do {
                val name=cursor.getString(cursor.getColumnIndex("name"))
                Log.d("SQL",name)
            }
                while (cursor.moveToNext())
        }
        cursor.close()
        db.close()


        var globalFile=Data
        //globalFile.update(this,"weight","100")
        val result=globalFile.query(this,"weight")
        Log.d("SQL",result)

        val map = mapOf("name" to globalFile.getUserName(applicationContext), "password" to globalFile.getPassword(applicationContext))
        val url = "http://47.94.139.212:3000/user/login"
        simplePostUseTo(url, map)
        if (globalFile.getState()=="success")
        {
            val intentL:Intent = Intent(this, MainActivity::class.java)
            startActivity(intentL)
        }
        /*
        if((globalFile.getLoginFlag())==true)
        {
            val map = mapOf("name" to globalFile.getUserName(), "password" to globalFile.getPassword())
            val url = "http://47.94.139.212:3000/user/login"
            simplePostUseFrom(url, map)
            if (globalFile.getState()=="fail")
            {
                throw IOException("Error Login")
            }
            val intentL:Intent = Intent(this, MainActivity::class.java)
            startActivity(intentL)
        }
        */
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        if((globalFile.getLoginFlag(applicationContext))==false)
        {
            setContentView(binding.root)
        }
        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val register=binding.register


        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        registerViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        registerViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register!!.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {

                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                setResult(RESULT_OK)
                var globalFile=Data
                val intentL: Intent = Intent(this, MainActivity::class.java)
                Log.d("Main","Start")
                startActivity(intentL)

                finish()
            }

            //Complete and destroy login activity once successful
        })

        registerViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {

                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                setResult(RESULT_OK)
                finish()
            }
            //Complete and destroy login activity once successful
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
        username.afterTextChanged {
            registerViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            afterTextChanged {
                registerViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString(),
                            false,this@LoginActivity
                        )
                }
                false
            }


            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString(),false,this@LoginActivity)
            }
            register!!.setOnClickListener {
                loading.visibility = View.VISIBLE
                registerViewModel.login(username.text.toString(), password.text.toString(),true,this@LoginActivity)
            }
        }

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {

        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}