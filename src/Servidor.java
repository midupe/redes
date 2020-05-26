import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;

public class Servidor {
    private static final String logFile = ("log/"+"log_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt");
    private static ServerSocket server = null;
    public static LinkedList<ClientConnected> onlineClients = new LinkedList<ClientConnected>();

    public static boolean checkOnFile (String listFile, String IP) {
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

    public static void logprint(String str) {
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

    public static class ServerTcp implements Runnable{
        private BufferedReader br;
        private PrintStream ps;
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
            ps.println("Opcao?");
        }


        public void start() throws Exception{
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
                            for (int i=0; i<onlineClients.size(); i++){
                                String IP = onlineClients.get(i).getIP();
                                String text = (i + " - " + IP);
                                ps.println(text);
                            }
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


        public void run() {
            try {
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ClientConnected{
        Socket socket;
        BufferedReader br;
        PrintStream ps;
        String IP;

        public String getIP() {
            return IP;
        }

        public ClientConnected() throws IOException {
            this.socket = server.accept();
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));;
            this.ps = new PrintStream(socket.getOutputStream());
            String linha = br.readLine();
            if(linha.equals("Pedido de conexão")){
                this.IP = socket.getInetAddress().getHostAddress();
                boolean checkIP = checkIP(IP);
                ps.println(checkIP);
                if (checkIP) {
                    Thread ServerTcp = new Thread(new ServerTcp(br, ps, IP));
                    ServerTcp.start();
                    onlineClients.add(this);
                } else {
                    socket.close();
                }
            }
        }

        public void endConnection() throws IOException {
            socket.close();
            onlineClients.remove(this);
            logprint("Cliente " + IP + " desconectado");
        }
    }


    public static void main(String[] args) throws Exception {
        createLog();
        server = new ServerSocket(6500);
        logprint("Servidor iniciado no porto 6500");
        Thread serverUdp = new Thread(new ServerUdp());
        serverUdp.start();
        logprint("Servidor iniciado no porto 9031");
        while(true) {
            ClientConnected client = new ClientConnected();
        }
    }
}
