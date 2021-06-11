package info.nftw.search.service;

import info.nftw.search.dto.SearchRequest;
import info.nftw.search.dto.SearchResponse;

public interface SearchService {
    /**
     * Process the {@link SearchRequest} and return the sorted matching results.
     */
    SearchResponse search(SearchRequest searchRequest);
}
