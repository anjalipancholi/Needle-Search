package info.nftw.search;

import info.nftw.search.entity.Show;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class InMemoryHack {

    private static final String AND = "AND";
    private static final String OR = "OR";

    private Map<String, Show> showMap;
    private Map<String, Map<String, Set<String>>> searchMap;

    private List<String> getQueryTokens(String query) {
        List<String> queryTokens = new ArrayList<>();
        boolean quotationOpen = false;
        int i = 0;
        for (int j = 0; j < query.length(); j++) {
            char c = query.charAt(j);
            if (c == '"') {
                quotationOpen = !quotationOpen;
            } else if (c == ' ') {
                if (!quotationOpen) {
                    queryTokens.add(query.substring(i, j));
                    i = j + 1;
                }
            }
        }
        queryTokens.add(query.substring(i));
        return queryTokens;
    }

    private List<List<String>> getUnionConditions(String query) {
        List<String> queryTokens = getQueryTokens(query);
        queryTokens.add(OR);
        List<List<String>> orConditions = new ArrayList<>();
        List<String> andConditions = new ArrayList<>();
        int i = 0;
        for (int j = 0; j < queryTokens.size(); j++) {
            if (OR.equalsIgnoreCase(queryTokens.get(j))) {
                for (int k = i; k < j; k++) {
                    if (!AND.equalsIgnoreCase(queryTokens.get(k))) {
                        andConditions.add(queryTokens.get(k));
                    }
                }
                orConditions.add(andConditions);
                andConditions = new ArrayList<>();
                i = j + 1;
            }
        }
        return orConditions;
    }

    private Set<String> getIntersectionResults(List<String> queries) {
        Set<String> set = null;
        for (String query : queries) {
            Set<String> searchResult = search(query);
            if (set == null) {
                set = new HashSet<>(searchResult);
            } else {
                set.retainAll(searchResult);
            }
        }
        return set;
    }

    public Set<String> searchOperations(String query) {
        List<List<String>> unionConditions = getUnionConditions(query);
        Set<String> result = new HashSet<>();
        for (List<String> intersectionConditions : unionConditions) {
            result.addAll(getIntersectionResults(intersectionConditions));
        }
        return result;
    }

    public Set<String> search(String query) {
        String[] split = query.split("=");
        Set<String> result = new HashSet<>();
        String key = split[0];
        String value = split[1].substring(1, split[1].length() - 1).toLowerCase();
        if (searchMap.containsKey(key)) {
            if (searchMap.get(key).containsKey(value)) {
                for (String showId : searchMap.get(key).get(value)) {
                    result.add(showId);
                }
            }
        }
        return result;
    }

    private void populateShowsAndSearchMapFromFile(String file) throws IOException {
        InputStreamReader input = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(input);
        searchMap = new HashMap<>();
        showMap = new HashMap<>();
        List<String> columns = records.getHeaderNames();
        for (String column : columns) {
            searchMap.put(column, new HashMap<>());
        }
        searchMap.put("actor", new HashMap<>());
        for (CSVRecord record : records) {
            String showId = record.get("show_id");
            Show show = new Show(record.get("show_id"),
                    record.get("type"),
                    record.get("title"),
                    record.get("director"),
                    record.get("cast"),
                    record.get("country"),
                    record.get("date_added"),
                    record.get("release_year"),
                    record.get("rating"),
                    record.get("duration"),
                    record.get("listed_in"),
                    record.get("description"));
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
                        actorMap.get(actor).add(showId);
                    }
                }
                if (!valueIdMap.containsKey(value)) {
                    valueIdMap.put(value, new HashSet<>());
                }
                valueIdMap.get(value).add(showId);
                showMap.put(showId, show);
            }
        }
    }

    public static void main(String[] args) throws IOException {
//        Set<String> requiredShows = new InMemoryHack().search("actor=\"Hugh Jackman\"", "/home/anjali/Downloads/netflix_titles.csv");
        InMemoryHack inMemoryHack = new InMemoryHack();
        inMemoryHack.populateShowsAndSearchMapFromFile("/home/anjali/Downloads/netflix_titles.csv");
        System.out.println(inMemoryHack.searchOperations("actor=\"Kiara Advani\" AND show_id=\"s2550\" OR show_id=\"s3284\""));
    }
}

