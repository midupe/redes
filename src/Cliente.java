import java.net.*;
import java.io.*;
import java.util.Scanner;


public class Cliente {
    private static String IP = "";
    private static BufferedReader br = null;
    private static PrintStream ps = null;
    private static boolean validation = false;
    private static Thread ClientUdp = null;
    private static ClientUdp udpClient = null;

    private static void runTcpClient() throws Exception{
        System.out.println("TCP INICIADO---------");
        ps.println("0");
        String fromServer;
        loop: while (true) {
            while(!(fromServer = br.readLine()).equals("null")) {
                System.out.println(fromServer);
                if (fromServer.equals("A sair")) {
                    break loop;
                }
            }
            Scanner scanner = new Scanner(System.in);
            String fromClient = scanner.nextLine();
            ps.println(fromClient);
        }
    }

    public static class ClientUdp implements Runnable {
        private DatagramSocket socket;
        private InetAddress address;

        private byte[] buf;

        public ClientUdp(String address) throws SocketException, UnknownHostException {
            this.socket = new DatagramSocket();
            this.address = InetAddress.getByName(address);
        }

        public String sendEcho(String msg) throws IOException {
            buf = msg.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
        }

        public void close() {
            socket.close();
        }

        public void run() {
            System.out.println("UDP INICIADO---------");
            try {
                System.out.println("Mensagem recebida: " + udpClient.sendEcho("Ola"));
                udpClient.sendEcho("end");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        do {
            validation = false;
            if (args.length == 0) {
                System.out.println("Indique o IP do servidor");
                Scanner scan = new Scanner(System.in);
                IP = scan.nextLine();
            } else {
                IP = args[0];
            }
            try {
                Socket socket = new Socket(IP, 6500);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ps = new PrintStream(socket.getOutputStream());
                ps.println("Pedido de conexão");
                if (br.readLine().equals("false")) {
                    System.out.println("Acesso negado");
                } else {
                    System.out.println("Conectado");
                    //Thread ClientUdp = new Thread(udpClient = new ClientUdp(IP));
                    //lientUdp.start();
                    runTcpClient();
                }
                socket.close();
                System.out.println("Cliente Desconectado..");
            } catch (IOException exception) {
                validation = true;
                System.out.println("ERRO: IP inválido");
            }
        }while (validation);
    }
}
