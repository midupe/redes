import java.io.IOException;
import java.net.*;

public class ClientUdp extends Thread{
    private DatagramSocket socketudp;
    private InetAddress address;

    private byte[] buf;

    public ClientUdp() throws SocketException, UnknownHostException {
        this.socketudp = new DatagramSocket();
        this.address = InetAddress.getByName(Cliente.IP);
    }

    public void sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9031);
        this.socketudp.send(packet);
    }

    public void reciveMsg() throws IOException{
        while (Cliente.running){
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socketudp.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if (!received.isEmpty() || !received.equals("")) {
                    System.out.println();
                    System.out.println("Mensagem recebida: " + received);
                    System.out.println();
                }
            } catch (IOException | NullPointerException e) {
            }
        }
    }

    public void close() {
        this.socketudp.close();
    }

    public void run(){
        try {
            reciveMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}