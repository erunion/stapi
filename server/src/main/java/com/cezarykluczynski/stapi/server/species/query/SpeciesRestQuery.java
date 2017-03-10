package com.cezarykluczynski.stapi.server.species.query;

import com.cezarykluczynski.stapi.model.species.dto.SpeciesRequestDTO;
import com.cezarykluczynski.stapi.model.species.entity.Species;
import com.cezarykluczynski.stapi.model.species.repository.SpeciesRepository;
import com.cezarykluczynski.stapi.server.common.mapper.PageMapper;
import com.cezarykluczynski.stapi.server.species.dto.SpeciesRestBeanParams;
import com.cezarykluczynski.stapi.server.species.mapper.SpeciesRestMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SpeciesRestQuery {

	private SpeciesRestMapper speciesRequestMapper;

	private PageMapper pageMapper;

	private SpeciesRepository speciesRepository;

	@Inject
	public SpeciesRestQuery(SpeciesRestMapper speciesRestMapper, PageMapper pageMapper,
			SpeciesRepository speciesRepository) {
		this.speciesRequestMapper = speciesRestMapper;
		this.pageMapper = pageMapper;
		this.speciesRepository = speciesRepository;
	}

	public Page<Species> query(SpeciesRestBeanParams speciesRestBeanParams) {
		SpeciesRequestDTO speciesRequestDTO = speciesRequestMapper.map(speciesRestBeanParams);
		PageRequest pageRequest = pageMapper.fromPageSortBeanParamsToPageRequest(speciesRestBeanParams);
		return speciesRepository.findMatching(speciesRequestDTO, pageRequest);
	}


}
