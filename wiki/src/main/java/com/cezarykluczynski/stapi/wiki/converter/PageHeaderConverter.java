package com.cezarykluczynski.stapi.wiki.converter;

import com.cezarykluczynski.stapi.util.constants.MemoryAlpha;
import com.cezarykluczynski.stapi.wiki.dto.PageHeader;
import info.bliki.api.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PageHeaderConverter {

	public List<PageHeader> fromPageInfoList(List<PageInfo> pageInfoList) {
		return pageInfoList.stream()
				.filter(pageInfo -> pageInfo.getNs().equals(MemoryAlpha.CONTENT_NAMESPACE))
				.map(this::fromPageInfo)
				.collect(Collectors.toList());
	}

	public PageHeader fromPageInfo(PageInfo pageInfo) {
		return PageHeader.builder()
				.pageId(Long.valueOf(pageInfo.getPageid()))
				.title(pageInfo.getTitle())
				.build();
	}

}
