/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import cliente.ClienteTela;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author AZUS
 */
public class ListnerNovoCliente implements Runnable {
    
    private TelaServidorController tela;
    private ServerSocket servidor;
    private Socket cliente;
    
    private ArrayList<ClienteTela> clientes;
    
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ListnerNovoCliente(TelaServidorController tela, ServerSocket servidor) {
        this.tela = tela;
        this.servidor = servidor;
    }
    
    public void run() {
        
        String nome, ip, porta;
        StringBuilder flag = new StringBuilder("true");
        
        while(tela.isAtivo())
        {
            try{
                cliente = servidor.accept();
                
                output = new ObjectOutputStream(cliente.getOutputStream());
                input = new ObjectInputStream(cliente.getInputStream());
                
                // 
                String protocolo = input.readObject().toString();
                String v[] = protocolo.split("#");
                ip = v[1];
                nome = v[2];
                porta = v[3];
                
                // protocolo clientes conectados
                String protocoloAccept = "<#@NEWUSER";
                for (ClienteTela c : clientes) {
                    protocoloAccept += "#IPCLIENT=" + c.getIP()
                                    + "#NOME=" + c.getNome()
                                    + "#PORTA=" + c.getPorta();
                }
                protocoloAccept +=">";
                
                for (ClienteTela c : clientes) {
                    output = new ObjectOutputStream(c.getSocket().getOutputStream());
                    output.writeObject(protocoloAccept);
                }
                
                // protocolo novo usuário
                protocolo = protocolo.replace("CONNECT", "ACCEPT");
                output = new ObjectOutputStream(cliente.getOutputStream());
                output.writeObject(protocolo);
                
                // novo cliente
                ClienteTela c = new ClienteTela(ip, nome, Integer.parseInt(porta), new Socket("127.0.0.1", 8081));
                
                // adicona novo cliente na lista 
                Platform.runLater(()->{tela.getTabelaConectados().getItems().add(c);});
                clientes.add(c);
                
                // lança uma trhread para tratar exclusivamente esse cliente
                ListnerLogoutCliente th = new ListnerLogoutCliente(tela, c, clientes, output, input, flag);
                Thread newThrd = new Thread(th);
                newThrd.start(); // inicia a thread
                
            }catch(IOException e){
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText(e.getMessage());
                a.showAndWait();
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ListnerNovoCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
