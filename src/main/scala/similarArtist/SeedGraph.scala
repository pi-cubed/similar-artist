package similar_artist

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Category
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified
import com.wrapper.spotify.model_objects.specification.PlaylistTrack
import com.wrapper.spotify.model_objects.specification.ArtistSimplified
import com.wrapper.spotify.exceptions.detailed.NotFoundException
import org.neo4j.driver.v1.Values.parameters
import java.lang.AutoCloseable


object SeedGraph {
	
	val spotify = getSpotify

	val database = new Database()


  	def main(args: Array[String]) {
  		try {

		  	val artists = getSeedArtists

		  	println("found " + artists.length + " seed artists")

		  	mergeArtists(artists)

		} finally {
		  	database.close
		}
	}


	def getSpotify = {
		val spotify = new SpotifyApi.Builder()
		  .setClientId(sys.env("SPOTIFY_CLIENT_ID"))
		  .setClientSecret(sys.env("SPOTIFY_CLIENT_SECRET"))
		  .build

		println("fetching Spotify access token")
		val token = spotify.clientCredentials()
			.build
			.execute
			.getAccessToken
		
		spotify.setAccessToken(token)
		spotify
	}


	def getSeedArtists: Array[ArtistSimplified] = {
		val categories = getCategories

		val playlists = getPlaylists(categories)

		val tracks = getTracks(playlists)

		getArtists(tracks)
	}

	def getCategories = {
		println("fetching list of categories")
		spotify.getListOfCategories
			.limit(50)
			.build
			.execute
			.getItems
	}

	def getPlaylists(categories: Array[Category]) = {
		println("fetching category playlists")
		categories.flatMap(cat => 
			try {
				Some(spotify.getCategorysPlaylists(cat.getId)
					.limit(50)
					.build
					.execute
					.getItems)
			} catch {
				case e: NotFoundException => None
			}
		).flatten
	}

	def getTracks(playlists: Array[PlaylistSimplified]) = {
		println("fetching playlist tracks")
		playlists.flatMap(pl =>
			spotify.getPlaylistsTracks(pl.getId)
				.fields("items(track(artists))")
				.build
				.execute
				.getItems
		)
	}

	def getArtists(tracks: Array[PlaylistTrack]) = {
		println("getting track artists")
		tracks.flatMap(pt => Option(pt.getTrack))
			.flatMap(_.getArtists)
			.distinct
	}

	def mergeArtists(artists: Array[ArtistSimplified]) = {
		println("merging artists into db")
		database.withTransaction { tx =>
			artists.foreach { artist =>
				tx.run(
					"MERGE (:Artist {name: {name}, spotifyId: {id}})",
					parameters("name", artist.getName, "id", artist.getId)
				)
			}
		}
	}

}
