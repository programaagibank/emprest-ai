package br.com.emprestai.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtils {
    public static LocalDate primeiroDiaUtil(LocalDate dia){
        if (dia == null) {
            throw new IllegalArgumentException("A data não pode ser nula");
        }
        dia = dia.plusDays(1);
        while(dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY){
            dia = dia.plusDays(1);
        }
        return dia;
    }
}
