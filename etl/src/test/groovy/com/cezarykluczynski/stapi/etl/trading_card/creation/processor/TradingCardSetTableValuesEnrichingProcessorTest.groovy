package com.cezarykluczynski.stapi.etl.trading_card.creation.processor

import com.cezarykluczynski.stapi.etl.common.dto.EnrichablePair
import com.cezarykluczynski.stapi.etl.trading_card.creation.dto.CardSizeDTO
import com.cezarykluczynski.stapi.etl.trading_card.creation.dto.ProductionRunDTO
import com.cezarykluczynski.stapi.etl.trading_card.creation.dto.TradingCarSetHeaderValuePair
import com.cezarykluczynski.stapi.etl.trading_card.creation.dto.TradingCardSetTableHeader
import com.cezarykluczynski.stapi.model.country.entity.Country
import com.cezarykluczynski.stapi.model.trading_card_set.entity.TradingCardSet
import com.cezarykluczynski.stapi.model.trading_card_set.entity.enums.ProductionRunUnit
import com.google.common.collect.Sets
import spock.lang.Specification

class TradingCardSetTableValuesEnrichingProcessorTest extends Specification {

	private static final String PRODUCTION_RUN = 'PRODUCTION_RUN'
	private static final String CARDS_SIZE = 'CARDS_SIZE'
	private static final String COUNTRY = 'COUNTRY'
	private static final Integer PRODUCTION_RUN_INTEGER = 5000
	private static final ProductionRunUnit PRODUCTION_RUN_UNIT = ProductionRunUnit.BOX
	private static final double WIDTH = 3.5d
	private static final double HEIGHT = 2.25d

	private ProductionRunProcessor productionRunProcessorMock

	private CardSizeProcessor cardSizeProcessorMock

	private TradingCardSetCountiesProcessor tradingCardSetCountiesProcessorMock

	private TradingCardSetTableValuesEnrichingProcessor tradingCardSetTableValuesEnrichingProcessor

	void setup() {
		productionRunProcessorMock = Mock()
		cardSizeProcessorMock = Mock()
		tradingCardSetCountiesProcessorMock = Mock()
		tradingCardSetTableValuesEnrichingProcessor = new TradingCardSetTableValuesEnrichingProcessor(productionRunProcessorMock,
				cardSizeProcessorMock, tradingCardSetCountiesProcessorMock)
	}

	@SuppressWarnings('BracesForMethod')
	void """sets production run and production run unit from ProductionRunProcessor when it returns ProductionRunDTO,
			when production run part is found"""() {
		given:
		TradingCarSetHeaderValuePair tradingCarSetHeaderValuePair = new TradingCarSetHeaderValuePair(
				headerText: TradingCardSetTableHeader.PRODUCTION_RUN,
				valueText: PRODUCTION_RUN)
		TradingCardSet tradingCardSet = new TradingCardSet()
		ProductionRunDTO productionRunDTO = ProductionRunDTO.of(PRODUCTION_RUN_INTEGER, PRODUCTION_RUN_UNIT)

		when:
		tradingCardSetTableValuesEnrichingProcessor.enrich(EnrichablePair.of(tradingCarSetHeaderValuePair, tradingCardSet))

		then:
		1 * productionRunProcessorMock.process(PRODUCTION_RUN) >> productionRunDTO
		0 * _
		tradingCardSet.productionRun == PRODUCTION_RUN_INTEGER
		tradingCardSet.productionRunUnit == PRODUCTION_RUN_UNIT
	}

	void "when ProductionRunProcessor returns null, nothing happens"() {
		given:
		TradingCarSetHeaderValuePair tradingCarSetHeaderValuePair = new TradingCarSetHeaderValuePair(
				headerText: TradingCardSetTableHeader.PRODUCTION_RUN,
				valueText: PRODUCTION_RUN)
		TradingCardSet tradingCardSet = new TradingCardSet()

		when:
		tradingCardSetTableValuesEnrichingProcessor.enrich(EnrichablePair.of(tradingCarSetHeaderValuePair, tradingCardSet))

		then:
		1 * productionRunProcessorMock.process(PRODUCTION_RUN) >> null
		0 * _
		tradingCardSet.productionRun == null
		tradingCardSet.productionRunUnit == null
	}

	void "sets card width and card height from CardSizeProcessor when it returns CardSizeDTO when cards size part is found"() {
		given:
		TradingCarSetHeaderValuePair tradingCarSetHeaderValuePair = new TradingCarSetHeaderValuePair(
				headerText: TradingCardSetTableHeader.CARDS_SIZE,
				valueText: CARDS_SIZE)
		TradingCardSet tradingCardSet = new TradingCardSet()
		CardSizeDTO cardSizeDTO = new CardSizeDTO(
				width: WIDTH,
				height: HEIGHT)

		when:
		tradingCardSetTableValuesEnrichingProcessor.enrich(EnrichablePair.of(tradingCarSetHeaderValuePair, tradingCardSet))

		then:
		1 * cardSizeProcessorMock.process(CARDS_SIZE) >> cardSizeDTO
		0 * _
		tradingCardSet.cardWidth == WIDTH
		tradingCardSet.cardHeight == HEIGHT
	}

	void "when CardSizeProcessor returns null, nothing happens"() {
		given:
		TradingCarSetHeaderValuePair tradingCarSetHeaderValuePair = new TradingCarSetHeaderValuePair(
				headerText: TradingCardSetTableHeader.CARDS_SIZE,
				valueText: CARDS_SIZE)
		TradingCardSet tradingCardSet = new TradingCardSet()

		when:
		tradingCardSetTableValuesEnrichingProcessor.enrich(EnrichablePair.of(tradingCarSetHeaderValuePair, tradingCardSet))

		then:
		1 * cardSizeProcessorMock.process(CARDS_SIZE) >> null
		0 * _
		tradingCardSet.cardWidth == null
		tradingCardSet.cardHeight == null
	}

	void "add all countries from TradingCardSetCountiesProcessor when country part was found"() {
		given:
		TradingCarSetHeaderValuePair tradingCarSetHeaderValuePair = new TradingCarSetHeaderValuePair(
				headerText: TradingCardSetTableHeader.COUNTRY,
				valueText: COUNTRY)
		TradingCardSet tradingCardSet = new TradingCardSet()
		Country country1 = Mock()
		Country country2 = Mock()

		when:
		tradingCardSetTableValuesEnrichingProcessor.enrich(EnrichablePair.of(tradingCarSetHeaderValuePair, tradingCardSet))

		then:
		1 * tradingCardSetCountiesProcessorMock.process(COUNTRY) >> Sets.newHashSet(country1, country2)
		0 * _
		tradingCardSet.countriesOfOrigin.size() == 2
		tradingCardSet.countriesOfOrigin.contains country1
		tradingCardSet.countriesOfOrigin.contains country2
	}

}
