package dario.gmori.webpageapi.spotify.artists;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dario.gmori.webpageapi.spotify.songs.Song;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Set;

@Entity
@Table(name = "artist")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Artist {
    @Id
    private String name;

    @Column(name="avatar_url")
    private String avatarUrl;

    @Column(name="profile_url")
    private String profileUrl;

    private Integer followers;

    private String genres;

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "artists")
    @Fetch(value = FetchMode.SELECT)
    @JsonIgnore
    private Set<Song> songs;

}
