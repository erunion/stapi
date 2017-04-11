package com.cezarykluczynski.stapi.server.movie.reader

import com.cezarykluczynski.stapi.client.v1.soap.MovieBase
import com.cezarykluczynski.stapi.client.v1.soap.MovieBaseRequest
import com.cezarykluczynski.stapi.client.v1.soap.MovieBaseResponse
import com.cezarykluczynski.stapi.client.v1.soap.MovieFull
import com.cezarykluczynski.stapi.client.v1.soap.MovieFullRequest
import com.cezarykluczynski.stapi.client.v1.soap.MovieFullResponse
import com.cezarykluczynski.stapi.client.v1.soap.ResponsePage
import com.cezarykluczynski.stapi.model.movie.entity.Movie
import com.cezarykluczynski.stapi.server.common.mapper.PageMapper
import com.cezarykluczynski.stapi.server.common.validator.exceptions.MissingGUIDException
import com.cezarykluczynski.stapi.server.movie.mapper.MovieBaseSoapMapper
import com.cezarykluczynski.stapi.server.movie.mapper.MovieFullSoapMapper
import com.cezarykluczynski.stapi.server.movie.query.MovieSoapQuery
import com.google.common.collect.Lists
import org.springframework.data.domain.Page
import spock.lang.Specification

class MovieSoapReaderTest extends Specification {

	private static final String GUID = 'GUID'

	private MovieSoapQuery movieSoapQueryBuilderMock

	private MovieBaseSoapMapper movieBaseSoapMapperMock

	private MovieFullSoapMapper movieFullSoapMapperMock

	private PageMapper pageMapperMock

	private MovieSoapReader movieSoapReader

	void setup() {
		movieSoapQueryBuilderMock = Mock()
		movieBaseSoapMapperMock = Mock()
		movieFullSoapMapperMock = Mock()
		pageMapperMock = Mock()
		movieSoapReader = new MovieSoapReader(movieSoapQueryBuilderMock, movieBaseSoapMapperMock, movieFullSoapMapperMock, pageMapperMock)
	}

	void "passed base request to queryBuilder, then to mapper, and returns result"() {
		given:
		List<Movie> movieList = Lists.newArrayList()
		Page<Movie> moviePage = Mock()
		List<MovieBase> soapMovieList = Lists.newArrayList(new MovieBase(guid: GUID))
		MovieBaseRequest movieBaseRequest = Mock()
		ResponsePage responsePage = Mock()

		when:
		MovieBaseResponse movieResponse = movieSoapReader.readBase(movieBaseRequest)

		then:
		1 * movieSoapQueryBuilderMock.query(movieBaseRequest) >> moviePage
		1 * moviePage.content >> movieList
		1 * pageMapperMock.fromPageToSoapResponsePage(moviePage) >> responsePage
		1 * movieBaseSoapMapperMock.mapBase(movieList) >> soapMovieList
		movieResponse.movies[0].guid == GUID
		movieResponse.page == responsePage
	}

	void "passed full request to queryBuilder, then to mapper, and returns result"() {
		given:
		MovieFull movieFull = new MovieFull(guid: GUID)
		Movie movie = Mock()
		Page<Movie> moviePage = Mock()
		MovieFullRequest movieFullRequest = new MovieFullRequest(guid: GUID)

		when:
		MovieFullResponse movieFullResponse = movieSoapReader.readFull(movieFullRequest)

		then:
		1 * movieSoapQueryBuilderMock.query(movieFullRequest) >> moviePage
		1 * moviePage.content >> Lists.newArrayList(movie)
		1 * movieFullSoapMapperMock.mapFull(movie) >> movieFull
		movieFullResponse.movie.guid == GUID
	}

	void "requires GUID in full request"() {
		given:
		MovieFullRequest movieFullRequest = Mock()

		when:
		movieSoapReader.readFull(movieFullRequest)

		then:
		thrown(MissingGUIDException)
	}

}
