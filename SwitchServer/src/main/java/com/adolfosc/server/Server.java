package com.adolfosc.server;

import com.adolfosc.trama.Option;
import com.adolfosc.trama.Trama;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author hectoradolfo
 */
public class Server {

    public static String HOST = "localhost";
    public static int PORT = 5000;
    public static int PORT_SEND = 5001;
    private List<Client> clients;
    private NewClient newClient;

    public Server() {
        this.clients = new ArrayList<>();
        this.newClient = new NewClient();
    }

   // @SuppressWarnings("empty-statement")
    public void startServer(JTextArea txtaLog, int typeSwitch) {
        try {
            ServerSocket served = new ServerSocket(PORT);

            String mac, ip, message;
            Option type;
            Trama tramaRecibido;

            while (true) {
                try ( Socket socket = served.accept()) {
                    ObjectInputStream dataIn = new ObjectInputStream(socket.getInputStream());
                    Object as = dataIn.readObject();
                    
                    tramaRecibido = (Trama) as;

                    mac = tramaRecibido.getMac();
                    ip = tramaRecibido.getIp();
                    message = tramaRecibido.getMessage();
                    type = tramaRecibido.getType();

                    switch (typeSwitch) {
                        case 0://unicast
                            //verificamos el tipo de peticion del cliente
                            switch (type) {
                                case CONNECTION:
                                    txtaLog.append("\nreribido de mac: " + mac + "  --> ip: " + ip + "  --> mensaje:  " + message);
                                    if (!this.clients.isEmpty()) {
                                        Client adds = this.newClient.addClient(clients, mac);
                                        if (adds == null) {
                                            this.clients.add(new Client(ip, mac, true));
                                        } else {
                                            System.out.println("este mac ya existe en el sistema, intente con otro!!");
                                        }
                                    } else {
                                        this.clients.add(new Client(ip, mac, true));
                                    }
                                    /*try ( //socket para enviar al destinatario
                                             Socket sendData = new Socket(ip, PORT_SEND)) {
                                        ObjectOutputStream tramaReenvio = new ObjectOutputStream(sendData.getOutputStream());
                                        tramaReenvio.writeObject(this.clients);
                                    }*/
                                    break;

                                case MESSAGE:
                                    Client adds = this.newClient.addClient(clients, mac);
                                    txtaLog.append("\nreribido de: " + mac + "  --> para: " + ip + "  --> mensaje:  " + message);
                                    if (adds != null) {
                                        try ( //socket para enviar al destinatario
                                                 Socket sendData = new Socket(adds.getIp(), PORT_SEND)) {
                                            ObjectOutputStream tramaReenvio = new ObjectOutputStream(sendData.getOutputStream());
                                            tramaReenvio.writeObject(tramaRecibido);
                                        }
                                    }
                                    break;

                                case CLOSE:
                                    Client clientsClose = this.newClient.addClient(clients, mac);
                                    if (clientsClose != null) {
                                        clientsClose.setStatus(false);
                                    } else {
                                        System.out.println("no existe cliente con este mac para desactivar!!");
                                    }
                                    break;
                                default:
                                    System.out.println("peticion no encontrada : " + type);
                                    break;
                            }
                            break;

                        case 1://broadcast                            
                            txtaLog.append("\n" + mac + "  -->  " + ip + "  -->  " + message);
                            for (Client client : clients) {
                                try ( //socket para enviar al destinatario
                                         Socket sendData = new Socket(client.getIp(), PORT_SEND)) {
                                    ObjectOutputStream tramaReenvio = new ObjectOutputStream(sendData.getOutputStream());
                                    tramaReenvio.writeObject(tramaRecibido);
                                }
                            }
                            break;
                    }

                }
            }

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
