package info.nftw.search.service;

import info.nftw.search.dto.SearchRequest;
import info.nftw.search.dto.SearchResponse;

public interface SearchService {
    SearchResponse search(SearchRequest searchRequest);
}
