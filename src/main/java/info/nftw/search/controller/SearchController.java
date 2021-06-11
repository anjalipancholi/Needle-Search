package info.nftw.search.controller;

import info.nftw.search.dto.SearchRequest;
import info.nftw.search.dto.SearchResponse;
import info.nftw.search.service.SearchService;
import info.nftw.search.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResponse> search(@RequestParam("q") String query,
                                                 @RequestParam(name = "sortBy", defaultValue = "id_asc", required = false) String sortBy,
                                                 @RequestParam(name = "start", defaultValue = "0", required = false) String start,
                                                 @RequestParam(name = "count", defaultValue = "10", required = false) String count) {
        SearchRequest searchRequest = null;
        try {
            searchRequest = RequestUtils.getSearchRequest(query, sortBy, start, count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(searchService.search(searchRequest), HttpStatus.OK);
    }

}
