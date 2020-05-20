import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client { 
    static Socket socket = null;
    static String IP = "";
    static BufferedReader br = null;
    static PrintStream ps = null;


    public static void menu() {
        System.out.println();
        System.out.println("MENU CLIENTE");
        System.out.println();
        System.out.println("0 - Menu Inicial");
        System.out.println("1 - Listar utilizadores online");
        System.out.println("2 - Enviar mensagem a um utilizador");
        System.out.println("3 - Enviar mensagem a todos os utilizadores");
        System.out.println("4 - Lista branca de utilizadores");
        System.out.println("5 - Lista negra de utilizadores");
        System.out.println("99 – Sair");
        System.out.println();
        System.out.println("Opcao?");
    }
    
    public static void run() throws Exception{
        String fromServer;
        Scanner scan = new Scanner(System.in);
        loop: while (true) {
            switch (scan.nextInt()) {
                case 0:
                    menu();
                    break;
                case 1:
                    ps.println("1");
                    System.out.println("Utilizadores Online:");
                    break;
                case 4:
                    ps.println(4);
                    System.out.println("Lista branca:");
                    break;
                case 5:
                    ps.println(5);
                    System.out.println("Lista negra:");
                    break;
                case 99:
                    System.out.println("A Sair");
                    ps.println("A sair");
                    break loop;
                default:
                    System.out.println("Opcao invalida");
            }
            while ((fromServer = br.readLine()) != null){
                System.out.println(fromServer);
            }
        }
    }

    public static void checkIP () throws Exception{
        ps.println("conexao");
        String checkIP = br.readLine();
        if (checkIP == null) {
            System.out.println("Ocorreu algum erro ao tentar conectar ao servidor");
            System.exit(1);
        }
        if (checkIP.equals("FALSE")) {
            System.out.println("Acesso bloqueado");
            System.exit(1);
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
        socket = new Socket(IP, 6500);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ps = new PrintStream(socket.getOutputStream());
        checkIP();
        menu();
        run();
        System.out.println(br.readLine());
        socket.close();
    }
}
