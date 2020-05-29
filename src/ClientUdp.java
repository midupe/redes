import java.io.IOException;
import java.net.*;

public class ClientUdp extends Thread{
    public DatagramSocket socketUdp;
    private InetAddress address;

    public ClientUdp(String address) throws SocketException, UnknownHostException {
        socketUdp = new DatagramSocket();
        this.address = InetAddress.getByName(address);
    }

    public void sendEcho(String msg) throws IOException {
        byte[] buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9031);
        socketUdp.send(packet);
    }

    public void reciveEcho() throws IOException {
        while(Cliente.running) {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socketUdp.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("-> " + received);
        }
    }

    public void run() {
        try {
            reciveEcho();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}