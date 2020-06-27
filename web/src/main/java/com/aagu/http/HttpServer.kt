package com.aagu.http

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.DestroyMethod
import com.aagu.ioc.annotation.Value
import com.aagu.ioc.annotation.Wire
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import java.net.SocketException
import javax.servlet.http.HttpServlet
import kotlin.system.exitProcess

@Bean
class HttpServer {
    @Volatile private var shutDown = false

    @Value("#{serverPort}") private var serverPort:Int = 4567

    @Wire private lateinit var dispatcherServlet:HttpServlet

    private lateinit var serverSocket: ServerSocket

    fun run() {
        try {
            serverSocket = ServerSocket(serverPort, 128, null)
            println("http server running at port: $serverPort")
        } catch (ex: IOException) {
            ex.printStackTrace()
            exitProcess(1)
        }

        while (!shutDown) {
            try {
                val socket = serverSocket.accept()
                GlobalScope.launch(Dispatchers.IO) {
                    val ins = socket.getInputStream()
                    val out = socket.getOutputStream()

                    val request = Request(ins, serverPort, socket.localPort)
                    val response = Response(out)
                    request.parse()

                    dispatcherServlet.service(request, response)

                    response.flushBuffer()

                    socket.close()
                }

            } catch (ignore: SocketException) {
                // ignore it
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    @DestroyMethod
    fun close() {
        println("http server closing...")
        shutDown = true
        if (::serverSocket.isInitialized) {
            serverSocket.close()
        }
        println("http sever closed")
    }
}