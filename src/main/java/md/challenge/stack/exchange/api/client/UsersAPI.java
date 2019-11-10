package md.challenge.stack.exchange.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsersAPI {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private HttpClient client;

	public UsersAPI(final HttpClient client) {
		super();

		this.client = client;
	}

	public String getSortedUsers(String uri, String stackSite, int pageSize, int page, String sortBy, int minScore, String order,
			String filter) {
		final String usersUri = ApiURIs.getSortedUsersUri(uri, stackSite, pageSize, page, sortBy, minScore, order, filter);
		logger.debug(
				"Retrieving Users of stackSite = {}, sort by = {}, minScore = {}, order = {}, with filter = {},  via URI = {} ",
				stackSite, sortBy, minScore, order, filter, usersUri);
		try {
			return accessUri(usersUri);
		} catch (final IOException ioEx) {
			logger.error("", ioEx);
		}

		return null;
	}

	public String getSortedUsersTags(String uri, String stackSite, List<Integer> userIDs, int pageSize, int page, String sortBy,
			String order, String filter) {
		String userTagsUri = ApiURIs.getSortedUsersTagsUri(uri, stackSite, userIDs, pageSize, page, sortBy, order, filter);
		logger.debug(
				"Retrieving Users of stackSite = {}, sort by = {}, pageSize = {}, order = {}, with filter = {},  via URI = {} ",
				stackSite, sortBy, pageSize, order, filter, userTagsUri);
		try {
			return accessUri(userTagsUri);
		} catch (final IOException ioEx) {
			logger.error("", ioEx);
		}

		return null;
	}

	final String accessUri(final String usersUri) throws IOException {
		HttpGet request = null;
		HttpEntity httpEntity = null;
		try {
			request = new HttpGet(usersUri);
			final HttpResponse httpResponse = client.execute(request);
			httpEntity = httpResponse.getEntity();
			final InputStream entityContentStream = httpEntity.getContent();
			final String outputAsEscapedHtml = IOUtils.toString(entityContentStream, Charset.forName("utf-8"));
			return outputAsEscapedHtml;
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		} finally {
			if (request != null) {
				request.releaseConnection();
			}
			if (httpEntity != null) {
				EntityUtils.consume(httpEntity);
			}
		}
	}

}
