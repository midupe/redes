import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Servidor {
    private static String logFile = ("log/"+"log_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt");
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

    public static boolean checkIP (String IP){ //true se o ip nao estiver na blacklist
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

    public static void createLog() {
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

    public static void main(String[] args) throws IOException {
        createLog();
        ServerSocket server = new ServerSocket(6500);
        logprint("Servidor iniciado no porto 6500");
        Socket socket = null;
        //aguarda mensagens
        while(true) {
            socket = server.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream ps = new PrintStream(socket.getOutputStream());
            String linha = br.readLine();
            if(linha.equals("Pedido de conexão")){
                String IP = socket.getInetAddress().getHostAddress();
                ps.println(checkIP(IP));
                ServerUdp serverUdp = new ServerUdp();
                serverUdp.run();
            }
            socket.close();
        }
    }
}
