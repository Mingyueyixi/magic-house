package com.lu.magic.frame.xp.socket

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.Socket

open class BaseSession(protected open val socket: Socket) {
    protected var oStream: OutputStream? = null
    protected var oWriter: OutputStreamWriter? = null
    protected var bWriter: BufferedWriter? = null

    protected var iStream: InputStream? = null
    protected var iReader: InputStreamReader? = null
    protected var bReader: BufferedReader? = null

    protected fun initStreamIf() {
        if (oStream == null) {
            oStream = socket.getOutputStream()
            oWriter = OutputStreamWriter(oStream)
            bWriter = BufferedWriter(oWriter)
        }
        if (iStream == null) {
            iStream = socket.getInputStream()
            iReader = InputStreamReader(iStream)
            bReader = BufferedReader(iReader)
        }

    }

}