/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.scene.control.Label;

/**
 *
 * @author AZUS
 */
public class ListnerConectarCliente implements Runnable {
    private Socket cliente;
    private ServerSocket servidor;
    
    private ObjectOutputStream output;
    private ObjectInputStream input;
    
    private Label status;
    
    ListnerConectarCliente (ServerSocket servidor, Label status)
    {   
        this.servidor = servidor;
        this.status = status;
        
        try
        {
            this.cliente = servidor.accept();
            this.output = new ObjectOutputStream(cliente.getOutputStream());
            this.input = new ObjectInputStream(cliente.getInputStream());
            
        }catch(Exception e){
            
        }
    }
    
    public void run() 
    {   
        try
        {
            do
            {
                try
                {   
                    String v[] = ((String)(input.readObject())).split("#");
                    
                    if(v[0].equals("@ACCEPT"))
                    {
                        // atualiza tabela
                    }
                    else if(v[0].equals("@MSG"))
                    {
                        // mostra mensagem   
                    }
                    else if(v[0].equals("@LOGOUT"))
                    {
                        // retira o cliente da tabela
                    }
                    
                } catch (Exception e) { 
                    System.out.println(e); 
                }

                String listaOnline = (String) input.readObject();
                if (!listaOnline.isEmpty()) 
                {
                    output.writeObject("");
                    output.flush(); 
                }
                
                try {
                    Thread.sleep(1000);
                    
                }catch(Exception e){}
                
            }while (status.getText().equals("Conectado"));
            
            // fecha as conexões
            output.close(); 
            input.close(); 
            cliente.close();
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
}
