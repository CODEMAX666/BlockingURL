package tech.httptoolkit.myapplication

import android.content.Context
import java.io.DataOutputStream

import android.widget.Toast
import java.io.PrintWriter
import java.io.StringWriter

object HostsFileEditor {

    fun blockWebsite(domain: String): Boolean {
        //val command = "echo '127.0.0.1 $domain' >> /etc/hosts"
        val command = "su -c 'mount -o rw,remount /system && echo \"127.0.0.1      $domain\" >> /system/etc/hosts && mount -o ro,remount /system'"
        executeShellCommand(command)
        var command1 = "su -c 'mount -o rw,remount /system && echo \"::1      $domain\" >> /system/etc/hosts && mount -o ro,remount /system'"
        return executeShellCommand(command1)

    }

    fun enableWebsite(domain: String): Boolean {
       // val command = "su -c 'sed -i \"/$domain/d\" /system/etc/hosts'"
        val command = "su -c 'mount -o rw,remount /system && sed -i \"/$domain/d\" /system/etc/hosts && mount -o ro,remount /system'"

        return executeShellCommand(command)
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun executeShellCommand(command: String): Boolean {
        return try {

            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            outputStream.writeBytes("$command\n")
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            process.waitFor() == 0
        } catch (e: Exception) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            val stackTraceString = sw.toString()
            e.printStackTrace()
            false
        }
    }
}
