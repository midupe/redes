import java.net.*;
import java.util.Random;
import java.io.*;
// Aguarda comunicação no porto 6500,
// recebe mensagens e devolve-as
public class EchoServer {
    public static void main(final String args[]) throws Exception {
//criar socket na porta 6500
        final ServerSocket server = new ServerSocket(6500);
        System.out.println ("servidor iniciado no porto 6500");
        Socket socket = null;
        String[] ola =  {"Bom dia", "Bem disposto?", "Ola", "Boas", "Viva"};
//aguarda mensagens
        while(true) {
            int rnd = new Random().nextInt(ola.length);
            socket = server.accept();
            final BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            final PrintStream ps = new PrintStream(socket.getOutputStream());
            final String linha = br.readLine();
			System.out.println("Mensagem recebida: " + linha); 
            ps.println(ola[rnd]); // Echo input para output
//termina socket
            socket.close();
        }
    }
}
//