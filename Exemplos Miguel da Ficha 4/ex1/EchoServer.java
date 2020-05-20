import java.net.*;
import java.io.*;
// Aguarda comunicação no porto 6500,
// recebe mensagens e devolve-as
public class EchoServer {
    public static void main(String args[]) throws Exception {
//criar socket na porta 6500
        ServerSocket server = new ServerSocket(6500);
        System.out.println ("servidor iniciado no porto 6500");
        Socket socket = null;
//aguarda mensagens
        while(true) {
            socket = server.accept();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintStream ps = new PrintStream(socket.getOutputStream());
            String linha = br.readLine();
			System.out.println("Mensagem recebida: " + linha); 
            ps.println(linha); // Echo input para output
//termina socket
            socket.close();
        }
    }
}
//