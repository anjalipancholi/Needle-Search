package info.nftw.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SearchResponse {
    List<Map<String, String>> items;
}
