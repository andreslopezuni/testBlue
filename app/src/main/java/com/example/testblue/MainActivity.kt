package com.example.testblue

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dorlet.mobileaccess.sdk.MobileAccessContext
import com.dorlet.mobileaccess.sdk.domain.AccessAttemptResultInfo
import com.dorlet.mobileaccess.sdk.domain.BindResultInfo
import com.dorlet.mobileaccess.sdk.interfaces.DmaEventListener
import com.example.testblue.ui.theme.TestBlueTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CompletableFuture

class MainActivity : ComponentActivity() {
    private val dmaEventListener = object : DmaEventListener {
        override fun onAccessAttemptResult(p0: AccessAttemptResultInfo) {
            println("access attemp ${p0.result}")
        }

        override fun onCredentialCreated(p0: String?) {
            println("Credential created")
        }

        override fun onCredentialUpdated(p0: String?) {
            println("Credential updated")
        }

        override fun onCredentialDeleted(p0: String?) {
            println("Credential delete")
        }

        override fun onBindResult(p0: BindResultInfo?) {
            println("ONBIND: ${p0?.result}");
            executeForLoop()
        }
    }

    var mobileAccessContext: MobileAccessContext = MobileAccessContext.init(dmaEventListener)
    val url: String = "http://10.0.26.189:8081/DASS/AccessControl/Acreditations/F0000011/DMAInviteCode"
    val key: String = "Authorization"
    val baseValue: String = "Basic SW50ZWdyYWNpb25lczpJbnRlZypSM3N0"
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBluetoothPermissions()
        setContent {
            TestBlueTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
    private fun checkBluetoothPermissions() {
        val requiredPermissions = arrayOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        )

        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        } else {
            checkBluetoothEnabled()
        }
    }

    private fun checkBluetoothEnabled() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no está disponible en este dispositivo", Toast.LENGTH_SHORT).show()
        } else if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothLauncher.launch(enableBtIntent)
        } else {
            println("LOGICA DE LA APP")
        }
    }

    private val bluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth activado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Se requiere Bluetooth para usar esta aplicación", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                checkBluetoothEnabled()
            } else {
                Toast.makeText(this, "Se requieren permisos para usar Bluetooth", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1
    }

    fun executeForLoop() {
        println("credentials:")
        println(mobileAccessContext.getCredentials())
        for (credential in mobileAccessContext.getCredentials()){
            // println("size: ${credential.getButtonReaders().size}")
            // println("SYNC: ${mobileAccessContext.isSyncOnAccessEvent}")
            println("button readers ${credential.getButtonReaders()}")
            val button = credential.getButtonReaders()[59]
            button.open()
            // println("butonreaders: ${credential.getButtonReaders().toString()}")
            /*for (buttonReader in credential.getButtonReaders()){
              println("button reader: ${buttonReader.name}")
              buttonReader.open()
            }*/
        }
    }

    fun testBlue() {
        this.mobileAccessContext.bind("eyJhdXQiOjEsInRrIjoiNGJDRjBVQnpvMFk9IiwiZXhwIjoxNzE2NTU3NTA4LCJhdWQiOiJodHRwczovL21vYmlsZWFjY2Vzcy1kZXYuc2VydmljZWJ1cy53aW5kb3dzLm5ldC8wMDAyNzAyOC8ifQ.McXrSMXkx95L8tpHg5OO8APYSa0esZFreqJD1cUpwuo")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Button(onClick = {
        val mainActivity = MainActivity()
        mainActivity.testBlue()
    },) {
        Text(text = "test")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestBlueTheme {
        Greeting("Android")
    }
}