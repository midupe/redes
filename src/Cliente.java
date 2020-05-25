import java.net.*;
import java.io.*;
import java.util.Scanner;


public class Cliente {
    static String IP = "";

    public static void startUdpClient (){
        try {
            ClientUdp client = new ClientUdp(IP);
            System.out.println("Mensagem recebida: " + client.sendEcho("Ola"));
            client.sendEcho("end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]) throws Exception {
        if(args.length == 0) {
            System.out.println("Indique o IP do servidor");
            Scanner scan = new Scanner(System.in);
            IP = scan.nextLine();
        } else {
            IP = args[0];
        }
        Socket socket = new Socket(IP, 6500);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.println("Pedido de conexão");
        if(br.readLine().equals("false")){
            System.out.println("Acesso negado");
        } else {
            System.out.println("Conectado");
            startUdpClient();
        }
        socket.close();
    }
}
