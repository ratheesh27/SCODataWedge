package com.technowave.decathlon


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.technowave.decathlon.App.Companion.hAcTr
import com.technowave.decathlon.App.Companion.mNurApi
import com.technowave.decathlon.App.Companion.nordicStatusListener
import com.technowave.decathlon.App.Companion.scannerSpecString
import com.technowave.decathlon.App.Companion.showConnecting
import com.technowave.decathlon.databinding.*
import com.technowave.decathlon.fragment.EpcListener
import com.technowave.decathlon.keyboard.NordicForegroundService
import com.technowave.decathlon.storage.SharedPrefManager
import com.technowave.decathlon.utils.WaitDialog
import com.technowave.decathlon.utils.showToast
import com.technowave.decathlon.viewModels.MainViewModel
import com.technowave.techno_rfid.NurApi.NurDeviceSpec
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), App.NordicStatusListener {

    lateinit var binding: ActivityMainBinding
    lateinit var popupWindow: PopupWindow
    lateinit var waitDialog: WaitDialog

    var DATA = ""


    @Inject
    lateinit var sharedPrefManager: SharedPrefManager
    val viewModel: MainViewModel by viewModels()
    private lateinit var menu: LayoutPopupBinding

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MSD", "onCreate")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        waitDialog = WaitDialog(this)
        initialization()
        setupPopUp()
        checkPermissions()
        if (isPortAvailable(HttpServerPORT)) {
            showToast("Port Available")
        } else {
            showToast("Port Not Available")
        }

        Thread(HttpServerThread()).start()


        nordicStatusListener = this
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding.tvSettings.setOnClickListener {
            popupWindow.showAsDropDown(it)
        }

        binding.toolbar.imgBack.setOnClickListener {
            onBackPressed()
        }


    }


    private fun initialization() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    }


    override fun readerStatus(status: String, statusColor: Int, isConnected: Boolean) {
        Log.d("EPC_status", status)
        runOnUiThread {
            binding.tvReaderStatus.run {
                text = status
                setTextColor(statusColor)
            }

        }

    }

    lateinit var epcListner: EpcListener
    override fun onTagRead(epc: String, totalInventory: Int) {
       // logMessage(epc)
    }


    private fun setupPopUp() {

        val view = layoutInflater.inflate(
            R.layout.layout_popup,
            window.decorView.findViewById(android.R.id.content),
            false
        )
        menu = LayoutPopupBinding.bind(view)
        menu.menuExit.setOnClickListener {
            val stopServiceIntent = Intent(this, NordicForegroundService::class.java)
            stopService(stopServiceIntent)
            exitApp()
        }

        menu.menuPower.setOnClickListener {
            nordicScannerSettings()
        }

        menu.menuConnect.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //  openScanner()
                showProgressBar()
                openScanner()
                //  tryToConnect()


            }
        }
        menu.menuSettings.setOnClickListener {
            showBaseUrlSettings()
        }



        popupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    fun exitApp() {
        showConfirmDialog("", "Are you sure you want to Exit", action = "exit") {
            if (it) {
                finish()
            }

        }
    }


    fun onMenuClick(view: View) {
        popupWindow.showAsDropDown(view)
    }


    private fun openScanner() {
        showProgressBar()
        openScannerSettings()
        hideProgressBar()
    }

    fun showProgressBar() = runOnUiThread { waitDialog.showDialog() }
    fun hideProgressBar() = runOnUiThread { waitDialog.dismiss() }


    fun showStatus() {
        /**runOnUiThread {
        binding.tvReaderStatus.run {
        text = "connected"
        setTextColor(R.color.teal_green)
        }
        }**/
    }


    override fun onStop() {
        /**   if (NordicApp.mIsConnected) {
        NordicApp.stopReader()
        NordicApp.disconnectScanner()
        }**/
        super.onStop()
    }


    override fun onStart() {
        /**
        CoroutineScope(Dispatchers.IO).launch {
        if (!NordicApp.mIsConnected)
        //  tryToConnect()
        connectToReader()
        }**/
        // super.onResume()
        super.onStart()

    }


    /**
     *    NordicApp.startReader()
    NordicApp.stopReader()
     */

    fun showConfirmDialog(
        title: String = "Rfid App",
        message: String = "",
        action: String,
        missing: String = "0",
        operation: (v: Boolean) -> Unit
    ) {
        Log.d("MSD_action", action)
        val exitDialogBuilder = MaterialAlertDialogBuilder(this)
        val exitDialogView =
            LayoutInflater.from(this).inflate(R.layout.layout_confirm, null)
        val dialog = LayoutConfirmBinding.bind(exitDialogView)
        when (action) {
            "download" -> {
                dialog.tvMessage.text = "$message"
            }

            "upload" -> {
                dialog.tvMessage.text = "$message"
            }

            "delete" -> {
                dialog.tvMessage.text = "$message"
            }

            "exit" -> {
                dialog.tvMessage.text = "$message ?"
                dialog.btnOk.setText("YES")
                dialog.btnCancel.visibility = View.VISIBLE
            }

            "red" -> {
                dialog.tvTitle.setTextColor(Color.parseColor("#11384A"))
                dialog.tvTitle.text = title
                dialog.btnCancel.visibility = View.GONE
                dialog.btnOk.text = "OK"
                dialog.tvMessage.text = "$message ($missing)"
                dialog.tvMessage.setTextColor(Color.RED)
                dialog.btnOk.setBackgroundColor(Color.RED)
            }

            else -> {
                dialog.tvTitle.setTextColor(Color.parseColor("#11384A"))
                dialog.tvTitle.text = title
                dialog.btnCancel.visibility = View.GONE
                dialog.btnOk.text = "OK"
                dialog.tvMessage.text = "$message"
            }


        }


        exitDialogBuilder.setView(dialog.root)
        exitDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)
        exitDialogBuilder.setCancelable(false)
        val exitDialog = exitDialogBuilder.create()
        dialog.btnOk.setOnClickListener {
            if (action == "save") {
                operation(true)
                // showToast("ok")
            } else if (action == "download")
                operation(true)
            else if (action == "upload")
                operation(true)
            else if (action == "exit")
                operation(true)
            else if (action == "delete")
                operation(true)
            else if (action == "ok")
                operation(true)
            else if (action == "red")
                operation(true)
            else if (action == "goBackToSelection") {
            }

            exitDialog.dismiss()

        }

        dialog.btnCancel.setOnClickListener {
            if (action == "2")
                operation(false)
            if (action == "save") {
                operation(false)
                // showToast("ok")
            }
            exitDialog.dismiss()

        }
        exitDialog.show()
    }


    fun showLoginDialog(
        title: String = "Rfid App",
        message: String = "",
        action: String,
        view: View,
        operation: (v: Boolean) -> Unit
    ) {
        Log.d("MSD_action", action)
        val exitDialogBuilder = MaterialAlertDialogBuilder(this)
        val exitDialogView =
            LayoutInflater.from(this).inflate(R.layout.layout_login, null)
        val dialog = LayoutLoginBinding.bind(exitDialogView)
        when (action) {


        }


        exitDialogBuilder.setView(dialog.root)
        exitDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)
        exitDialogBuilder.setCancelable(false)
        val exitDialog = exitDialogBuilder.create()
        dialog.buttonLogin.setOnClickListener {


        }

        dialog.btnCancel.setOnClickListener {
            exitDialog.dismiss()
        }
        exitDialog.show()
    }


    override fun onBackPressed() {}


    private fun showBaseUrlSettings() {
        val settingsDialogBuilder = MaterialAlertDialogBuilder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_api_settings, null)
        val dialog = LayoutApiSettingsBinding.bind(view)
        settingsDialogBuilder.setView(dialog.root)
        settingsDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)
        settingsDialogBuilder.setCancelable(false)
        val baseUrlSettingsDialog = settingsDialogBuilder.create()
        dialog.baseUrl.setText(sharedPrefManager.getBaseUrl().toString().trim())
        dialog.customerID.setText(sharedPrefManager.getStoreCode().toString().trim())

        dialog.buttonCancel.setOnClickListener { baseUrlSettingsDialog.dismiss() }
        dialog.buttonSave.setOnClickListener {
            val baseUrl = dialog.baseUrl.text.toString().trim()
            val locationID = dialog.customerID.text.toString().trim()
            if (baseUrl.isEmpty()) {
                dialog.baseUrl.error = "Please Enter Base Url"
                return@setOnClickListener
            } else if (locationID.isEmpty()) {
                dialog.customerID.error = "Please Enter Location ID"
                return@setOnClickListener
            } else {
                dialog.baseUrl.error = null
                dialog.customerID.error = null
                sharedPrefManager.setBaseUrl(baseUrl)
                sharedPrefManager.setStoreCode(locationID)
                baseUrlSettingsDialog.dismiss()
                //  showToast(sharedPrefManager.getBaseUrl().toString().trim())
            }
        }
        baseUrlSettingsDialog.show()
    }

    private fun tryToConnect() {
        try {
            scannerSpecString = "type=INT;addr=integrated_reader;name=Integrated Reader"
            // Log.d(TAG, "Spec string = $scannerSpecString")
            val spec = NurDeviceSpec(scannerSpecString)
            println("Dispose transport")
            if (mNurApi.isConnected) hAcTr.dispose()
            hAcTr = NurDeviceSpec.createAutoConnectTransport(this, mNurApi, spec)
            val strAddress: String = spec.address
            // Log.d(TAG, "Dev selected: code = $strAddress")
            hAcTr.address = strAddress
            showConnecting()
            //If you want connect to same device automatically later on, you can save 'strAddress" and use that for connecting at app startup for example.
            //saveSettings(spec);
        } catch (e: Exception) {
            e.printStackTrace()
            // Log.d(TAG, "error ${e.message}")
        }
        hideProgressBar()
    }

    fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun checkPermissions() {
        val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.ACCESS_WIFI_STATE
        )

        val PERMISSIONS_LOCATION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
        )

        val permissionStorage =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionLocation =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
        val permissionWifiState =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            // We don't have storage permission, so prompt the user
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                1
            )
        } else if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            // We have storage permission but not location permission, prompt for location
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_LOCATION,
                1
            )
        } else if (permissionWifiState != PackageManager.PERMISSION_GRANTED) {
            // We have location permission but not wifi state permission, prompt for wifi state
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_WIFI_STATE),
                1
            )
        }
    }

    /** private fun checkPermissions() {

    var PERMISSIONS_STORAGE: Array<String> = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_PRIVILEGED
    )
    val PERMISSIONS_LOCATION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_PRIVILEGED
    )


    val permission1 =
    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val permission2 =
    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
    if (permission1 != PackageManager.PERMISSION_GRANTED) {
    // We don't have permission so prompt the user
    ActivityCompat.requestPermissions(
    this,
    PERMISSIONS_STORAGE,
    1
    )
    } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(
    this,
    PERMISSIONS_LOCATION,
    1
    )
    }
    }**/

    fun setTitle(title: String) {
        binding.toolbar.title.text = title
    }

    fun settings() {
        nordicScannerSettings()
    }


    private lateinit var httpServerSocket: ServerSocket
    private val msgLog = StringBuilder()
    private var readingStarted = false
    var HttpServerPORT = 1234

   var count=0
    private inner class HttpServerThread : Thread() {
        override fun run() {
            try {
                httpServerSocket = ServerSocket(HttpServerPORT)
                show("HTTPSERVER STARTED, PORT :$HttpServerPORT")
                while (true) {
                    count++
                    logMessage("round$count")
                    val clientSocket = httpServerSocket.accept()
                    HttpResponseThread(clientSocket, "").start()
                }
            } catch (e: IOException) {
                show("HTTPSERVER ERROR " + e.message)
            }
        }
    }


    private inner class HttpResponseThread(private val socket: Socket, private val h1: String) : Thread() {
        lateinit var startJob:Job
        override fun run() {
            try {
                val request = BufferedReader(InputStreamReader(socket.getInputStream())).readLine()
                val os = PrintWriter(socket.getOutputStream(), true)
                val response = "<html><head></head><body><h1>$h1</h1></body></html>"
                os.print("HTTP/1.0 200 OK\r\n")
                os.print("Access-Control-Allow-Origin: *\r\n")
                os.print("Access-Control-Allow-Headers: *\r\n")
                os.print("Access-Control-Allow-Methods:*\r\n")
                os.print("Content type: application/json\r\n")
                os.print("Content length: ${response.length}\r\n")
                os.print("Connection: close\r\n")
                os.print("\r\n")
                os.print("$response\r\n")
                os.flush()
                socket.close()
            //    showToast("Request of $request from ${socket.inetAddress.toString()}")
             //   showToast("HTTP-RESPONSE : $request")
                CoroutineScope(Dispatchers.IO).launch {
                    val part2 = request.split("/")[1]
                    if (part2 != null) {
                        when {
                            part2.equals("StartReading HTTP", ignoreCase = true) ->{
                                show("Reading Started")
                                Log.d("MSD_oops","status")
                             CoroutineScope(Dispatchers.IO).launch {
                                    App.startReader()
                                    delay(500)
                                }
                            }
                            part2.equals("StopReading HTTP", ignoreCase = true) -> {
                                CoroutineScope(Dispatchers.IO).launch {
                                        App.stopReader()
                                        delay(10)
                                }
                                show("Reading Stopped")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                show("HTTPRESPONSE ERROR " + e.message)
            }
        }
    }

    private fun startButton() {
        // Implement your start button logic here
        readingStarted = true
    }

    private fun stopButton() {
        // Implement your stop button logic here
        readingStarted = false
    }

    private fun appendLog(log: String, tag: String) {
        // Implement your log appending logic here
    }

    private fun getLocalIpAddress(): String {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager?
        val ipAddress = wifiManager?.connectionInfo?.ipAddress ?: 0
        return InetAddress.getByAddress(
            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipAddress).array()
        ).hostAddress
    }


    private fun isPortAvailable(port: Int): Boolean {
        var serverSocket: ServerSocket? = null
        return try {
            serverSocket = ServerSocket(port)
            true
        } catch (e: Exception) {
            false
        } finally {
            serverSocket?.close()
            Thread.sleep(100)
        }
    }

    private fun logMessage(msg: String) {
        runOnUiThread {
            binding.logTxt.append("$msg\n")
        }
    }

    private fun show(message:String){
        runOnUiThread {
            showToast(message)
            logMessage(message)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            httpServerSocket.close()
            show("HTTPSERVER CLOSED")
        } catch (e: IOException) {
            show("Error while closing HTTP server: ${e.message}")
        }
    }

}



