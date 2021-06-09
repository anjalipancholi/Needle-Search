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

    private Map<String, Show> showMap;
    private Map<String, Map<String, Set<String>>> searchMap;

    // actor="Hugh Jackman"
    public List<Show> search(String query, String file) throws IOException {
        String[] split = query.split("=");
        List<Show> result = new ArrayList<>();
        populateShowsAndSearchMapFromFile(file);
        String key = split[0];
        String value = split[1].substring(1, split[1].length() - 1).toLowerCase();
        if (searchMap.containsKey(key)) {
            if (searchMap.get(key).containsKey(value)) {
                for (String showId : searchMap.get(key).get(value)) {
                    result.add(showMap.get(showId));
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
                String value = record.get(column).toLowerCase();
                if (!valueIdMap.containsKey(value)) {
                    valueIdMap.put(value, new HashSet<>());
                }
                valueIdMap.get(value).add(showId);
            }
            showMap.put(showId, show);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Show> requiredShows = new InMemoryHack().search("director=\"Shawn Levy\"", "/home/anjali/Downloads/netflix_titles.csv");
        System.out.println(requiredShows);
    }
}

