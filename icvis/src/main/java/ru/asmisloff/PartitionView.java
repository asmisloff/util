package ru.asmisloff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.asmisloff.dto.BranchPartitionsDto;
import ru.asmisloff.model.BranchPartitions;
import ru.asmisloff.model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.SortedMap;
import java.util.function.Consumer;

public final class PartitionView extends JFrame implements Runnable {

    private boolean disposed;
    private final Layout lyt;

    public PartitionView(BranchPartitionsDto dto) {
        disposed = false;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        lyt = new Layout(new BranchPartitions(dto));
        getContentPane().add(lyt, BorderLayout.CENTER);
        lyt.setOnMouseMoved(new Consumer<>() {

            private float mxPrev = 0f;

            @Override
            public void accept(MouseEvent e) {
                float mx = lyt.viewport().mx(e.getX());
                if (mxPrev != mx) {
                    setTitle(String.format("%.3f", mx));
                    mxPrev = mx;
                }
            }
        });
    }

    public static PartitionView fromJson(String json) {
        ObjectMapper m = new ObjectMapper();
        try {
            TypeReference<SortedMap<Integer, BranchPartitionsDto>> typeRef = new TypeReference<>() { };
            SortedMap<Integer, BranchPartitionsDto> dto = m.readValue(json, typeRef);
            return new PartitionView(dto.get(0));
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