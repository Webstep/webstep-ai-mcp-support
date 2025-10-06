package no.webstep.internals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExceptionStringifier {

    public static String stringifyException(Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }


    public static String justCauses(Exception e) {
        final List<String> exceptionAndCausesNoStacktrace = new ArrayList<>();
        addCause(exceptionAndCausesNoStacktrace, e);
        return exceptionAndCausesNoStacktrace.stream().collect(Collectors.joining("\n"));
    }

    private static void addCause(List<String> list, Throwable e) {
        list.add(e.toString());
        if (e.getCause() != null) {
            addCause(list, e.getCause());
        }
    }
}
