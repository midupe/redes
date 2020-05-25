import java.net.*;
import java.io.*;
import java.util.Scanner;


public class Cliente {
    private static String IP = "";
    private static BufferedReader br = null;
    private static PrintStream ps = null;

    private static void startUdpClient (){
        try {
            ClientUdp client = new ClientUdp(IP);
            System.out.println("Mensagem recebida: " + client.sendEcho("Ola"));
            client.sendEcho("end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runTcpClient() throws Exception{
        String fromServer = "";
        loop: while (true) {
            while ((fromServer = br.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.equals("A sair")){
                    break loop;
                }
            }
            Scanner scan = new Scanner(System.in);
            String fromClient = scan.nextLine();
            ps.println(fromClient);
        }
    }


    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.out.println("Indique o IP do servidor");
            Scanner scan = new Scanner(System.in);
            IP = scan.nextLine();
        } else {
            IP = args[0];
        }
        Socket socket = new Socket(IP, 6500);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ps = new PrintStream(socket.getOutputStream());
        ps.println("Pedido de conexão");
        if(br.readLine().equals("false")){
            System.out.println("Acesso negado");
        } else {
            System.out.println("Conectado");
            runTcpClient();
            //startUdpClient();
        }
        System.out.println("Cliente Desconectado..");
        socket.close();
    }
}
