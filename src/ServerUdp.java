import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerUdp extends Thread {
    private DatagramSocket socket;
    private boolean running;

    public ServerUdp() throws SocketException {
        socket = new DatagramSocket(9031);
    }

    public boolean send(String msg, String fromIP ,String toIP){
        try {
            String text = "Mensagem recebida [ " + fromIP + " ] : " + msg;
            InetAddress address = InetAddress.getByName(toIP);
            byte[] buf = text.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9031);
            socket.send(packet);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void run() {
        running = true;

        while (running) {
            try {
                //receber
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                packet = new DatagramPacket(buf, buf.length, address, 9031);
                String received = new String(packet.getData(), 0, packet.getLength());
                //dividir string recebida
                String[] parts = received.split(";");
                if (parts.length == 2) {
                    String toIP = parts[0];
                    String msg = parts[1];
                    toIP = Servidor.onlineClients.get(Integer.parseInt(toIP)).getIP();
                    Servidor.logprint("Mensagem de [ " + packet.getAddress().getHostAddress() + " ] para [ " + toIP + " ] : " + msg);
                    //enviar
                    send(msg, packet.getAddress().getHostAddress(), toIP);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}