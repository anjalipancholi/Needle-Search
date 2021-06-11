package info.nftw.search.utils;

import info.nftw.search.dto.SearchRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class RequestUtilsTest {

    @Test
    void getSearchRequest() {
        assertThrows(IllegalArgumentException.class, () -> RequestUtils.getSearchRequest(null, "_desc", null, null));
        assertThrows(IllegalArgumentException.class, () -> RequestUtils.getSearchRequest("query", "asc", null, null));
        assertThrows(NumberFormatException.class, () -> RequestUtils.getSearchRequest("query", null, null, null));
//        assertEquals(RequestUtils.getSearchRequest("query", null, null, null), new SearchRequest("query", null, 0, 10));
    }
}