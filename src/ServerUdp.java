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
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());
                //dividir string recebida
                String[] parts = received.split(";");
                if (parts.length == 2) {
                    String toIP = parts[0];
                    String msg = parts[1];
                    toIP = Servidor.onlineClients.get(Integer.parseInt(toIP)).getIP();
                    //enviar
                    if(send(msg, packet.getAddress().getHostAddress(), toIP)){
                        Servidor.logprint("Mensagem enviada de [ " + packet.getAddress().getHostAddress() + " ] para [ " + toIP + " ] : " + msg);
                    } else {
                        Servidor.logprint("ERRO: Enviar mensagem de [ " + packet.getAddress().getHostAddress() + " ] para [ " + toIP + " ] : " + msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}