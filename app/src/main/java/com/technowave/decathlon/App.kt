package com.technowave.decathlon

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.technowave.decathlon.keyboard.NordicForegroundService
import com.technowave.decathlon.utils.Constants
import com.technowave.techno_rfid.Beeper
import com.technowave.techno_rfid.NordicId.*
import com.technowave.techno_rfid.NurApi.BleScanner
import com.technowave.techno_rfid.NurApi.NurApiAutoConnectTransport
import com.technowave.techno_rfid.NurApi.NurDeviceSpec
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Boolean.TRUE
import java.util.Timer

@HiltAndroidApp
class App : Application(), NurApiListener {


    interface NordicStatusListener {
        fun readerStatus(status: String, statusColor: Int, isConnected: Boolean)
        fun onTagRead(epc: String, totalInventory: Int)

    }

    companion object {

        private var timerRep: Timer? = null


        var READING = TRUE


        const val SERVICE_NOTIFICATION_ID = 1
        const val SERVICE_CHANNEL_ID = "TechnoServiceChannel"
        const val SERVICE_CHANNEL_NAME = "Techno Service Channel"

        // New method to start the foreground service
        fun startForegroundService(context: Context) {
            val serviceIntent = Intent(context, NordicForegroundService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }


        lateinit var myObject: App.NordicStatusListener
        const val BROADCAST_ACTION_TAG_READ = "com.technowave.flipkart.BROADCAST_TAG_READ"

        //Need to keep track when barcode mScanning or mAiming ongoing in case it cancelled by trigger press or program leave
        private var mScanning = false
        private var mAiming = false
        var nordicStatus = ""
        var nordicstatusColor = 0
        private const val NORDIC_PREF = "nordic_pref"
        const val SCANNER_ID = "scanner_id"
        var mTriggerDown = false
        var mTagsAddedCounter = 0
        lateinit var nordicStatusListener: NordicStatusListener
        var scannerSpecString = ""
        lateinit var hAcTr: NurApiAutoConnectTransport
        lateinit var mNurApi: NurApi
        lateinit var mAccExt: AccessoryExtension
        var mIsConnected = false
        var mIsAccessorySupported = false
        var mUiConnStatusText = ""
        var mUiConnStatusTextColor = 0
        lateinit var sharedPrefManager: SharedPreferences
        fun showConnecting() {
            mUiConnStatusText = "Connecting to " + hAcTr?.address
            updateReaderStatus()
        }

        private fun updateReaderStatus() {
            nordicStatusListener.readerStatus(
                mUiConnStatusText,
                mUiConnStatusTextColor,
                mIsConnected
            )
        }

        fun stopReader() {
            try {
                if (mNurApi.isInventoryStreamRunning) mNurApi.stopInventoryStream()
                mTriggerDown = false
            } catch (ex: Exception) {
                mUiConnStatusText = ex.message.toString()
                mUiConnStatusTextColor = Color.RED
                updateReaderStatus()
            }
        }


        var job: Job?=null
        fun startReader() {

            CoroutineScope(Dispatchers.IO).launch {
                if (mTriggerDown) {
                    return@launch
                }
                try {
                    mNurApi.clearIdBuffer()
                    mNurApi.startInventoryStream()
                    mTriggerDown = true
                    mTagsAddedCounter = 0
                } catch (ex: Exception) {
                    mUiConnStatusText = ex.message.toString()
                    mUiConnStatusTextColor = Color.RED
                    updateReaderStatus()
                }
            }
        }

        fun disconnectScanner() {
            if (mNurApi.isConnected) {
                hAcTr?.dispose()
                // hAcTr =null
            }
        }


        private const val TAG = "MSD"
    }

    open fun connectToReader() {
        disconnectScanner()
        scannerSpecString = sharedPrefManager.getString(Constants.SCANNER_ID, "").toString()
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

    override fun onCreate() {
        super.onCreate()
        Beeper.init(this)
        Beeper.setEnabled(true)
        BleScanner.init(this)
        mIsConnected = false
        mNurApi = NurApi()
        mAccExt = AccessoryExtension(mNurApi)
        mNurApi.listener = this
        mUiConnStatusText = "Disconnected!"
        mUiConnStatusTextColor = Color.RED
        sharedPrefManager = getSharedPreferences(NORDIC_PREF, Context.MODE_PRIVATE)
       // mAccExt.registerBarcodeResultListener(mBarcodeResult)
        nordicStatusListener = nordicCallback
        startForegroundService(this)

    }

    private val nordicCallback = object : NordicStatusListener {
        override fun readerStatus(status: String, statusColor: Int, isConnected: Boolean) {
            nordicStatus = status
            nordicstatusColor = statusColor
        }

        override fun onTagRead(epc: String, totalInventory: Int) {

        }


    }


    override fun logEvent(p0: Int, p1: String?) {

    }

    override fun connectedEvent() {
        try {
            if (mAccExt.isSupported) {
                mIsAccessorySupported = true
                mUiConnStatusText = "Connected to " + mAccExt.config.name
            } else {
                mIsAccessorySupported = false
                val ri: NurRespReaderInfo = mNurApi.readerInfo
                mUiConnStatusText = "Connected to " + ri.name
            }
        } catch (ex: Exception) {
            mUiConnStatusText = ex.message.toString()
        }
        mIsConnected = true
        Log.i(TAG, "Connected!")
        Beeper.beep(Beeper.BEEP_100MS)
        mUiConnStatusTextColor = Color.GREEN
        sharedPrefManager.edit { putString(SCANNER_ID, scannerSpecString) }
        updateReaderStatus()
    }


    override fun disconnectedEvent() {
        mIsConnected = false
        Log.i(TAG, "Disconnected!")
        mUiConnStatusText = "Reader disconnected"
        mUiConnStatusTextColor = Color.RED
        showConnecting()
    }

    override fun bootEvent(p0: String?) {}

    override fun inventoryStreamEvent(event: NurEventInventory) {
        try {
            if (event.stopped) {
                if (mTriggerDown) mNurApi.startInventoryStream()
            } else {
                if (event.tagsAdded > 0) {
                    if (mIsAccessorySupported) mAccExt.beepAsync(20)
                    else Beeper.beep(Beeper.BEEP_40MS)
                    val tagStorage: NurTagStorage = mNurApi.storage
                    for (x in mTagsAddedCounter until mTagsAddedCounter + event.tagsAdded) {
                        var epcString: String
                        val tag = tagStorage[x]
                        epcString = NurApi.byteArrayToHexString(tag.epc)
                        myObject.onTagRead(epcString, 0)

                    }

                    //just for testing purpose only
                    CoroutineScope(Dispatchers.IO).launch {
                            stopReader()
                            startReader() }

                    mTagsAddedCounter += event.tagsAdded

                }
            }
        } catch (ex: Exception) {
            mUiConnStatusText = "RFID read error: ${ex.message}"
            mUiConnStatusTextColor = Color.RED
            updateReaderStatus()
        }
    }



    override fun IOChangeEvent(p0: NurEventIOChange) {
        Log.i(TAG, "Key " + p0.source)
        handleIOEvent(p0)
    }

    override fun traceTagEvent(p0: NurEventTraceTag?) {}

    override fun triggeredReadEvent(p0: NurEventTriggeredRead?) {}

    override fun frequencyHopEvent(p0: NurEventFrequencyHop?) {}

    override fun debugMessageEvent(p0: String?) {}

    override fun inventoryExtendedStreamEvent(p0: NurEventInventory?) {}

    override fun programmingProgressEvent(p0: NurEventProgrammingProgress?) {}

    override fun deviceSearchEvent(p0: NurEventDeviceInfo?) {}

    override fun clientConnectedEvent(p0: NurEventClientInfo?) {}

    override fun clientDisconnectedEvent(p0: NurEventClientInfo?) {}

    override fun nxpEasAlarmEvent(p0: NurEventNxpAlarm?) {}

    override fun epcEnumEvent(p0: NurEventEpcEnum?) {}

    override fun autotuneEvent(p0: NurEventAutotune?) {}

    override fun tagTrackingScanEvent(p0: NurEventTagTrackingData?) {}

    override fun tagTrackingChangeEvent(p0: NurEventTagTrackingChange?) {}


    private fun handleIOEvent(event: NurEventIOChange) {
        Log.i(
            "BARCODE_MSD",
            "BarcodeKey " + event.source.toString() + " Dir=" + event.direction
        )
        try {
            if (event.source == 100 && event.direction == 1) {
                if (mScanning) {
                    //There is mScanning ongoing so we need just abort it
                    mAccExt.cancelBarcodeAsync()
                    Log.i(TAG, "Cancelling..")
                } else {
                    mAiming = true
                    mAccExt.imagerAIM(mAiming)
                    //mUiStatusText = "Aiming..."
                }
            } else if (event.source == 100 && event.direction == 0) {
                if (mScanning) {
                    mScanning = false
                    return
                }
                //Trigger released. Stop aiming and start mScanning
                mAiming = false
                mAccExt.imagerAIM(mAiming)
                mAccExt.readBarcodeAsync(5000) //5 sec timeout
                //mUiStatusText = "Scanning barcode..."
                mScanning = true
            } else if (event.source == 101) {
                if (event.direction == 0) {
                    Log.d(TAG, "Power button released")
                } else {
                    Log.d(TAG, "Power button pressed")
                }
            } else if (event.source == 102) {
                if (event.direction == 0) {
                    Log.d(TAG, "Unpair button released")
                } else {
                    Log.d(TAG, "Unpair button pressed")
                }
            }
        } catch (ex: Exception) {
            //Show error on status field
            Log.d(TAG, ex.message.toString())
        }
    }




}







