package com.cezarykluczynski.stapi.etl.template.actor.processor

import com.cezarykluczynski.stapi.etl.template.actor.dto.ActorTemplate
import com.cezarykluczynski.stapi.etl.template.common.dto.Gender
import com.cezarykluczynski.stapi.etl.template.common.processor.gender.PartToGenderProcessor
import com.cezarykluczynski.stapi.wiki.dto.Template
import com.google.common.collect.Lists
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class ActorTemplateTemplateProcessorTest extends Specification {

	private static final String NAME = 'NAME'
	private static final String BIRTH_NAME = 'BIRTH_NAME'
	private static final String PLACE_OF_BIRTH = 'PLACE_OF_BIRTH'
	private static final String PLACE_OF_DEATH = 'PLACE_OF_DEATH'
	private static final String NOT_EMPTY_STRING = 'NOT_EMPTY_STRING'
	private static final Gender GENDER = Gender.F

	private PartToGenderProcessor partToGenderProcessorMock

	private ActorTemplateTemplateProcessor actorTemplateTemplateProcessor

	def setup() {
		partToGenderProcessorMock = Mock(PartToGenderProcessor)
		actorTemplateTemplateProcessor = new ActorTemplateTemplateProcessor(partToGenderProcessorMock)
	}

	def "valid template is parsed"() {
		given:
		Template.Part genderPart = new Template.Part(key: ActorTemplateTemplateProcessor.GENDER, value: NOT_EMPTY_STRING)
		Template template = new Template(parts: Lists.newArrayList(
				new Template.Part(key: ActorTemplateTemplateProcessor.NAME, value: NAME),
				new Template.Part(key: ActorTemplateTemplateProcessor.BIRTH_NAME, value: BIRTH_NAME),
				new Template.Part(key: ActorTemplateTemplateProcessor.PLACE_OF_BIRTH, value: PLACE_OF_BIRTH),
				new Template.Part(key: ActorTemplateTemplateProcessor.PLACE_OF_DEATH, value: PLACE_OF_DEATH),
				genderPart
		))

		when:
		ActorTemplate actorTemplate = actorTemplateTemplateProcessor.process(template)

		then: 'gender parsing is delegated'
		1 * partToGenderProcessorMock.process(genderPart) >> GENDER

		then: 'all values are parsed'
		actorTemplate.name == NAME
		actorTemplate.birthName == BIRTH_NAME
		actorTemplate.placeOfBirth == PLACE_OF_BIRTH
		actorTemplate.placeOfDeath == PLACE_OF_DEATH
		actorTemplate.gender == GENDER
	}

	def "blank values are not set"() {
		given:
		Template template = new Template(parts: Lists.newArrayList(
				new Template.Part(key: ActorTemplateTemplateProcessor.NAME, value: StringUtils.EMPTY),
				new Template.Part(key: ActorTemplateTemplateProcessor.BIRTH_NAME, value: StringUtils.EMPTY),
				new Template.Part(key: ActorTemplateTemplateProcessor.GENDER, value: StringUtils.EMPTY),
				new Template.Part(key: ActorTemplateTemplateProcessor.PLACE_OF_BIRTH, value: StringUtils.EMPTY),
				new Template.Part(key: ActorTemplateTemplateProcessor.PLACE_OF_DEATH, value: StringUtils.EMPTY)
		))

		when:
		ActorTemplate actorTemplate = actorTemplateTemplateProcessor.process(template)

		then: 'gender parsing is delegated'
		0 * partToGenderProcessorMock.process(_)

		then: 'all values are parsed'
		actorTemplate.name == null
		actorTemplate.birthName == null
		actorTemplate.placeOfBirth == null
		actorTemplate.placeOfDeath == null
		actorTemplate.gender == null
	}

}
