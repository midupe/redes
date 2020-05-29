import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class ServerTcp implements Runnable{
    private BufferedReader br;
    public PrintStream ps;
    private String IP;

    public ServerTcp(BufferedReader br, PrintStream ps, String IP) {
        this.br = br;
        this.ps = ps;
        this.IP = IP;
    }

    private void sendMenu() throws Exception{
        ps.println();
        ps.println("MENU CLIENTE");
        ps.println();
        ps.println("0 - Menu Inicial");
        ps.println("1 - Listar utilizadores online");
        ps.println("2 - Enviar mensagem a um utilizador");
        ps.println("3 - Enviar mensagem a todos os utilizadores");
        ps.println("4 - Lista branca de utilizadores");
        ps.println("5 - Lista negra de utilizadores");
        ps.println("99 – Sair");
    }

    public void optionTwo() throws Exception{
        String fromClient;
        int userToSendIp = -1;
        String mensagem = null;
        ps.println("Utilizador?");
        ps.println("null");
        loop: while (mensagem == null) {
            if (userToSendIp != -1) {
                ps.println("Mensagem?");
                ps.println("null");
            }
            if ((fromClient = br.readLine()) != null){
                if (userToSendIp == -1) {
                    try {
                        userToSendIp = Integer.parseInt(fromClient);
                        if (userToSendIp<0 || userToSendIp>=Servidor.onlineClients.size()){
                            userToSendIp = -1;
                            ps.println("ERRO: Utilizador inválido");
                            ps.println("null");
                        }
                    } catch (NumberFormatException e) {
                        ps.println("ERRO: Utilizador inválido");
                        ps.println("null");
                    }
                } else {
                    mensagem = fromClient;
                    ps.println("OK, mensagem enviada para " + Servidor.onlineClients.get(userToSendIp).getIP());
                    String send = ("Recebeu uma mensagem de " + IP + ": " + mensagem);
                    Servidor.onlineClients.get(userToSendIp).ps.println();
                    Servidor.onlineClients.get(userToSendIp).ps.println(send);
                    Servidor.onlineClients.get(userToSendIp).ps.println();
                    Servidor.onlineClients.get(userToSendIp).ps.println("null");
                }
            }
        }
        System.out.println("Cliente " + IP + " enviou: [" + mensagem + "] para " + Servidor.onlineClients.get(userToSendIp).getIP());
    }

    public void start() throws Exception{
        String fromClient;
        loop: while (true){
            if ((fromClient = br.readLine()) != null){
                Servidor.logprint("Cliente " + IP + " enviou o comando " + fromClient);
                switch (fromClient) {
                    case "0":
                        sendMenu();
                        break;
                    case "1":
                        ps.println("Utilizadores Online:");
                        for (int i=0; i<Servidor.onlineClients.size(); i++){
                            String IP = Servidor.onlineClients.get(i).getIP();
                            String text = (i + " - " + IP);
                            ps.println(text);
                        }
                        break;
                    case "2":
                        optionTwo();
                        break;
                    case "3":
                        break;
                    case "4":
                        ps.println("Lista branca:");
                        try {
                            File myObj = new File("list/whiteList.txt");
                            Scanner myReader = new Scanner(myObj);
                            while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                ps.println(data);
                            }
                            myReader.close();
                        } catch (FileNotFoundException e) {
                            Servidor.logprint("An error occurred.");
                            e.printStackTrace();
                        }
                        break;
                    case "5":
                        ps.println("Lista negra:");
                        try {
                            File myObj = new File("list/blackList.txt");
                            Scanner myReader = new Scanner(myObj);
                            while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                ps.println(data);
                            }
                            myReader.close();
                        } catch (FileNotFoundException e) {
                            Servidor.logprint("An error occurred.");
                            e.printStackTrace();
                        }
                        break;
                    case "99":
                        ps.println("A sair");
                        for (Servidor.ClientConnected onlineClient : Servidor.onlineClients) {
                            String IP = onlineClient.getIP();
                            if (IP.equals(this.IP)) {
                                onlineClient.endConnection();
                            }
                        }
                        break loop;
                    default:
                        ps.println("Opcao invalida");
                }
                ps.println();
                ps.println("Opcao?");
                ps.println("null");
            }
        }
    }


    public void run() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}