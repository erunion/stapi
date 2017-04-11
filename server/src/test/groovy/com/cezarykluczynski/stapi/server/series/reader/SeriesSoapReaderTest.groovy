package com.cezarykluczynski.stapi.server.series.reader

import com.cezarykluczynski.stapi.client.v1.soap.ResponsePage
import com.cezarykluczynski.stapi.client.v1.soap.SeriesBase
import com.cezarykluczynski.stapi.client.v1.soap.SeriesBaseRequest
import com.cezarykluczynski.stapi.client.v1.soap.SeriesBaseResponse
import com.cezarykluczynski.stapi.client.v1.soap.SeriesFull
import com.cezarykluczynski.stapi.client.v1.soap.SeriesFullRequest
import com.cezarykluczynski.stapi.client.v1.soap.SeriesFullResponse
import com.cezarykluczynski.stapi.model.series.entity.Series
import com.cezarykluczynski.stapi.server.common.mapper.PageMapper
import com.cezarykluczynski.stapi.server.common.validator.exceptions.MissingGUIDException
import com.cezarykluczynski.stapi.server.series.mapper.SeriesBaseSoapMapper
import com.cezarykluczynski.stapi.server.series.mapper.SeriesFullSoapMapper
import com.cezarykluczynski.stapi.server.series.query.SeriesSoapQuery
import com.google.common.collect.Lists
import org.springframework.data.domain.Page
import spock.lang.Specification

class SeriesSoapReaderTest extends Specification {

	private static final String GUID = 'GUID'

	private SeriesSoapQuery seriesSoapQueryBuilderMock

	private SeriesBaseSoapMapper seriesBaseSoapMapperMock

	private SeriesFullSoapMapper seriesFullSoapMapperMock

	private PageMapper pageMapperMock

	private SeriesSoapReader seriesSoapReader

	void setup() {
		seriesSoapQueryBuilderMock = Mock()
		seriesBaseSoapMapperMock = Mock()
		seriesFullSoapMapperMock = Mock()
		pageMapperMock = Mock()
		seriesSoapReader = new SeriesSoapReader(seriesSoapQueryBuilderMock, seriesBaseSoapMapperMock, seriesFullSoapMapperMock, pageMapperMock)
	}

	void "passed base request to queryBuilder, then to mapper, and returns result"() {
		given:
		List<Series> seriesList = Lists.newArrayList()
		Page<Series> seriesPage = Mock()
		List<SeriesBase> soapSeriesList = Lists.newArrayList(new SeriesBase(guid: GUID))
		SeriesBaseRequest seriesBaseRequest = Mock()
		ResponsePage responsePage = Mock()

		when:
		SeriesBaseResponse seriesResponse = seriesSoapReader.readBase(seriesBaseRequest)

		then:
		1 * seriesSoapQueryBuilderMock.query(seriesBaseRequest) >> seriesPage
		1 * seriesPage.content >> seriesList
		1 * pageMapperMock.fromPageToSoapResponsePage(seriesPage) >> responsePage
		1 * seriesBaseSoapMapperMock.mapBase(seriesList) >> soapSeriesList
		seriesResponse.series[0].guid == GUID
		seriesResponse.page == responsePage
	}

	void "passed full request to queryBuilder, then to mapper, and returns result"() {
		given:
		SeriesFull seriesFull = new SeriesFull(guid: GUID)
		Series series = Mock()
		Page<Series> seriesPage = Mock()
		SeriesFullRequest seriesFullRequest = new SeriesFullRequest(guid: GUID)

		when:
		SeriesFullResponse seriesFullResponse = seriesSoapReader.readFull(seriesFullRequest)

		then:
		1 * seriesSoapQueryBuilderMock.query(seriesFullRequest) >> seriesPage
		1 * seriesPage.content >> Lists.newArrayList(series)
		1 * seriesFullSoapMapperMock.mapFull(series) >> seriesFull
		seriesFullResponse.series.guid == GUID
	}

	void "requires GUID in full request"() {
		given:
		SeriesFullRequest seriesFullRequest = Mock()

		when:
		seriesSoapReader.readFull(seriesFullRequest)

		then:
		thrown(MissingGUIDException)
	}

}
