package tech.httptoolkit.myapplication

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter

object RootAccessManager {

    fun requestRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            process.waitFor() == 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun executeCommandWithRoot(command: String): Boolean {
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
