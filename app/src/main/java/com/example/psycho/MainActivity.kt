package com.example.psycho
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.psycho.data.Data
import com.example.psycho.databinding.ActivityMainBinding
import com.example.psycho.ui.setting.usage.CountActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS,
        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
    )
    var intance: MainActivity? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 321) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("zzp", "没有获取权限" + permissions[i])
                } else {
                    Log.d("zzp", "获取权限成功" + permissions[i])
                }
            }
        }
    }

    private val _data = Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intance = this;
        val i = ContextCompat.checkSelfPermission(applicationContext, permissions[0])
        val j = ContextCompat.checkSelfPermission(applicationContext, permissions[1])
        val k = ContextCompat.checkSelfPermission(applicationContext, permissions[2])
        val l = ContextCompat.checkSelfPermission(applicationContext, permissions[3])
        val z = ContextCompat.checkSelfPermission(applicationContext, permissions[4])
        if (i != PackageManager.PERMISSION_GRANTED || j != PackageManager.PERMISSION_GRANTED ||
            k != PackageManager.PERMISSION_GRANTED || l != PackageManager.PERMISSION_GRANTED ||
            z!=  PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, permissions, 321)
        }

        if(_data.getFirstFlag(applicationContext)==1)
        {
            val intentL: Intent = Intent(this, CountActivity::class.java)
            Log.d("Count","Start")
            startActivity(intentL)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        // requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)


        //val button: Button = binding.loginButton
        //button.setOnClickListener {
         //   val intent = Intent(this,LoginActivity::class.java)
          //  startActivity(intent)
        //}
        
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_setting
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}