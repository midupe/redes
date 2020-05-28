import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerUdp extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private InetAddress address;

    public ServerUdp() throws SocketException {
        socket = new DatagramSocket(9031);
    }

    public void sendEcho(String msg, String IP) throws IOException {
        try {
            InetAddress adress = InetAddress.getByName(IP);
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9031);
            socket.send(packet);
        }catch (NullPointerException | IOException e){
        }
    }

    public void run() {
        Servidor.logprint("Servidor iniciado no porto 9031");
        running = true;

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());

                System.out.println("Mensagem recebida [ " + packet.getAddress().getHostAddress() + " ] : " + received);
                if (received.equals("end")) {
                    running = false;
                    continue;
                }
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}