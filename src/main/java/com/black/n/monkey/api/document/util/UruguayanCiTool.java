package com.black.n.monkey.api.document.util;

import com.black.n.monkey.api.document.exception.InvalidUruguayanCiException;
import org.apache.commons.lang3.StringUtils;

/**
 * https://twitter.com/federicod/status/1368944677370687495?s=24
 * <p>
 * #regex
 * Expresión regular para cédulas uruguayas. Sólo válida para cédulas mayores de 1 millón y funciona con y sin puntos y si el usuario utiliza diferentes separadores para el dígito verificador.
 * <p>
 * ^[1-9][\.]?\d{3}[\.]?\d{3}[\.\-/_]?[1-9]
 * <p>
 * Nota: puede haber soluciones mejores.
 */
public class UruguayanCiTool {

    private final static int[] CONST = {2, 9, 8, 7, 6, 3, 4};

    public static Integer calcLastVerificatorDigit(String ci) {

        int a = 0;

        if (ci.length() <= 7) {
            ci = StringUtils.leftPad(ci, 8, "0");
        }

        for (int i = 0; i < 7; i++) {

            a += CONST[i] * Integer.parseInt(String.valueOf(ci.charAt(i)));
        }

        if (a % 10 == 0) {
            return 0;
        } else {
            return 10 - a % 10;
        }
    }

    public static Boolean isValid(String ci) throws Exception {
        ci = StringUtils.getDigits(ci);

        if (ci.length() < 7) {
            throw new Exception("CI length should be 7 or 8 digits");
        }

        Integer lastDigit = Integer.parseInt(String.valueOf(ci.charAt(ci.length() - 1)));

        return lastDigit.compareTo(calcLastVerificatorDigit(ci)) == 0;
    }

    public static void validCi(String ci) {
        ci = StringUtils.getDigits(ci);

        if (ci.length() < 7) {
            throw new InvalidUruguayanCiException(ci);
        }

        Integer lastDigit = Integer.parseInt(String.valueOf(ci.charAt(ci.length() - 1)));

        if (lastDigit.compareTo(calcLastVerificatorDigit(ci)) != 0) {
            throw new InvalidUruguayanCiException(ci);
        }
    }

    public static Boolean isValidWithoutException(String ci) {

        if (StringUtils.isEmpty(ci)) {
            return false;
        }

        ci = StringUtils.getDigits(ci);

        if (ci.length() < 7) {
            return false;
        }

        Integer lastDigit = Integer.parseInt(String.valueOf(ci.charAt(ci.length() - 1)));

        return lastDigit.compareTo(calcLastVerificatorDigit(ci)) == 0;
    }

    public static String addMinusSymbol(String id) {
        if (id.contains("-")) {
            return id;
        }
        return String.format("%s-%s", StringUtils.substring(id, 0, id.length() - 1), StringUtils.substring(id, id.length() - 1));
    }

}
