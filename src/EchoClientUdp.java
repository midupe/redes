import java.io.IOException;
import java.net.*;

public class EchoClientUdp {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public EchoClientUdp(String address) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        this.address = InetAddress.getByName(address);
    }

    public void receiveEcho() throws IOException {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 4445);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("RECEBIDO: " + received);
    }

    public void sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
    }

    public void close() {
        socket.close();
    }


    public static void main(String[] args) {
        try {
            EchoClientUdp client = new EchoClientUdp("127.0.0.1");
            client.sendEcho("OLAAAAA");
            client.receiveEcho();
            client.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}