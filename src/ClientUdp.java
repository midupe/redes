import java.net.*;
import java.io.*;

public class ClientUdp {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public ClientUdp(String address) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        this.address = InetAddress.getByName(address);
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }

}
