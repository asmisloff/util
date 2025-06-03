package ru.asmisloff.model;

import java.util.Collections;
import java.util.List;

public record Partition(List<Cell> cells) { // TODO: удалить?

    public Partition {
        if (cells.isEmpty()) {
            throw new IllegalArgumentException("В разделе нет ячеек");
        }
    }

    public int xLeft() {
        return cells.get(0).leftSection().get(0).x();
    }

    public int xRight() {
        return cells.get(cells.size() - 1).rightSection().get(0).x();
    }

    @Override
    public List<Cell> cells() { return Collections.unmodifiableList(cells); }
}
