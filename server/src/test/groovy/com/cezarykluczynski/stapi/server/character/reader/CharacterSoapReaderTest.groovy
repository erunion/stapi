package com.cezarykluczynski.stapi.server.character.reader

import com.cezarykluczynski.stapi.client.v1.soap.CharacterBase
import com.cezarykluczynski.stapi.client.v1.soap.CharacterBaseRequest
import com.cezarykluczynski.stapi.client.v1.soap.CharacterBaseResponse
import com.cezarykluczynski.stapi.client.v1.soap.CharacterFull
import com.cezarykluczynski.stapi.client.v1.soap.CharacterFullRequest
import com.cezarykluczynski.stapi.client.v1.soap.CharacterFullResponse
import com.cezarykluczynski.stapi.client.v1.soap.ResponsePage
import com.cezarykluczynski.stapi.model.character.entity.Character
import com.cezarykluczynski.stapi.server.character.mapper.CharacterBaseSoapMapper
import com.cezarykluczynski.stapi.server.character.mapper.CharacterFullSoapMapper
import com.cezarykluczynski.stapi.server.character.query.CharacterSoapQuery
import com.cezarykluczynski.stapi.server.common.mapper.PageMapper
import com.cezarykluczynski.stapi.server.common.validator.exceptions.MissingGUIDException
import com.google.common.collect.Lists
import org.springframework.data.domain.Page
import spock.lang.Specification

class CharacterSoapReaderTest extends Specification {

	private static final String GUID = 'GUID'

	private CharacterSoapQuery characterSoapQueryBuilderMock

	private CharacterBaseSoapMapper characterBaseSoapMapperMock

	private CharacterFullSoapMapper characterFullSoapMapperMock

	private PageMapper pageMapperMock

	private CharacterSoapReader characterSoapReader

	void setup() {
		characterSoapQueryBuilderMock = Mock()
		characterBaseSoapMapperMock = Mock()
		characterFullSoapMapperMock = Mock()
		pageMapperMock = Mock()
		characterSoapReader = new CharacterSoapReader(characterSoapQueryBuilderMock, characterBaseSoapMapperMock, characterFullSoapMapperMock,
				pageMapperMock)
	}

	void "passed base request to queryBuilder, then to mapper, and returns result"() {
		given:
		List<Character> characterList = Lists.newArrayList()
		Page<Character> characterPage = Mock()
		List<CharacterBase> soapCharacterList = Lists.newArrayList(new CharacterBase(guid: GUID))
		CharacterBaseRequest characterBaseRequest = Mock()
		ResponsePage responsePage = Mock()

		when:
		CharacterBaseResponse characterResponse = characterSoapReader.readBase(characterBaseRequest)

		then:
		1 * characterSoapQueryBuilderMock.query(characterBaseRequest) >> characterPage
		1 * characterPage.content >> characterList
		1 * pageMapperMock.fromPageToSoapResponsePage(characterPage) >> responsePage
		1 * characterBaseSoapMapperMock.mapBase(characterList) >> soapCharacterList
		characterResponse.characters[0].guid == GUID
		characterResponse.page == responsePage
	}

	void "passed full request to queryBuilder, then to mapper, and returns result"() {
		given:
		CharacterFull characterFull = new CharacterFull(guid: GUID)
		Character character = Mock()
		Page<Character> characterPage = Mock()
		CharacterFullRequest characterFullRequest = new CharacterFullRequest(guid: GUID)

		when:
		CharacterFullResponse characterFullResponse = characterSoapReader.readFull(characterFullRequest)

		then:
		1 * characterSoapQueryBuilderMock.query(characterFullRequest) >> characterPage
		1 * characterPage.content >> Lists.newArrayList(character)
		1 * characterFullSoapMapperMock.mapFull(character) >> characterFull
		characterFullResponse.character.guid == GUID
	}

	void "requires GUID in full request"() {
		given:
		CharacterFullRequest characterFullRequest = Mock()

		when:
		characterSoapReader.readFull(characterFullRequest)

		then:
		thrown(MissingGUIDException)
	}

}
