import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Main {
    private static final Logger log = Logger.getLogger(String.valueOf(Main.class));
    private static int count = 0;

    public static void main(String[] args) {
        log.info("Start");
        String jsonString = "{\"glossary\":{\"title\":\"example glossary\",\"GlossDiv\":{\"title\":\"S\",\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\",\"GlossTerm\":\"Standard Generalized Markup Language\",\"Acronym\":\"SGML\",\"Abbrev\":\"ISO 8879:1986\",\"GlossDef\":{\"para\":\"A meta-markup language, used to create markup languages such as DocBook.\",\"GlossSeeAlso\":[\"GML\",\"XML\"]},\"GlossSee\":\"markup\"}}}}}";
        try {
            if (args.length != 0) {
                String path = args[0];
                jsonString = Files.readString(Path.of(path), StandardCharsets.US_ASCII);
            }
        } catch (IOException ex) {
            log.info("Error reading file");
        }
        getKeysFromJson(jsonString);
        log.info("End");
    }

    static void getKeysFromJson(String jsoString) {
        Object things;
        try {
            things = new Gson().fromJson(jsoString, Object.class);
        } catch (JsonSyntaxException ex) {
            log.info("Json Syntax Error");
            return;
        }
        Map<String, Set<String>> res = new HashMap<>();
        collectAllTheKeys(things, res);
        log.info("Result");
        for (Map.Entry<String, Set<String>> entry : res.entrySet())
            System.out.println(entry.getKey() + ": " + entry.getValue().size() + " unique values");

    }

    @SuppressWarnings(value = "unchecked")
    static void collectAllTheKeys(Object o, Map<String, Set<String>> res) {
        Collection<?> values;
        if (o instanceof Map) {
            Map<String, ?> map = (Map<String, ?>) o;
            log.info("Get keys on level  " + ++count);
            values = map.values();
            log.info("Add keys and values on level " + ++count);
            for (Object k : map.keySet()) {
                if (!res.containsKey(k.toString()))
                    res.put(k.toString(), new HashSet<>());
                if (map.get(k.toString()) != null)
                    res.get(k.toString()).add(map.get(k.toString()).toString());
            }
        } else if (o instanceof Collection) {
            values = (Collection<?>) o;
        } else {
            log.info("Error. Is not a Map or Collection");
            return;
        }

        for (Object value : values)
            collectAllTheKeys(value, res);

    }
}
