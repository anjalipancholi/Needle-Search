package info.nftw.search.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataRepository {
    String getIdField();

    List<String> getFields();

    List<Map<String, String>> getMatchingItems(Set<String> matchingIds);

    Set<String> getItemIdsFromFieldAndValue(String field, String fieldValue);
}
