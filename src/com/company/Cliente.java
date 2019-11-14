package com.company;

import java.io.*;
import java.net.Socket;

public class Cliente {

    private String Host = "localhost";
    private int Puerto = 5555;
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static Socket socket;
    private static String respuesta;
    private static String opcion;
    private static DataOutputStream flujoSalida;
    private static DataInputStream flujoEntrada;

    // constructor
    public Cliente() throws IOException, ClassNotFoundException {


        socket = new Socket(Host, Puerto);

        // creo el flujo de salida
        flujoSalida = new DataOutputStream(socket.getOutputStream());

        // creo el flujo de entrada
        flujoEntrada = new DataInputStream(socket.getInputStream());


        do {

            // menu que se muestra al usuario
            System.out.println("Menú: \n" +
                    "a) Listar\n" +
                    "b) Bajar fichero\n" +
                    "c) Subir fichero\n" +
                    "d) Salir");

            System.out.println("Opción: ");
            opcion = br.readLine();

            switch (opcion) {

                case "a":
                    System.out.println("Listado de archivos que hay en el servidor:");
                    flujoSalida.writeUTF(opcion);

                    // obtengo el nº de archivos del servidor
                    long size = flujoEntrada.readLong();
                    System.out.println("Número de archivos: " + size);

                    // lo recorro por si hay variación de num de archivos
                    for (int i = 0; i < size; i++) {
                        System.out.println("\t" + flujoEntrada.readUTF());
                    }

                    break;
                case "b":
                    // envio la opcion
                    flujoSalida.writeUTF(opcion);

                    System.out.println("Introduzca el nombre del archivo a recibir con su extensión:");
                    String nombreArchivo = br.readLine();

                    // envio el nombre del archivo que deseo
                    flujoSalida.writeUTF(nombreArchivo);

                    // compruebo si existe o no en el servidor si/no
                    String sn = flujoEntrada.readUTF();

                    if (sn.equalsIgnoreCase("no")) {
                        System.out.println("El archivo solicitado NO existe en el servidor");
                    } else {
                        System.out.println(sn.toUpperCase() + " existe el archivo solicitado.");

                        // ejecuto la funcion correspondiente que realiza el trabajo
                        recibirArchivoYguardarlo(nombreArchivo);
                    }

                    break;
                case "c":
                    // envio la opcion
                    flujoSalida.writeUTF(opcion);

                    System.out.println("Introduzca el nombre del archivo a subir con su extensión:");
                    String nombreArchivoAsubir = br.readLine();

                    // compruebo si existe el archivo
                    File archivorequerido = new File("directoriocliente/" + nombreArchivoAsubir);

                    if (archivorequerido.exists()) {

                        // le mando al servidor que si
                        flujoSalida.writeUTF("si");

                        // envio el nombre del archivo al servidor
                        flujoSalida.writeUTF(nombreArchivoAsubir);

                        // envio el archivo
                        enviarArchivoAlServidor(archivorequerido.toString());


                    } else {

                        System.out.println("Archivo NO existente en directoriocliente, introduzca un archivo existente");
                        flujoSalida.writeUTF("no");


                    }


                    break;
                case "d":
                    //envio la opcion c al servidor
                    flujoSalida.writeUTF(opcion);
                    System.out.println("Gracias por utilizar el servcio.");
                    break;

                default:
                    System.out.println("Introduzca una opción correcta: a, b o c.");

            }


        } while (!opcion.equalsIgnoreCase("d"));

    }

    // main
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Cliente();
    }

    // metodo par recibir y guardar el archivo
    public void recibirArchivoYguardarlo(String nombreArchivo) throws IOException {

        File file = new File("directoriocliente/" + nombreArchivo);

        if (file.exists()) {
            System.out.println("El archivo ya existe desea reemplazarlo ? s/n");
            String caso = br.readLine();

            switch (caso) {

                case "s":

                    // machaco el archivo aunque de la forma en que lo creo no haria falta
                    file.delete();

                    // recibo el nombre y size del archivo solicitado
                    System.out.println("Nombre del archivo solicitado: " + flujoEntrada.readUTF());

                    int tam = flujoEntrada.readInt();
                    System.out.println("Tamaño del archivo: " + tam);

                    System.out.println("Recibiendo archivo " + nombreArchivo);


                    // creo el flujo de entrada para indicar donde guardare el archivo
                    FileOutputStream fos = new FileOutputStream("directoriocliente/" + nombreArchivo);


                    //Ruta donde se va a guardar el archivo
                    BufferedOutputStream out = new BufferedOutputStream(fos);
                    BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

                    // Creamos el array de bytes para leer los datos del archivo
                    byte[] buffer = new byte[tam];

                    // Obtenemos el archivo mediante la lectura de bytes enviados
                    for (int i = 0; i < buffer.length; i++) {
                        buffer[i] = (byte) in.read();
                    }

                    // Escribimos el archivo
                    out.write(buffer);

                    // Cerramos flujos
                    out.flush();


                    System.out.println("Archivo " + nombreArchivo + " recibido y guardado correctamente.");


                    break;
                case "n":
                    System.out.println("El archivo no se ha reemplazado");
                    break;
                default:
                    System.out.println("Introduzca s/n");
            }


        } else {

            // recibo el nombre y size del archivo solicitado
            System.out.println("Nombre del archivo solicitado: " + flujoEntrada.readUTF());

            int tam = flujoEntrada.readInt();
            System.out.println("Tamaño del archivo: " + tam);

            System.out.println("Recibiendo archivo " + nombreArchivo);


            // creo el flujo de entrada para indicar donde guardare el archivo
            FileOutputStream fos = new FileOutputStream("directoriocliente/" + nombreArchivo);


            //Ruta donde se va a guardar el archivo
            BufferedOutputStream out = new BufferedOutputStream(fos);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

            // Creamos el array de bytes para leer los datos del archivo
            byte[] buffer = new byte[tam];

            // Obtenemos el archivo mediante la lectura de bytes enviados
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (byte) in.read();
            }

            // Escribimos el archivo
            out.write(buffer);

            // Cerramos flujos
            out.flush();


            System.out.println("Archivo " + nombreArchivo + " recibido y guardado correctamente.");


        }


    }

    // metodo para enviar el archivo al servidor
    public void enviarArchivoAlServidor(String nombreArchivoAsubir) throws IOException {


        System.out.println("Archivo existente en directoriocliente, se procede a la subida ...");


        int size = (int) nombreArchivoAsubir.length();

        // Enviamos el nombre del archivo
        flujoSalida.writeUTF(nombreArchivoAsubir);

        // Enviamos el tamano del archivo
        flujoSalida.writeInt(size);

        // utilizo el flujo de salida especial
        System.out.println("Subiendo Archivo: " + nombreArchivoAsubir);

        // creo un flujo de entrada para leer el archivo
        FileInputStream fis = new FileInputStream(nombreArchivoAsubir);
        BufferedInputStream bis = new BufferedInputStream(fis);

        // Creo el flujo de salida para enviar los datos del archivo
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());

        // creo un array de tipo byte con el size del archivo
        byte[] buffer = new byte[size];

        // leo el archivo
        bis.read(buffer);

        // Realizamos el envio de los bytes que conforman el archivo
        for (int i = 0; i < buffer.length; i++) {
            bos.write(buffer[i]);
        }

        bos.flush();

        System.out.println("Archivo Subido: " + nombreArchivoAsubir);


    }


}
