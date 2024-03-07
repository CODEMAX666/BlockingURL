package tech.httptoolkit.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import tech.httptoolkit.myapplication.ui.theme.MyApplicationTheme
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class MainActivity : ComponentActivity() {

    private val TAG = "HostsFileEditor"

    private lateinit var listViewBlockedSites: ListView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listViewBlockedSites = findViewById(R.id.listViewBlockedSites)

        refreshBlockedSitesList()

        if (RootAccessManager.requestRootAccess()) {
            // Root access granted
            Toast.makeText(this, "Root access granted", Toast.LENGTH_SHORT).show()
        } else {
            // Root access denied
            Toast.makeText(this, "Root access denied, Install Root Grant Apps.", Toast.LENGTH_SHORT).show()
            return;
        }

        val editTextSiteName = findViewById<EditText>(R.id.editTextSiteName)

        val buttonUnblock = findViewById<Button>(R.id.buttonUnblock)

        buttonUnblock.setOnClickListener {
            val siteName = editTextSiteName.text.toString().trim()
            if (siteName.isNotEmpty()) {
                val success = RootAccessManager.executeCommandWithRoot("su -c 'mount -o rw,remount /system && sed -i \"/$siteName/d\" /system/etc/hosts && mount -o ro,remount /system'")
                if (success) {
                    refreshBlockedSitesList()
                    Toast.makeText(this, "Site unblocked successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to unblock site", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a site name", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonBlock = findViewById<Button>(R.id.buttonBlock)
        buttonBlock.setOnClickListener {
            val siteName = editTextSiteName.text.toString().trim()
            if (siteName.isNotEmpty()) {
                val success = RootAccessManager.executeCommandWithRoot("su -c 'mount -o rw,remount /system && echo \"127.0.0.1      $siteName\" >> /system/etc/hosts && mount -o ro,remount /system'")
                if (success) {
                    refreshBlockedSitesList();
                    Toast.makeText(this, "Site blocked successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to block site", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a site name", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun refreshBlockedSitesList() {
        val blockedSites = getBlockedSites()
        val blockedAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, blockedSites)
        listViewBlockedSites.adapter = blockedAdapter
    }

    private fun getBlockedSites(): List<String> {
        val blockedSites = mutableListOf<String>()
        try {
            val file = File("/system/etc/hosts")
            val reader = BufferedReader(FileReader(file))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val domain = extractDomainFromLine(line!!)
                if (domain.isNotBlank() && line!!.contains("127.0.0.1")) {
                    blockedSites.add(domain)
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return blockedSites
    }

    private fun extractDomainFromLine(line: String): String {
        val parts = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
        return if (parts.size > 1) parts[1] else ""
    }

}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}