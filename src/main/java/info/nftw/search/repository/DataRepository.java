package info.nftw.search.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataRepository {

    /**
     * @return Unique Identifier field for the given dataset.
     */
    String getIdField();

    /**
     * @return Field names for the given dataset.
     */
    List<String> getFields();

    /**
     * @param matchingIds Set of unique identifier for each row.
     * @return List of items.
     */
    List<Map<String, String>> getMatchingItems(Set<String> matchingIds);

    /**
     * @return For the given field and fieldValue, return set of item ids.
     */
    Set<String> getItemIdsFromFieldAndValue(String field, String fieldValue);
}
