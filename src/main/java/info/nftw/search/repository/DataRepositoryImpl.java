package info.nftw.search.repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class DataRepositoryImpl implements DataRepository {

    private String idField;
    private List<String> columns;
    private Map<String, Map<String, String>> itemMap;
    private Map<String, Map<String, Set<String>>> searchMap;

    @Autowired
    public DataRepositoryImpl(@Value("${data_file}") String filePath) throws IOException {
        itemMap = new HashMap<>();
        searchMap = new HashMap<>();

        InputStreamReader input = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
        CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(input);

        columns = records.getHeaderNames();
        idField = columns.get(0);
        for (String column : columns) {
            searchMap.put(column, new HashMap<>());
        }
        searchMap.put("actor", new HashMap<>());
        for (CSVRecord record : records) {
            String id = record.get(idField);
            Map<String, String> item = new LinkedHashMap<>();
            for (String column : columns) {
                item.put(column, record.get(column).trim().toLowerCase());
            }
            for (String column : columns) {
                Map<String, Set<String>> valueIdMap = searchMap.get(column);
                String value = record.get(column).trim().toLowerCase();
                if (column.equals("cast")) {
                    Map<String, Set<String>> actorMap = searchMap.get("actor");
                    String[] split = value.split(",");
                    for (String actor : split) {
                        actor = actor.trim().toLowerCase();
                        if (!actorMap.containsKey(actor)) {
                            actorMap.put(actor, new HashSet<>());
                        }
                        actorMap.get(actor).add(id);
                    }
                }
                if (!valueIdMap.containsKey(value)) {
                    valueIdMap.put(value, new HashSet<>());
                }
                valueIdMap.get(value).add(id);
                itemMap.put(id, ImmutableMap.copyOf(item));
            }
        }
    }

    @Override
    public String getIdField() {
        return idField;
    }

    @Override
    public List<String> getFields() {
        return columns;
    }

    @Override
    public List<Map<String, String>> getMatchingItems(Set<String> matchingIds) {
        List<Map<String, String>> items = new ArrayList<>();
        for (String matchingId : matchingIds) {
            items.add(itemMap.get(matchingId));
        }
        return ImmutableList.copyOf(items);
    }

    @Override
    public Set<String> getItemIdsFromFieldAndValue(String field, String fieldValue) {
        if (!searchMap.containsKey(field)) {
            return ImmutableSet.of();
        }
        if (!searchMap.get(field).containsKey(fieldValue)) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf(searchMap.get(field).get(fieldValue));
    }
}
