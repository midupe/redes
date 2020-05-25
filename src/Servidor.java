import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Servidor {
    private static String logFile = ("log/"+"log_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt");
    private static BufferedReader br = null;
    private static PrintStream ps = null;
    private static String IP = "";

    private static boolean checkOnFile (String listFile, String IP) {
        try {
            File myObj = new File(listFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.equals(IP)) {
                    myReader.close();
                    return true;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            logprint("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean checkIP (String IP){ //true se o ip nao estiver na blacklist
        if (checkOnFile("list/blackList.txt", IP)){
            logprint("Conexão bloqueada " + IP  + " que consta na lista negra");
            return false;
        }
        if(checkOnFile("list/whiteList.txt", IP)){
            logprint("Conexão aceite " + IP  + " que consta na lista branca");
            return true;
        } else {
            logprint("Tentativa de conexão de " + IP  + " que não consta em nenhuma lista");
        }
        return false;
    }

    private static void createLog() {
        try {
            File myObj = new File(logFile);
            if (myObj.createNewFile()) {
                logprint("Log file created: " + myObj.getName());
            } else {
                logprint("File already exists.");
            }
        } catch (IOException e) {
            logprint("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void logprint(String str) {
        System.out.println(str);
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(logFile, true));
            out.write(str + '\n');
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }

    private static void sendMenu() throws Exception{
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
        ps.println("Opcao?");
    }

    private static void runTcpServer() throws Exception{
        String fromClient;
        loop: while (true){
            if ((fromClient = br.readLine()) != null){
                logprint("Cliente " + IP + " enviou o comando " + fromClient);
                switch (fromClient) {
                    case "0":
                        sendMenu();
                        break;
                    case "1":
                        ps.println("Utilizadores Online:");
                        break;
                    case "2":
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
                            logprint("An error occurred.");
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
                            logprint("An error occurred.");
                            e.printStackTrace();
                        }
                        break;
                    case "99":
                        ps.println("A sair");
                        break loop;
                    default:
                        ps.println("Opcao invalida");
                }
                ps.println("null");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        createLog();
        ServerSocket server = new ServerSocket(6500);
        logprint("Servidor iniciado no porto 6500");
        Socket socket = null;
        while(true) {
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ps = new PrintStream(socket.getOutputStream());
            String linha = br.readLine();
            if(linha.equals("Pedido de conexão")){
                //conexao
                IP = socket.getInetAddress().getHostAddress();
                ps.println(checkIP(IP));
                //criar ligacao udp para enviar msg para cliente
                //ServerUdp serverUdp = new ServerUdp();
                //serverUdp.run();
                //comandos do cliente
                runTcpServer();
            }
            socket.close();
        }
    }
}
