import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;

public class Servidor {
    private static final String logFile = ("log/"+"log_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt");
    private static ServerSocket server = null;
    public static ServerUdp serverUdp = null;
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
                verifyIfAlreadyConnected();
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

        private boolean verifyIfAlreadyConnected(){
            onlineClients.removeIf(client -> this.IP.equals(client.getIP()));
            return false;
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
        logprint("Servidor TCP iniciado no porto 6500");
        Thread threadUdp = new Thread(serverUdp = new ServerUdp());
        threadUdp.start();
        logprint("Servidor UDP iniciado no porto 6500");
        while(true) {
            ClientConnected client = new ClientConnected();
        }
    }
}
