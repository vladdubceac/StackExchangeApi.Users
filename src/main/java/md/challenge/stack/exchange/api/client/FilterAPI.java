package md.challenge.stack.exchange.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterAPI {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private HttpClient client;

	public FilterAPI(final HttpClient client) {
		super();

		this.client = client;
	}

	public final String createFilter(final String stackSite, List<String> includedFields, List<String> excludedFields,
			String base, Boolean unsafe) {
		final String createFilterUri = ApiURIs.getCreateFilterUri(stackSite, includedFields, excludedFields, base, unsafe);
		Collector<CharSequence, ?, String> collector = Collectors.joining(",");
		logger.debug(
				"Creating filter with included fields = {}; excluded fields = {}; base = {}; unsafe = {} of stackSite = {} via URI = {} ",
				includedFields!=null?includedFields.stream().collect(collector):"", excludedFields!=null?excludedFields.stream().collect(collector):"", base, unsafe,
				stackSite, createFilterUri);
		try {
			return accessCreateFilterUri(createFilterUri);
		} catch (final IOException ioEx) {
			logger.error("", ioEx);
		}

		return null;
	}

	final String accessCreateFilterUri( final String createFilterUri) throws IOException {
		HttpGet request = null;
		HttpEntity httpEntity = null;
		try {
			request = new HttpGet(createFilterUri);
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
