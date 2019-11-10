package Vista;

import com.company.ArchivosTableModel;
import com.company.Cliente;
import com.company.Servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class AppView {
    private JPanel contenedorPrincipal;
    private JScrollPane contenedorScrollPane;
    private JPanel contenedorOpciones;
    private JButton buttonListar;
    private JButton buttonDowload;
    private JTextField texfieldDescarga;
    private JTable table1;
    private JButton buttonConexion;

    public AppView() {


        buttonListar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                ArrayList<String> datos = new ArrayList<>();

                table1.setModel(new ArchivosTableModel(datos));

            }
        });
        buttonDowload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        buttonConexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    Servidor servidor = new Servidor();


                } catch (IOException   e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void main(String[] args) {

        JFrame jFrame = new JFrame("AppView");
        jFrame.setContentPane(new AppView().contenedorPrincipal);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

    }
}
