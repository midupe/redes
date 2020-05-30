import com.diogonunes.jcdp.color.*;
import com.diogonunes.jcdp.color.api.Ansi;

import java.io.IOException;
import java.net.*;

public class ClientUdp extends Thread{
    public DatagramSocket socketUdp;
    private InetAddress address;

    public ClientUdp(String address) throws IOException {
        socketUdp = new DatagramSocket();
        this.address = InetAddress.getByName(address);
        sendEcho("Connection");
    }

    public void sendEcho(String msg) throws IOException {
        byte[] buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9031);
        socketUdp.send(packet);
    }

    public void reciveEcho() throws IOException {
        while (Cliente.running) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socketUdp.receive(packet);
                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                String received = new String (data);
                System.out.println();
                ColoredPrinter cp = new ColoredPrinter.Builder(0, false)
                        .foreground(Ansi.FColor.WHITE).background(Ansi.BColor.BLUE)   //setting format
                        .build();
                cp.print(received, Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.BLACK);
                System.out.println();
            } catch (SocketException ignored){
            }

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