import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class ServerUdp extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private static HashMap<String, Integer> users = new HashMap<>();

    public ServerUdp() throws SocketException {
        socket = new DatagramSocket(9031);
    }

    public boolean send(String msg, String fromIP ,String toIP){
        try {
            String text = ("[ " + fromIP + " ] : " + msg);
            byte[] buf = text.getBytes();
            InetAddress address = InetAddress.getByName(toIP);
            int port = users.get(toIP);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            return true;
        } catch (IOException | NullPointerException e) {
            return false;
        }
    }

    public void sendoToAll(String msg, String fromIP){
        for(String key: users.keySet()) {
            if(!key.equals(fromIP)){
                send(msg, fromIP, key);
            }
        }
    }

    public void run() {
        running = true;

        while (running) {
            try {
                //receber
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                String received = new String (data);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                if (received.equals("Connection")){
                    users.put(packet.getAddress().getHostAddress(),port);
                } else {
                    //dividir string recebida
                    String[] parts = received.split(";");
                    if (parts.length == 2) {
                        String toIP = parts[0];
                        String msg = parts[1];
                        if(toIP.equals("All")){
                            sendoToAll(msg, packet.getAddress().getHostAddress());
                            Servidor.logprint("Mensagem enviada de [ " + packet.getAddress().getHostAddress() + " ] para todos : " + msg);
                        } else {
                            toIP = Servidor.onlineClients.get(Integer.parseInt(toIP)).getIP();
                            //enviar
                            if (send(msg, packet.getAddress().getHostAddress(), toIP)) {
                                Servidor.logprint("Mensagem enviada de [ " + packet.getAddress().getHostAddress() + " ] para [ " + toIP + " ] : " + msg);
                            } else {
                                Servidor.logprint("ERRO: Enviar mensagem de [ " + packet.getAddress().getHostAddress() + " ] para [ " + toIP + " ] : " + msg);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}