package com.technowave.decathlon
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.technowave.decathlon.App.Companion.SCANNER_ID
import com.technowave.decathlon.App.Companion.disconnectScanner
import com.technowave.decathlon.App.Companion.hAcTr
import com.technowave.decathlon.App.Companion.mNurApi
import com.technowave.decathlon.App.Companion.scannerSpecString
import com.technowave.decathlon.App.Companion.sharedPrefManager
import com.technowave.decathlon.App.Companion.showConnecting
import com.technowave.techno_rfid.NurApi.NurDeviceListActivity
import com.technowave.techno_rfid.NurApi.NurDeviceSpec
import com.technowave.techno_rfid.SettingsUI.NordicSettingsActivity



open class BaseActivity : AppCompatActivity() {
    protected fun openScannerSettings() {
        if (mNurApi.isConnected) disconnectScanner()
        NurDeviceListActivity.startDeviceRequest(this, mNurApi)
    }

    protected fun nordicScannerSettings() {
        if (mNurApi.isConnected) disconnectScanner()
        startActivity(Intent(this, NordicSettingsActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            NurDeviceListActivity.REQUEST_SELECT_DEVICE -> {
                if (data == null || resultCode != NurDeviceListActivity.RESULT_OK) return
                try {
                    scannerSpecString =
                        data.getStringExtra(NurDeviceListActivity.SPECSTR).toString()
                    Log.i(Companion.TAG, "Spec string = $scannerSpecString")
                    val spec = NurDeviceSpec(scannerSpecString)
                    Log.i(TAG, "Dispose transport")
                    if (mNurApi.isConnected) disconnectScanner()
                    hAcTr = NurDeviceSpec.createAutoConnectTransport(this, mNurApi, spec)
                    val strAddress: String = spec.address
                    Log.d(TAG, "Dev selected: code = $strAddress")
                    hAcTr?.address = strAddress
                    showConnecting()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i(TAG, "error ${e.message}")
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

     open fun connectToReader() {
        disconnectScanner()
        scannerSpecString = sharedPrefManager.getString(SCANNER_ID, "").toString()
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val spec = NurDeviceSpec(scannerSpecString)
                Log.i(TAG, "Dispose transport")
                hAcTr = NurDeviceSpec.createAutoConnectTransport(this, mNurApi, spec)
                val strAddress: String = spec.address
                Log.i(TAG, "Dev selected: code = $strAddress")
                hAcTr?.address = strAddress
                showConnecting()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(TAG, "error ${e.message}")
            }
        }, 1000)
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}