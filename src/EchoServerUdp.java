import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class EchoServerUdp extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private static HashMap<String, Integer> users = new HashMap<>();
 
    public EchoServerUdp() throws SocketException {
        socket = new DatagramSocket(4445);
    }

    public void send(String msg, String IP) throws IOException {
        System.out.println("FUncao send");
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        InetAddress address = InetAddress.getByName(IP);
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }
 
    public void run() {
        running = true;

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                users.put(packet.getAddress().getHostAddress(),port);
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Mensagem recebida [ " + packet.getAddress().getHostAddress() + " ] : " + received);
                //socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        EchoServerUdp serverUdp = new EchoServerUdp();
        serverUdp.send("OLA DAQUI FALA O SERVER", "127.0.0.1");
        serverUdp.start();
    }
}