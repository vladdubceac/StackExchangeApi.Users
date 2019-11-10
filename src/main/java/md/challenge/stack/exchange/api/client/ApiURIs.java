package md.challenge.stack.exchange.api.client;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import md.challenge.stack.exchange.api.constants.Constants.UriParameters;

public class ApiURIs {

	public static String getSortedUsersUri(String uri, String site, int pageSize, int page, String sortBy, int min,
			String order, String filter) {
		return createSortedUsersUri(uri,site, pageSize, page, sortBy, min, order, filter);
	}

	public static String getSortedUsersTagsUri(String uri, String stackSite, List<Integer> userIDs, int pageSize, int page,
			String sortBy, String order, String filter) {
		String ids = userIDs.stream().map(String::valueOf).collect(Collectors.joining(";"));
		uri = uri.replace("{ids}", ids);
		return createSortedUsersUri( uri ,stackSite, pageSize, page, sortBy, 0, order, filter);
	}

	public static String getCreateFilterUri(String site, List<String> include, List<String> exclude, String base,
			Boolean unsafe) {
		return generateCreateFilterUri(site,  include, exclude, base, unsafe);
	}

	// https://api.stackexchange.com/2.2/filters/create/
	static String generateCreateFilterUri(String site, List<String> include, List<String> exclude, String base,
			Boolean unsafe) {
		RequestBuilder requestBuilder = new RequestBuilder();
		if (include != null && !include.isEmpty()) {
			String includeParam = include.stream().collect(Collectors.joining(";"));
			requestBuilder = requestBuilder.add("include", includeParam);
		}
		if (exclude != null && !exclude.isEmpty()) {
			String excludeParam = exclude.stream().collect(Collectors.joining(";"));
			requestBuilder = requestBuilder.add("exclude", excludeParam);
		}
		if (StringUtils.isNotBlank(base)) {
			requestBuilder = requestBuilder.add("base", base);
		}
		if (unsafe != null) {
			requestBuilder = requestBuilder.add("unsafe", unsafe.booleanValue());
		} else {
			requestBuilder = requestBuilder.add("unsafe", "false");
		}
		String parameters = requestBuilder.build();
		return site + parameters;
	}

	// util
	static String createSortedUsersUri(String uri, String site, int pageSize, int page, String sortBy, int min,
			String order, String filter) {
		RequestBuilder requestBuilder = new RequestBuilder();
		requestBuilder = requestBuilder.add(UriParameters.ORDER, order == null || order.isEmpty() ? "asc" : order)
				.add(UriParameters.SORT, sortBy).add(UriParameters.MIN, min).add(UriParameters.SITE, site);
		if (StringUtils.isNotBlank(filter)) {
			requestBuilder = requestBuilder.add(UriParameters.FILTER, filter);
		}
		if (pageSize > 0) {
			requestBuilder = requestBuilder.add(UriParameters.PAGESIZE, pageSize);
		}
		if (page > 0) {
			requestBuilder.add(UriParameters.PAGE, page);
		}

		final String params = requestBuilder.build();
		return uri + params;
	}

}
