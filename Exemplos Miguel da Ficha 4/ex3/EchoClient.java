import java.net.*;
import java.io.*;
// Connectar ao porto 6500 de um servidor especifico,
// envia um comando e imprime resultado,

public class EchoClient {
    // usage: java EchoClient <servidor> <comando>
    public static void main(String args[]) throws Exception {
        Socket socket = new Socket(args[0], 6500);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.println(args[1]); // escreve mensagem na socket
// imprime resposta do servidor
        System.out.println("Recebido : " + br.readLine());
// termina socket
        socket.close();
    }
}
