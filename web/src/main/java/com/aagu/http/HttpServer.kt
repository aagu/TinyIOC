package com.aagu.http

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.DestroyMethod
import com.aagu.ioc.annotation.Value
import com.aagu.ioc.bean.BeanFactoryAware
import com.aagu.ioc.factory.AbstractBeanFactory
import com.aagu.mvc.DispatcherServlet
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import kotlin.system.exitProcess

@Bean
class HttpServer : BeanFactoryAware {
    @Volatile private var shutDown = false

    @Value("#{serverPort}") private var serverPort:Int = 4567

    private lateinit var factory: AbstractBeanFactory
    private lateinit var serverSocket: ServerSocket

    fun run() {
        try {
            serverSocket = ServerSocket(serverPort, 1, InetAddress.getByName("127.0.0.1"))
            println("http server running at port: $serverPort")
        } catch (ex: IOException) {
            ex.printStackTrace()
            exitProcess(1)
        }

        val dispatcherServlet = factory.getBean(DispatcherServlet::class.java)

        while (!shutDown) {
            try {
                val socket = serverSocket.accept()
                val ins = socket.getInputStream()
                val out = socket.getOutputStream()

                val request = Request(ins, serverPort, socket.localPort)
                val response = Response(out)
                request.parse()

                dispatcherServlet.doGet(request, response)

                response.flushBuffer()

                socket.close()
            } catch (ignore: SocketException) {
                // ignore it
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun setBeanFactory(factory: AbstractBeanFactory) {
        this.factory = factory
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