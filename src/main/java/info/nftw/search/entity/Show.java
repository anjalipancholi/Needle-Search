package info.nftw.search.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Show {
    private final String showId;
    private final String type;
    private final String title;
    private final String director;
    private final String country;
    private final String dateAdded;
    private final String releaseYear;
    private final String rating;
    private final String duration;
    private final String listedIn;
    private final String description;
}
