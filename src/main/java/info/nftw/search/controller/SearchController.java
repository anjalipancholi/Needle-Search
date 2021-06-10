package info.nftw.search.controller;

import info.nftw.search.dto.SearchRequest;
import info.nftw.search.dto.SearchResponse;
import info.nftw.search.utils.RequestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(@RequestParam("q") String query,
                                                 @RequestParam("sortBy") String sortBy,
                                                 @RequestParam(name = "start", defaultValue = "0") String start,
                                                 @RequestParam(name = "count", defaultValue = "10") String count) {
        SearchRequest searchRequest = null;
        try {
            searchRequest = RequestUtils.getSearchRequest(query, sortBy, start, count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return null;
    }

}
