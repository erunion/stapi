package com.cezarykluczynski.stapi.server.comic_collection.endpoint;

import com.cezarykluczynski.stapi.client.v1.rest.model.ComicCollectionBaseResponse;
import com.cezarykluczynski.stapi.client.v1.rest.model.ComicCollectionFullResponse;
import com.cezarykluczynski.stapi.server.comic_collection.dto.ComicCollectionRestBeanParams;
import com.cezarykluczynski.stapi.server.comic_collection.reader.ComicCollectionRestReader;
import com.cezarykluczynski.stapi.server.common.dto.PageSortBeanParams;
import com.cezarykluczynski.stapi.server.configuration.CxfConfiguration;
import com.cezarykluczynski.stapi.util.constant.ContentType;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Service
@Produces(ContentType.APPLICATION_JSON_CHARSET_UTF8)
@CrossOriginResourceSharing(allowAllOrigins = CxfConfiguration.CORS_ALLOW_ALL_ORIGINS, maxAge = CxfConfiguration.CORS_MAX_AGE)
public class ComicCollectionRestEndpoint {

	public static final String ADDRESS = "/v1/rest/comicCollection";

	private final ComicCollectionRestReader comicCollectionRestReader;

	@Inject
	public ComicCollectionRestEndpoint(ComicCollectionRestReader comicCollectionRestReader) {
		this.comicCollectionRestReader = comicCollectionRestReader;
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public ComicCollectionFullResponse getComicCollection(@QueryParam("uid") String uid) {
		return comicCollectionRestReader.readFull(uid);
	}

	@GET
	@Path("search")
	@Consumes(MediaType.APPLICATION_JSON)
	public ComicCollectionBaseResponse searchComicCollection(@BeanParam PageSortBeanParams pageSortBeanParams) {
		return comicCollectionRestReader.readBase(ComicCollectionRestBeanParams.fromPageSortBeanParams(pageSortBeanParams));
	}

	@POST
	@Path("search")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ComicCollectionBaseResponse searchComicCollection(@BeanParam ComicCollectionRestBeanParams comicCollectionRestBeanParams) {
		return comicCollectionRestReader.readBase(comicCollectionRestBeanParams);
	}

}
