package dario.gmori.webpageapi.spotify;

import dario.gmori.webpageapi.common.requests.spotify.SpotifyRequestUtils;
import dario.gmori.webpageapi.spotify.artists.Artist;
import dario.gmori.webpageapi.spotify.artists.ArtistRepository;
import dario.gmori.webpageapi.spotify.artists.mappers.SpotifyArtistJsonModelMapper;
import dario.gmori.webpageapi.spotify.dto.SpotifyUserResponseDto;
import dario.gmori.webpageapi.spotify.mappers.SpotifyUserJsonModelMapper;
import dario.gmori.webpageapi.spotify.mappers.SpotifyUserResponseDtoMapper;
import dario.gmori.webpageapi.spotify.songs.Song;
import dario.gmori.webpageapi.spotify.songs.SongRepository;
import dario.gmori.webpageapi.spotify.songs.mappers.SpotifySongsJsonModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SpotifyUserService {

    private final SpotifyUserRepository spotifyUserRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final SpotifyUserResponseDtoMapper spotifyUserResponseDtoMapper;
    private final SpotifyUserJsonModelMapper spotifyUserJsonModelMapper;
    private final SpotifySongsJsonModelMapper spotifySongsJsonModelMapper;
    private final SpotifyArtistJsonModelMapper artistJsonModelMapper;

    private final SpotifyRequestUtils spotifyRequestUtils;
    public SpotifyUserResponseDto getSpotifyInfo() {
        Optional<SpotifyUser> spotifyUser = spotifyUserRepository.findUserInfo();
        if(spotifyUser.isEmpty() ){ // No user found, create a new one
            return spotifyUserResponseDtoMapper.apply(createNewSpotifyUser());
        } else if(spotifyUser.get().timeToRefreshPassed(10)) { // User found, but it's time to refresh
            return spotifyUserResponseDtoMapper.apply(updateSpotifyUser(spotifyUser.get()));
        }else{
            return spotifyUserResponseDtoMapper.apply(spotifyUser.get()); // User found and it's not time to refresh
        }
    }

    private SpotifyUser updateSpotifyUser(SpotifyUser spotifyUser) {
        List<Song> songsList = spotifySongsJsonModelMapper.apply(spotifyRequestUtils.getTopSongs());
        for( Song song : songsList){
            song.setUser(spotifyUser);
        }
        Set<Artist> artistList = artistJsonModelMapper.apply(spotifyRequestUtils.getTopArtists().get("items"));
        for( Artist artist : artistList){
            artist.setUser(spotifyUser);
        }
        spotifyUser.updateUser(spotifyUserJsonModelMapper.apply(spotifyRequestUtils.getSpotifyInformation()),saveSongsNotRepeated(songsList),saveArtistsNotRepeated(artistList));
        return spotifyUserRepository.save(spotifyUser);

    }

    private SpotifyUser createNewSpotifyUser() {
        SpotifyUser newSpotifyUser = spotifyUserJsonModelMapper.apply(spotifyRequestUtils.getSpotifyInformation());
        List<Song> songsList = spotifySongsJsonModelMapper.apply(spotifyRequestUtils.getTopSongs());
        newSpotifyUser.setTopSongs(saveSongsNotRepeated(songsList));
        newSpotifyUser.setId(1L);
        newSpotifyUser.setLastModifiedDate(LocalDateTime.now());
        newSpotifyUser = spotifyUserRepository.save(newSpotifyUser);
        for( Song song : songsList){
            song.setUser(newSpotifyUser);
        }
        songRepository.saveAll(songsList);
        return newSpotifyUser;
    }

    private List<Song> saveSongsNotRepeated(List<Song> songsList) {
        artistRepository.saveAll(songsList.stream()
                .flatMap(song -> song.getArtists().stream())
                .distinct()// Remove duplicates
                .toList()
        );

        return songRepository.saveAll(songsList);
    }

    private List<Artist> saveArtistsNotRepeated(Set<Artist> artistList) {
        return artistRepository.saveAll(artistList);
    }
}
