import java.net.*;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {   
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

    public static void run() throws Exception{
        ServerSocket server = new ServerSocket(6500);
        logprint("Servidor iniciado na porta 6500");
        Socket client = null;
        while(true) {
            client = server.accept();
            if (client.getInetAddress() != null && client.getInetAddress().getHostAddress() != null) {
                final String IP = client.getInetAddress().getHostAddress();
                final PrintStream ps = new PrintStream(client.getOutputStream());
                final BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                final boolean check = checkIP(IP);
                final String checkRequeste = br.readLine();
                if (checkRequeste!= null && checkRequeste.equals("conexao")) {
                    if (check){
                        ps.println("TRUE");
                    } else {
                        ps.println("FALSE");
                    }
                }
                if (check){
                    String linha = br.readLine();
                    if (linha == null) {
                        logprint("O cliente " + IP + " fechou inesperadamente ou ocorreu algum problema com a conexao ao server");
                    } else {
                        logprint("Pedido recebido de " + IP + ": " + linha);
                        switch(linha){
                            case "1":
                                ps.println("TA A DAR"); 
                                break;
                            case "A sair":
                                ps.println("Cliente desconectado");
                                break;
                            case "Menu":
                                ps.println("Menu");
                                break;
                        }
                    }
                } 
            }   
            client.close();
        }
    }

    public static void main(String args[]) throws Exception {
        createLog();
        run();
    }
}
