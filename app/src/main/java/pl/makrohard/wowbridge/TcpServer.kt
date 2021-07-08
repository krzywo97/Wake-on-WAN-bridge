package pl.makrohard.wowbridge

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*

class TcpServer(private val broadcastAddress: String, port: Int) : Thread() {
    private var serverSocket: ServerSocket = ServerSocket(port)

    override fun run() {
        super.run()

        while (!isInterrupted) {
            val exchangeSocket = serverSocket.accept()
            val inputStream = exchangeSocket.getInputStream()
            val input = ByteArray(12)
            inputStream.read(input, 0, 12)
            if (!isMacAddressValid(input)) continue

            val macAddress = String(input).lowercase(Locale.getDefault())

            val broadcastAddress = InetAddress.getByName(broadcastAddress)
            val payload = ByteArray(102)
            for (i in 0..5) {
                payload[i] = 0xff.toByte()
            }
            for (i in 0..15) {
                for (j in 0..5) {
                    val macPart = macAddress.substring(2 * j, 2 * j + 2)
                    payload[6 + j + i * 6] = macPart.toInt(16).toByte()
                }
            }
            val packet = DatagramPacket(payload, payload.size, broadcastAddress, 9)
            val wolSocket = DatagramSocket()
            wolSocket.send(packet)

            wolSocket.close()
            inputStream.close()
            exchangeSocket.close()
        }

        serverSocket.close()
    }

    private fun isMacAddressValid(macAddress: ByteArray): Boolean {
        for (b in macAddress) {
            if (!((b >= '0'.code.toByte() && b <= '9'.code.toByte())
                        || (b >= 'A'.code.toByte() && b <= 'F'.code.toByte())
                        || (b >= 'a'.code.toByte() && b <= 'f'.code.toByte()))
            ) {
                return false
            }
        }
        return true
    }
}