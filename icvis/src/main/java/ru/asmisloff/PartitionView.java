package ru.asmisloff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public final class PartitionView extends JFrame implements Runnable {

    int origin;
    float xScale = 1f;
    private boolean disposed;
    private final Layout lyt = new Layout();
    final Viewport vp = new Viewport(getWidth(), getHeight());

    public PartitionView(PartitionsDto dto) {
        disposed = false;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        BranchPartitions pp = new BranchPartitions(dto);
        lyt.setPartitions(pp);
        lyt.viewport().setWidth(640);
        lyt.viewport().setHeight(240);
        lyt.viewport().setScale(40, 1);
        lyt.viewport().setCenter(10, 0);
        add(lyt);
//        registerMouseListeners();
    }

    public static PartitionView fromJson(String json) {
        ObjectMapper m = new ObjectMapper();
        try {
            PartitionsDto dto = m.readValue(json, PartitionsDto.class);
            return new PartitionView(dto);
        } catch (JsonProcessingException e) {
            String msg = "Не удалось разобрать JSON";
            JOptionPane.showMessageDialog(null, msg);
            throw new IllegalArgumentException(msg, e);
        }
    }

    public static PartitionView fromClipboard() {
        try {
            String json = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            return PartitionView.fromJson(json);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Буфер обмена недоступен");
        } catch (UnsupportedFlavorException e) {
            throw new IllegalStateException("Неверный формат данных в буфере обмена");
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось получить данные из буфера обмена");
        }
    }

    @Override
    public void run() {
        var dim = getToolkit().getScreenSize();
        dim.height = Node.LINE_SPACING * (lyt.getPartitions().numLines() + 1);
        setSize(dim);
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }
}