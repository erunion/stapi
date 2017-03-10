package com.cezarykluczynski.stapi.etl.template.species.processor;

import com.cezarykluczynski.stapi.etl.common.dto.EnrichablePair;
import com.cezarykluczynski.stapi.etl.common.service.PageBindingService;
import com.cezarykluczynski.stapi.etl.template.service.TemplateFinder;
import com.cezarykluczynski.stapi.etl.template.species.dto.SpeciesTemplate;
import com.cezarykluczynski.stapi.etl.template.species.service.SpeciesTemplateFilter;
import com.cezarykluczynski.stapi.etl.util.TitleUtil;
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Page;
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Template;
import com.cezarykluczynski.stapi.util.constant.TemplateTitle;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class SpeciesTemplatePageProcessor implements ItemProcessor<Page, SpeciesTemplate> {

	private SpeciesTemplateFilter speciesTemplateFilter;

	private PageBindingService pageBindingService;

	private TemplateFinder templateFinder;

	private SpeciesTemplatePartsEnrichingProcessor speciesTemplatePartsEnrichingProcessor;

	private SpeciesTemplateTypeEnrichingProcessor speciesTemplateTypeEnrichingProcessor;

	@Inject
	public SpeciesTemplatePageProcessor(SpeciesTemplateFilter speciesTemplateFilter, PageBindingService pageBindingService,
			TemplateFinder templateFinder, SpeciesTemplatePartsEnrichingProcessor speciesTemplatePartsEnrichingProcessor,
			SpeciesTemplateTypeEnrichingProcessor speciesTemplateTypeEnrichingProcessor) {
		this.speciesTemplateFilter = speciesTemplateFilter;
		this.pageBindingService = pageBindingService;
		this.templateFinder = templateFinder;
		this.speciesTemplatePartsEnrichingProcessor = speciesTemplatePartsEnrichingProcessor;
		this.speciesTemplateTypeEnrichingProcessor = speciesTemplateTypeEnrichingProcessor;
	}

	@Override
	public SpeciesTemplate process(Page item) throws Exception {
		if (speciesTemplateFilter.shouldBeFilteredOut(item)) {
			return null;
		}

		SpeciesTemplate speciesTemplate = new SpeciesTemplate();

		speciesTemplate.setName(TitleUtil.getNameFromTitle(item.getTitle()));
		speciesTemplate.setPage(pageBindingService.fromPageToPageEntity(item));

		speciesTemplateTypeEnrichingProcessor.enrich(EnrichablePair.of(item, speciesTemplate));

		Optional<Template> templateOptional = templateFinder.findTemplate(item, TemplateTitle.SIDEBAR_SPECIES);

		if (templateOptional.isPresent()) {
			speciesTemplatePartsEnrichingProcessor.enrich(EnrichablePair.of(templateOptional.get().getParts(), speciesTemplate));
		}

		return speciesTemplate;
	}

}
