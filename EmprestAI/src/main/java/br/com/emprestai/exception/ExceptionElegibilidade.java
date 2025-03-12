package br.com.emprestai.exception;

import java.util.ArrayList;
import java.util.List;

public class ExceptionElegibilidade extends RuntimeException {
    List<String> falhas = new ArrayList<>();

    public ExceptionElegibilidade(List<String> falhas){
        this.falhas = falhas;
    }

    public List<String> getFalhas() {
        return falhas;
    }
}
