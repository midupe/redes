import java.net.*;
import java.time.LocalDateTime;
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
            socket = server.accept();
            final BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            final PrintStream ps = new PrintStream(socket.getOutputStream());
            final String linha = br.readLine();
            System.out.println("Comando recebido: " + linha); 
            switch(linha){
                case "frase":
                    int rnd = new Random().nextInt(ola.length);
                    ps.println(ola[rnd]); // Echo input para output
                    break;
                case "listar":
                    ps.println("Bom dia, Bem disposto?, Ola, Boas, Viva");
                    break;
                case "tchau":
                    ps.println("Ligação terminada");
                    break;
                case "horas":
                    ps.println(LocalDateTime.now());
                    break;
                default:
                    ps.println("Comando não disponivel. Use um dos segunintes: horas, listar, frase ou tchau");
            }
//termina socket
            socket.close();
        }
    }
}
//