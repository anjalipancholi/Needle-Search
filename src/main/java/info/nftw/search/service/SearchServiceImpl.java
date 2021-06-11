package info.nftw.search.service;

import info.nftw.search.dto.SearchRequest;
import info.nftw.search.dto.SearchResponse;
import info.nftw.search.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SearchServiceImpl implements SearchService {

    private static final String AND = "AND";
    private static final String OR = "OR";

    @Autowired
    private DataRepository dataRepository;

    @Override
    public SearchResponse search(SearchRequest searchRequest) {
        Set<String> matchingIds = getMatchingIds(searchRequest.getQuery());
        List<Map<String, String>> matchingItems = new ArrayList<>(dataRepository.getMatchingItems(matchingIds));
        final String sortBy = searchRequest.getSortBy();
        matchingItems.sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> m1, Map<String, String> m2) {
                int compare = 0;
                if (sortBy != null && !sortBy.isEmpty()) {
                    if (sortBy.endsWith("_desc")) {
                        String[] split = sortBy.split("_desc");
                        compare = m1.getOrDefault(split[0], "").compareTo(m2.getOrDefault(split[0], ""));
                        compare = -compare;
                    } else {
                        String[] split = sortBy.split("_asc");
                        compare = m1.getOrDefault(split[0], "").compareTo(m2.getOrDefault(split[0], ""));
                    }
                }
                if (compare != 0) {
                    return compare;
                }
                String idField = dataRepository.getIdField();
                return m1.get(idField).compareTo(m2.get(idField));
            }
        });
        List<Map<String, String>> topItems = matchingItems;
        if (matchingItems.size() < searchRequest.getStart() && matchingItems.size() <= searchRequest.getStart() + searchRequest.getCount()) {
            topItems = matchingItems.subList(searchRequest.getStart(), searchRequest.getStart() + searchRequest.getCount());
        }
        return new SearchResponse(topItems);
    }

    //Service
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

    //service
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

    //service
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

    //service
    public Set<String> search(String query) {
        String[] split = query.split(":");
        String field = split[0];
        // Removing the quotes
        String fieldValue = split[1].substring(1, split[1].length() - 1).toLowerCase();
        Set<String> matchingIds = dataRepository.getItemIdsFromFieldAndValue(field, fieldValue);
        return matchingIds;
    }

    public Set<String> getMatchingIds(String query) {
        List<List<String>> unionConditions = getUnionConditions(query);
        Set<String> result = new HashSet<>();
        for (List<String> intersectionConditions : unionConditions) {
            result.addAll(getIntersectionResults(intersectionConditions));
        }
        return result;
    }
}
