package com.technowave.decathlon.keyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Toast
import com.technowave.decathlon.App
import com.technowave.decathlon.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class MyInputMethodService : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private var keyboardView: KeyboardView? = null
    private var keyboard: Keyboard? = null
    private var caps = false
    private var action = false
    private var DATA = ""
    override fun onPress(p0: Int) {
        Log.d("MSD", "working_onPress")
    }

    override fun onRelease(p0: Int) {
        Log.d("MSD", "working_onRelease")
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        Log.d("MSD", "on-$primaryCode")
        val inputConnection = currentInputConnection
        if (inputConnection != null) {
            when (primaryCode) {
                Keyboard.KEYCODE_DELETE -> {
                    val selectedText = inputConnection.getSelectedText(0)
                    if (TextUtils.isEmpty(selectedText)) {
                        inputConnection.deleteSurroundingText(1, 0)
                    } else {
                        inputConnection.commitText("", 1)
                    }
                    caps = !caps
                    keyboard!!.isShifted = caps
                    keyboardView!!.invalidateAllKeys()
                }

                Keyboard.KEYCODE_SHIFT -> {
                    caps = !caps
                    keyboard!!.isShifted = caps
                    keyboardView!!.invalidateAllKeys()
                }

                Keyboard.KEYCODE_DONE -> inputConnection.sendKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER
                    )
                )

                1001 -> {
                    action = true
                    Log.d("MSD", "RFID")
                    CoroutineScope(Dispatchers.IO).launch {
                        if (App.mNurApi.isConnected) {
                            if (!App.mNurApi.isInventoryStreamRunning) {
                                App.startReader()
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(
                                        applicationContext,
                                        "Started",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(
                                        applicationContext,
                                        "Already Running!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    applicationContext,
                                    "Please connect Reader!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                }

                1002 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (App.mNurApi.isConnected) {
                            if (App.mNurApi.isInventoryStreamRunning) {
                                App.stopReader()
                                //  NordicApp.stopTimer()
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(
                                        applicationContext,
                                        "Already stopped!",
                                         Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    applicationContext,
                                    "please connect Reader First!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                else -> {
                    var code = primaryCode.toChar()
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code)
                    }
                    inputConnection.commitText(code.toString(), 1)
                }
            }
        }

    }

    @Synchronized
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)



    }




    private val handler = Handler(Looper.getMainLooper())
    private val DELAY_MILLIS = 100

    @Synchronized
    private fun handleTagEvent(inputConnection: InputConnection, epc: String) {
        try {
            val mes: String = epc.toString().trim()
            if (mes != null) {
                for (i in 0 until mes.length) {
                    val ch = mes[i]
                    handleKeyEvent(inputConnection, ch)
                      handler.postDelayed({

                    }, (i * DELAY_MILLIS).toLong())

                }

                inputConnection.sendKeyEvent(KeyEvent(0, 66))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun handleKeyEvent(inputConnection: InputConnection, ch: Char) {
        when (ch) {
            '0' -> inputConnection.sendKeyEvent(KeyEvent(0, 7))
            '1' -> inputConnection.sendKeyEvent(KeyEvent(0, 8))
            '2' -> inputConnection.sendKeyEvent(KeyEvent(0, 9))
            '3' -> inputConnection.sendKeyEvent(KeyEvent(0, 10))
            '4' -> inputConnection.sendKeyEvent(KeyEvent(0, 11))
            '5' -> inputConnection.sendKeyEvent(KeyEvent(0, 12))
            '6' -> inputConnection.sendKeyEvent(KeyEvent(0, 13))
            '7' -> inputConnection.sendKeyEvent(KeyEvent(0, 14))
            '8' -> inputConnection.sendKeyEvent(KeyEvent(0, 15))
            '9' -> inputConnection.sendKeyEvent(KeyEvent(0, 16))
            'A', 'a' -> inputConnection.sendKeyEvent(KeyEvent(0, 29))
            'B', 'b' -> inputConnection.sendKeyEvent(KeyEvent(0, 30))
            'C', 'c' -> inputConnection.sendKeyEvent(KeyEvent(0, 31))
            'D', 'd' -> inputConnection.sendKeyEvent(KeyEvent(0, 32))
            'E', 'e' -> inputConnection.sendKeyEvent(KeyEvent(0, 33))
            'F', 'f' -> inputConnection.sendKeyEvent(KeyEvent(0, 34))
        }


    }




    override fun onText(p0: CharSequence?) {
    }

    override fun swipeLeft() {
    }

    override fun swipeRight() {
    }

    override fun swipeDown() {
    }

    override fun swipeUp() {
    }




    private fun getToken(): IBinder? {
        var window: Window
        val dialog = getWindow()
        window = dialog.getWindow()!!
        return if (dialog == null || dialog.window.also { window = it!! } == null) {
            null
        } else window.attributes.token
    }

    override fun onCreateInputView(): View {
        getData()
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        keyboard = Keyboard(this, R.xml.keys_layout)
        keyboardView!!.apply {
            keyboard = this@MyInputMethodService.keyboard
            setOnKeyboardActionListener(this@MyInputMethodService)
        }
        //setLatinKeyboard(mQwertyKeyboard)
        handlers = Handler()
        handlers.postDelayed(object : Runnable {
            var count=0
            @SuppressLint("SuspiciousIndentation")
            override fun run() {
                try {
                    count++
                    val mes = DATA
                    Log.e("Keyboard ", "MESSAGE ${mes}")

                    if (mes != null) {
                        val inputConnection = currentInputConnection
                        for (i in 0 until mes.length) {
                            val ch = mes[i]
                            when (ch) {
                                '0' -> inputConnection.sendKeyEvent(KeyEvent(0, 7))
                                '1' -> inputConnection.sendKeyEvent(KeyEvent(0, 8))
                                '2' -> inputConnection.sendKeyEvent(KeyEvent(0, 9))
                                '3' -> inputConnection.sendKeyEvent(KeyEvent(0, 10))
                                '4' -> inputConnection.sendKeyEvent(KeyEvent(0, 11))
                                '5' -> inputConnection.sendKeyEvent(KeyEvent(0, 12))
                                '6' -> inputConnection.sendKeyEvent(KeyEvent(0, 13))
                                '7' -> inputConnection.sendKeyEvent(KeyEvent(0, 14))
                                '8' -> inputConnection.sendKeyEvent(KeyEvent(0, 15))
                                '9' -> inputConnection.sendKeyEvent(KeyEvent(0, 16))
                                'A', 'a' -> inputConnection.sendKeyEvent(KeyEvent(0, 29))
                                'B', 'b' -> inputConnection.sendKeyEvent(KeyEvent(0, 30))
                                'C', 'c' -> inputConnection.sendKeyEvent(KeyEvent(0, 31))
                                'D', 'd' -> inputConnection.sendKeyEvent(KeyEvent(0, 32))
                                'E', 'e' -> {
                                    Log.e("Testing", "CHARCATER $ch")
                                    inputConnection.sendKeyEvent(KeyEvent(0, 33))
                                }

                                'F', 'f' -> inputConnection.sendKeyEvent(KeyEvent(0, 34))
                            }
                        }
                        if(mes!="")
                        inputConnection.sendKeyEvent(KeyEvent(0, 66))

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                DATA = ""
                handlers.postDelayed(this, 1000)
            }
        }, 1000)

        return keyboardView as KeyboardView
    }

    private lateinit var mInputView: LatinKeyboardView
    private lateinit var mQwertyKeyboard: LatinKeyboard
    private lateinit var handlers: Handler


    fun getData() {
        var count = 0
        Log.d("MSD", "focus")
       // val inputConnection: InputConnection = this.currentInputConnection
        App.myObject = object : App.NordicStatusListener {
            override fun readerStatus(status: String, statusColor: Int, isConnected: Boolean) {
            }

            @Synchronized
            override fun onTagRead(epc: String, totalInventory: Int) {
                Log.d("MSD_ONTAG", epc.toString())
                DATA = epc



            }

        }
    }



}