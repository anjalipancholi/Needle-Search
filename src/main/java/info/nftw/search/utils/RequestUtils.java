package info.nftw.search.utils;

import info.nftw.search.dto.SearchRequest;

public class RequestUtils {

    public static SearchRequest getSearchRequest(String query, String sortBy, String start, String count) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }
        if (sortBy != null && !sortBy.isEmpty() && !(sortBy.endsWith("_desc") || sortBy.endsWith("_asc"))) {
            throw new IllegalArgumentException("Invalid sortBy");
        }
        return new SearchRequest(query, sortBy, Integer.parseInt(start), Integer.parseInt(count));
    }
}
