package info.nftw.search.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import info.nftw.search.SearchApplication;
import info.nftw.search.dto.SearchRequest;
import info.nftw.search.dto.SearchResponse;
import info.nftw.search.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = {SearchApplication.class})
class SearchServiceImplTest {

    @MockBean
    private DataRepository dataRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void searchTest() {
        Mockito.when(dataRepository.getIdField()).thenReturn("id");
        Mockito.when(dataRepository.getFields()).thenReturn(ImmutableList.of("id", "name"));

        Map<String, String> item1 = ImmutableMap.of("id", "1", "name", "F1 L1");
        Map<String, String> item2 = ImmutableMap.of("id", "2", "name", "F2 L2");
        Map<String, String> item3 = ImmutableMap.of("id", "3", "name", "F3 L3");

        Mockito.when(dataRepository.getMatchingItems(ImmutableSet.of("1"))).thenReturn(ImmutableList.of(item1));
        Mockito.when(dataRepository.getMatchingItems(ImmutableSet.of("2"))).thenReturn(ImmutableList.of(item2));
        Mockito.when(dataRepository.getMatchingItems(ImmutableSet.of("3"))).thenReturn(ImmutableList.of(item3));
        Mockito.when(dataRepository.getMatchingItems(ImmutableSet.of("1", "2", "3"))).thenReturn(ImmutableList.of(item1, item2, item3));

        Mockito.when(dataRepository.getItemIdsFromFieldAndValue("id", "1")).thenReturn(ImmutableSet.of("1"));
        Mockito.when(dataRepository.getItemIdsFromFieldAndValue("id", "2")).thenReturn(ImmutableSet.of("2"));
        Mockito.when(dataRepository.getItemIdsFromFieldAndValue("id", "3")).thenReturn(ImmutableSet.of("3"));
        Mockito.when(dataRepository.getItemIdsFromFieldAndValue("name", "f1 l1")).thenReturn(ImmutableSet.of("1"));
        Mockito.when(dataRepository.getItemIdsFromFieldAndValue("name", "f2 l2")).thenReturn(ImmutableSet.of("2"));
        Mockito.when(dataRepository.getItemIdsFromFieldAndValue("name", "f3 l3")).thenReturn(ImmutableSet.of("3"));

        SearchRequest searchRequest = new SearchRequest("id:\"1\"", "id_asc", 0, 5);
        assertEquals(searchService.search(searchRequest).getItems().get(0).get("id"), "1");

        searchRequest = new SearchRequest("id:\"1\" AND id:\"2\"", "id_asc", 0, 5);
        assertEquals(searchService.search(searchRequest).getItems().size(), 0);


        searchRequest = new SearchRequest("id:\"1\" AND id:\"2\" OR id:\"3\"", "id_asc", 0, 5);
        assertEquals(searchService.search(searchRequest).getItems().get(0).get("id"), "3");

        searchRequest = new SearchRequest("id:\"1\" OR id:\"2\" OR id:\"3\"", "id_asc", 0, 5);
        assertEquals(searchService.search(searchRequest).getItems().size(), 3);

        searchRequest = new SearchRequest("id:\"1\" OR id:\"2\" OR id:\"3\"", "id_asc", 0, 2);
        assertEquals(searchService.search(searchRequest).getItems().size(), 2);

        searchRequest = new SearchRequest("id:\"1\" OR id:\"2\" OR id:\"3\"", "name_desc", 0, 3);
        SearchResponse searchResponse = searchService.search(searchRequest);
        assertEquals("3", searchResponse.getItems().get(0).get("id"));
        assertEquals("2", searchResponse.getItems().get(1).get("id"));
        assertEquals("1", searchResponse.getItems().get(2).get("id"));

        searchRequest = new SearchRequest("id:\"1\" OR id:\"2\" OR id:\"3\"", null, 0, 3);
        searchResponse = searchService.search(searchRequest);
        assertEquals("1", searchResponse.getItems().get(0).get("id"));
        assertEquals("2", searchResponse.getItems().get(1).get("id"));
        assertEquals("3", searchResponse.getItems().get(2).get("id"));
    }
}
