package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Servidor {

    private static final int PUERTO = 5555;
    private static Socket socket;
    private static ServerSocket serverSocket;
    private static DataInputStream flujoEntrada;
    private static DataOutputStream flujoSalida;
    private static String opcion;


    public Servidor() throws IOException {

        serverSocket = new ServerSocket(PUERTO);

        System.out.println("Esperando la conexión con el cliente");

        // creo objeto Socket para la conexion con el cliente
        socket = serverSocket.accept();// aquí se acepta la conexion
        System.out.println("Atiendo al cliente " + socket.getInetAddress().toString());

        // creo flujo de entrada del cliente
        flujoEntrada = new DataInputStream(socket.getInputStream());

        // creo el flujo de salida
        flujoSalida = new DataOutputStream(socket.getOutputStream());

        do {
            System.out.println("Esperando opción del cliente ...");
            opcion = flujoEntrada.readUTF();


            switch (opcion) {

                case "a":
                    System.out.println("Opción requerida :" + opcion + " ----> mandar listado de archivos.");
                    listarArchivosAlCliente(flujoSalida);
                    break;
                case "b":
                    System.out.println("Opción requerida :" + opcion + " ----> mandar archivo");

                    System.out.println("Esperando nombre del archivo requerido...");
                    String archivoAenviar = flujoEntrada.readUTF();

                    System.out.println("El cliente ha pedido el archivo " + archivoAenviar);

                    File archivorequerido = new File("directorioservidor/" + archivoAenviar);

                    if (archivorequerido.exists()) {
                        flujoSalida.writeUTF("si");

                        int size = (int) archivorequerido.length();

                        // Enviamos el nombre del archivo
                        flujoSalida.writeUTF(archivorequerido.getName());

                        // Enviamos el tamano del archivo
                        flujoSalida.writeInt(size);

                        // utilizo el flujo de salida especial
                        System.out.println("Enviando Archivo: " + archivorequerido.getName());

                        // creo un flujo de entrada para leer el archivo
                        FileInputStream fis = new FileInputStream(archivorequerido);
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

                        System.out.println("Archivo Enviado: " + archivorequerido.getName());

                    } else {
                        flujoSalida.writeUTF("no");
                    }

                    break;

                case "c":
                    System.out.println("Opción requerida :" + opcion + " ----> recibir archivo");

                    // recibo s/n del cliente
                    String sn = flujoEntrada.readUTF();

                    if(sn.equalsIgnoreCase("si")){

                        String name = flujoEntrada.readUTF();
                        System.out.println(sn.toUpperCase()+"Confirmado el envío; "+"Recibiendo el archivo del cliente " + name);

                        recibirArchivoYguardarlo(name);
                    }else if(sn.equalsIgnoreCase("no")){
                        System.out.println(sn.toUpperCase()+" confirmada la operación por el cliente");

                    }


                    break;
                case "d":

                    System.out.println("Opción requerida :" + opcion + " ----> fin de servicio");
                    System.out.println("Gracias por utilizar el servicio.");
                    break;

            }

        } while (!opcion.equalsIgnoreCase("d"));

        flujoSalida.close();
        flujoEntrada.close();
        socket.close();

    }

    public static void main(String[] args) throws IOException {
        new Servidor();
    }

    public static void listarArchivosAlCliente(DataOutputStream flujoSalida) {

        try (Stream<Path> files = Files.list(Paths.get("directorioservidor"))) {
            long count = files.count();

            flujoSalida.writeLong(count);

            System.out.println("Nº de archivos: " + count);

            File folder = new File("directorioservidor");
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    System.out.println("Archivo: " + listOfFiles[i].getName());
                    flujoSalida.writeUTF(listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directorio: " + listOfFiles[i].getName());
                    flujoSalida.writeUTF(listOfFiles[i].getName());
                }
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void recibirArchivoYguardarlo(String nombreArchivo) throws IOException {

        File file = new File("carpetaDelClienteEnServidor/" + nombreArchivo);

        // recibo el nombre y size del archivo solicitado
        System.out.println("Nombre del archivo a recibir: " + flujoEntrada.readUTF());

        int tam = flujoEntrada.readInt();
        System.out.println("Tamaño del archivo: " + tam);

        System.out.println("Recibiendo archivo " + nombreArchivo);


        // creo el flujo de entrada para indicar donde guardare el archivo
        FileOutputStream fos = new FileOutputStream("carpetaDelClienteEnServidor/" + nombreArchivo);


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
