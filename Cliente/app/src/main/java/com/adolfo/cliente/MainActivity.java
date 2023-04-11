package com.adolfo.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.adolfosc.trama.Option;
import com.adolfosc.trama.Trama;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity implements  Runnable {

   /// static String HOST = "192.168.43.187";

    private static String HOST = "192.168.1.106"; //IP DE LA COMPUTADORA O SERVIDOR
    private static int PORT = 5000;
    private static int PORT_RECIVED = 5001;
    private Socket sc;
   // private DataOutputStream salida;
   // private DataInputStream entrada;
    private String mensajeRecibido;
    private EditText mensaje;
    private EditText respuesta;
    private EditText mac;
    private EditText listC;
    private EditText nameDestino;
    private List<String> listMacs;
    private int macSelect;
    private String macSend, ipSend;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    //String mensajeRecibido;
    private String myIp;
    private ArrayAdapter<String> adapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.listMacs = new ArrayList<>();
        this.mensaje = (EditText)findViewById(R.id.txtMensaje);
        this.mac = (EditText)findViewById(R.id.txtMac);
        this.respuesta = (EditText) findViewById(R.id.tvRespuesta);
        this.listC = (EditText) findViewById(R.id.txtListMac);
        this.nameDestino = (EditText) findViewById(R.id.txtDestino);

        this.respuesta.setMovementMethod(new ScrollingMovementMethod());
        this.listC.setMovementMethod(new ScrollingMovementMethod());

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build());

        mostrarDatos();
       /* adapterList = new ArrayAdapter<>(this, R.layout.list_view_mac, listMacs);
        listC.setAdapter(adapterList);
        /*adapterList = new ArrayAdapter<>(this, R.layout.list_view_mac, listMacs);
        listC.setAdapter(adapterList);*/
        //funcionalidad del listVIew cuando se selecciona un nombre u opcion
        /*this.listC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listMacs.size() > 1 && position > 0) {
                    macSend = listMacs.get(position);
                    listC.setSelection(position);
                    macSelect++;
                    System.out.println(macSelect);
                 //   Toast.makeText(null, "destino seleccionado!!", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        Thread thread = new Thread(this);
        thread.start();
    }

    public void mostrarDatos() {
        // try {

        if (listMacs.size() > 0) {
            listMacs.add(0, "seleccione un destino");
           // adapterList.set(listMacs);

            //listC.add
        } else {
            listMacs.add("no existen clientes");
        }


       //
        /*} catch (Exception e) {
            System.out.println("error en la obtencion de datos de los productos \n" + e);
        }*/
    }

    public void activarConexion(View view) throws UnknownHostException {
        //this.myIp = InetAddress.getLocalHost().getHostAddress();
        System.out.println("este es mi ip ---> "+ this.mac.getText().toString());
        this.mac.setEnabled(false);
        this.mac.setBackgroundColor(RED);
        try{
            Socket socket = new Socket(HOST, PORT);
            Trama trama = new Trama();
            trama.setIp(this.myIp);
            trama.setMac(this.mac.getText().toString());
            trama.setMessage("solicitud de coneccion");
            trama.setType(Option.CONNECTION);

            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.salida.writeObject(trama);
            this.salida.close();

        }catch(IOException e){
            Toast.makeText(this,"error en conexion Conexion" + e, Toast.LENGTH_LONG).show();
            System.out.println("no hay red disponible...." + e);
        }
    }

    public void finalizarConexion(View view) throws UnknownHostException {
        this.mac.setEnabled(true);

        this.mac.setBackgroundColor(WHITE);
        System.out.println("este es mi ip ---> "+ this.mac.getText().toString());
        try{
            Socket socket = new Socket(HOST, PORT);
            Trama trama = new Trama();
            trama.setIp(this.myIp);
            trama.setMac(this.mac.getText().toString());
            trama.setMessage("solicitud de finalizacion de conexion");
            trama.setType(Option.CLOSE);

            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.salida.writeObject(trama);
            this.salida.close();
            this.mac.setText("");

        }catch(IOException e){
            Toast.makeText(this,"error en conexion Conexion" + e, Toast.LENGTH_LONG).show();
            System.out.println("no hay red disponible...." + e);
        }
    }

    public void enviarMensaje(View view) {
        myIp = "";
        String destinno = this.nameDestino.getText().toString();
        System.out.println("este es mi ip ---> "+ myIp);
        if (destinno.isEmpty() || destinno.equals("") || destinno == null) {
            Toast.makeText(this,"debe de ingresar una direccion mac para enviar el mensaje", Toast.LENGTH_LONG).show();
        } else {
            try{
                Socket socket = new Socket(HOST, PORT);
                Trama trama = new Trama();
                trama.setIp(this.myIp);
                trama.setMac(destinno);
                trama.setMessage(this.mensaje.getText().toString());
                trama.setType(Option.MESSAGE);

                this.salida = new ObjectOutputStream(socket.getOutputStream());
                this.salida.writeObject(trama);
                this.salida.close();
                this.mensaje.setText("");

            }catch (Exception e) {
                respuesta.append( " ..no enviado..." );
                Toast.makeText(this,"error en envio de mensaje", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void run() {
        try {
            ServerSocket serverClient = new ServerSocket(PORT_RECIVED);
            Socket client;

            Trama tramaRecived;

            while (true) {
                client = serverClient.accept();

                ObjectInputStream flujoIn = new ObjectInputStream(client.getInputStream());

                tramaRecived = (Trama) flujoIn.readObject();

                switch (tramaRecived.getType()) {
                    case CLIENTS:
                        listMacs.clear();
                        listC.setText("");
                        String [] clientes = tramaRecived.getMessage().split("&");
                        String macks = "";
                        for(int i = 0; i < (clientes.length); i++) {
                            listMacs.add(clientes[i]);
                            macks += (i+1) + " ->" + clientes[i] + "\n";
                        }
                        listC.setText(macks);
                       // mostrarDatos();
                        this.respuesta.append("\nla conexion se establecio correctamente!");
                        break;
                    default:
                        this.respuesta.append("\ntrama: " + tramaRecived.getIp() + " --> " + tramaRecived.getMac() + " --> " + tramaRecived.getMessage());
                        this.respuesta.append("\nde: " + tramaRecived.getIp()  + " -->mensaje: " + tramaRecived.getMessage());
                        break;
                }

            }

        } catch (IOException e) {
            System.out.println("error mensaje recibido:  " + e);

        } catch (ClassNotFoundException ex) {
            Toast.makeText(this,"error en recibir mensaje" + ex, Toast.LENGTH_LONG).show();
            System.out.println("no hay red disponible...." + ex);
        }
    }
}