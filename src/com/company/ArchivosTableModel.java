package com.company;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ArchivosTableModel extends AbstractTableModel {

    private String[] columnas = {"Nombre Archivo"};
    private List<String> archivos;

    public ArchivosTableModel(List<String> archivos) {
        this.archivos = archivos;
    }

    @Override
    public int getRowCount() {
        return archivos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        String a = archivos.get(rowIndex);
        switch (columnIndex){
            case 0:
                return a;

        }
        return null;


    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }
}
