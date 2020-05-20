import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client { 
        static Socket socket = null;
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
    
    public static void run(String IP) throws Exception{ 
        int menu = -1;
        int count = 0;
        loop: while (true){
        socket = new Socket(IP, 6500);
        BufferedReader br = null;
        PrintStream ps = null;
        if (socket != null) {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ps = new PrintStream(socket.getOutputStream());
                ps.println("conexao");
        }
        String checkIP = br.readLine();
        if (checkIP != null && checkIP.equals("FALSE")){
                System.out.println("Acesso bloqueado");
                System.exit(1);
        }
        if (count == 0){
                menu();
        }
        count++;
        if (menu != 99 && socket != null && br != null && ps != null){
                Scanner scan = new Scanner(System.in);
                menu = scan.nextInt(); 
                switch (menu) {
                        case 0:
                                ps.println("Menu");
                                br.readLine();
                                menu();
                                break;
                        case 1:
                                ps.println("1");
                                System.out.println("Utilizadores Online:");
                                System.out.println(br.readLine());
                                break;
                        case 99:
                                System.out.println("A Sair");
                                ps.println("A sair");
                                System.out.println(br.readLine());
                                break loop;
                        default:
                                System.out.println("Opcao invalida");
                }
        }
        socket.close();
        }
     }
    public static void main(String args[]) throws Exception { 
        String IP = "";
        if(args.length == 0) {
                System.out.println("Indique o IP do servidor");
                Scanner scan = new Scanner(System.in); 
                IP = scan.nextLine(); 
        } else {
                IP = args[0];
        }
        run(IP);
    }
}
