import java.io.IOException;
import java.net.*;

public class ClientUdp extends Thread{
    private DatagramSocket socket;
    private InetAddress address;

    public ClientUdp(String address) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        this.address = InetAddress.getByName(address);
    }

    public void sendEcho(String msg) throws IOException {
        //enviar
        byte[] buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9031);
        socket.send(packet);
    }

    public void run(){
        while (Cliente.running) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
            } catch (IOException ignored) {
            }
        }
    }

    public void close() {
        socket.close();
    }
}