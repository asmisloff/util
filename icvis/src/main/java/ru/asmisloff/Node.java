package ru.asmisloff;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record Node(int i, int li, int x, boolean br) {

    int trackNumber() {
        return li - 10_000 * (li / 10_000);
    }
}
