package info.nftw.search.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResponse {
    List<Map<String, String>> items;
}
