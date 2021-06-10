package info.nftw.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SearchRequest {
    private String query;
    private String sortBy;
    private int start;
    private int count;
}
