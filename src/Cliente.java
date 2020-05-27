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
    private static Socket socket = null;

    private static void runTcpClient() throws Exception{
        System.out.println("TCP INICIADO---------");
        ps.println("0");
        String fromServer;
        String fromClient = "";
        boolean running = true;
        loop: while (running) {
            try{
                while(!(fromServer = br.readLine()).equals("null") && !fromServer.equals("true")) {
                    System.out.println(fromServer);
                    if (fromServer.equals("A sair")) {
                        running = false;
                        break loop;
                    }
                }
                if (fromServer.equals("true")){
                    System.out.println("Conexão retomada com o servidor");
                    if (!fromClient.equals("")) {
                        ps.println(fromClient);
                    }
                } else {
                    Scanner scanner = new Scanner(System.in);
                    fromClient = scanner.nextLine();
                    ps.println(fromClient);
                }
            } catch (NullPointerException | IOException exception){
                int count = 0;
                System.out.print("A tentar conectar ao servidor.");
                while (count!=15) {
                    Thread.sleep(1000);
                    count++;
                    if (count%3==0){
                        System.out.print(".");
                    }
                    if(connect()){
                        System.out.print("\n");
                        break;
                    }
                }
                if (count==15) {
                    System.out.print("\n");
                    System.out.println("Ligação com o servidor perdida");
                    break;
                }
            }

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

    private static boolean connect() {
        try {
            socket = new Socket(IP, 6500);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ps = new PrintStream(socket.getOutputStream());
            ps.println("Pedido de conexão");
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        validation = false;
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
                if(!connect()){
                    throw new IOException();
                }
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
