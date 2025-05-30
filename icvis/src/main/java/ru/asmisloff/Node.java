package ru.asmisloff;

/**
 * @param index Индекс узла.
 * @param x  Логическая координата узла, м.
 * @param li Индекс линии.
 * @param br true, если узел разрывной.
 */
public record Node(int index, int x, int li, boolean br) {

    public Node(NodeDto dto) {
        this(dto.i(), dto.x(), dto.li(), dto.br());
    }

    public int trackNumber() {
        return li % 10_000;
    }
}
