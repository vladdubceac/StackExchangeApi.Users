package md.challenge.stack.exchange.api.main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import md.challenge.stack.exchange.api.client.FilterAPI;
import md.challenge.stack.exchange.api.client.UsersAPI;
import md.challenge.stack.exchange.api.constants.PropertiesConstants;
import md.challenge.stack.exchange.api.model.User;
import md.challenge.stack.exchange.api.utils.PropertiesUtils;

import static md.challenge.stack.exchange.api.constants.Constants.*;
import static md.challenge.stack.exchange.api.constants.ApiObjectConstants.*;

public class Main {

	private static final String APP_PROPERTIES = "app.properties";
	private static final String ASCENDING_ORDER = "asc";

	public static void main(String[] args) throws IOException {

		Properties properties = PropertiesUtils.loadProperties(APP_PROPERTIES);
		String sortUsersBy = properties.getProperty(PropertiesConstants.SORT_USERS_BY);
		String sortUserTagsBy = properties.getProperty(PropertiesConstants.SORT_USER_TAGS_BY);
		int minScore = Integer.parseInt(properties.getProperty(PropertiesConstants.MIN));
		int pageSize = Integer.parseInt(properties.getProperty(PropertiesConstants.PAGE_SIZE));
		String siteParam = properties.getProperty(PropertiesConstants.SITE);
		String createFilterUri = properties.getProperty(PropertiesConstants.CREATE_FILTER_URI);
		String usersUri = properties.getProperty(PropertiesConstants.USERS_URI);
		String userTagsUri = properties.getProperty(PropertiesConstants.USER_TAGS_URI);
		List<String> tagsAccepted = Arrays.asList(properties.getProperty(PropertiesConstants.TAGS).split(","));
		List<String> locations = Arrays.asList(properties.getProperty(PropertiesConstants.LOCATIONS).split(","));
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		UsersAPI usersAPI = new UsersAPI(httpClient);
		FilterAPI filterAPI = new FilterAPI(httpClient);

		List<String> includeList = createCustomList(UserObject.OBJECT_NAME + "." + UserObject.ANSWER_COUNT,
				UserObject.OBJECT_NAME + "." + UserObject.QUESTION_COUNT, UserObject.OBJECT_NAME + "." + UserObject.LOCATION);

		List<String> excludeList = createCustomList(UserObject.OBJECT_NAME + "." + UserObject.IS_EMPLOYEE,
				UserObject.OBJECT_NAME + "." + UserObject.LAST_MODIFIED_DATE,
				UserObject.OBJECT_NAME + "." + UserObject.LAST_ACCESS_DATE,
				UserObject.OBJECT_NAME + "." + UserObject.REPUTATION_CHANGE_YEAR,
				UserObject.OBJECT_NAME + "." + UserObject.REPUTATION_CHANGE_QUARTER,
				UserObject.OBJECT_NAME + "." + UserObject.REPUTATION_CHANGE_MONTH,
				UserObject.OBJECT_NAME + "." + UserObject.REPUTATION_CHANGE_WEEK,
				UserObject.OBJECT_NAME + "." + UserObject.REPUTATION_CHANGE_DAY,
				UserObject.OBJECT_NAME + "." + UserObject.CREATION_DATE, UserObject.OBJECT_NAME + "." + UserObject.WEBSITE_URL,
				UserObject.OBJECT_NAME + "." + UserObject.BADGE_COUNTS);

		String order = ASCENDING_ORDER;
		int pageNumber = 1;

		String filterJson = filterAPI.createFilter(createFilterUri, includeList, excludeList, null, null);
		String filterId = getFilterNumber(filterJson);
		
		String usersJSON = usersAPI.getSortedUsers(usersUri, siteParam, pageSize, pageNumber, sortUsersBy, minScore,
				order, filterId);

		List<User> users = parseUsersJSON(usersJSON);

		List<String> tagsFilterIncludeList = createCustomList(Tags.OBJECT_NAME + "." + Tags.USER_ID,
				Tags.OBJECT_NAME + "." + Tags.NAME);

		List<String> tagsFilterExcludeList = createCustomList(Tags.OBJECT_NAME + "." + Tags.COUNT,
				Tags.OBJECT_NAME + "." + Tags.HAS_SYNONYMS, Tags.OBJECT_NAME + "." + Tags.IS_MODERATOR_ONLY,
				Tags.OBJECT_NAME + "." + Tags.IS_REQUIRED, Tags.OBJECT_NAME + "." + Tags.LAST_ACTIVITY_DATE,
				Tags.OBJECT_NAME + "." + Tags.SYNONYMS);

		String tagsFilterJSON = filterAPI.createFilter(createFilterUri, tagsFilterIncludeList, tagsFilterExcludeList,
				null, null);
		String tagsFilterId = getFilterNumber(tagsFilterJSON);

		List<Integer> ids = getUserIDs(users);

		Map<Integer, Set<String>> userTagsMap = new HashMap<>();

		while (!ids.isEmpty()) {
			String userTagsJSON = usersAPI.getSortedUsersTags(userTagsUri, siteParam, ids, pageSize, 1, sortUserTagsBy,
					order, tagsFilterId);
			userTagsMap.putAll(parseUsersTagsJSON(userTagsJSON, tagsAccepted));
			ids.removeAll(userTagsMap.keySet());
		}

		users.stream().forEach(user -> {
			Set<String> tags = userTagsMap.get(user.getUserId());
			if (tags.contains(null)) {
				tags.remove(null);
			}
			if (!tags.isEmpty()) {
				user.setTags(tags);
				user.setTagsCsv(tags.stream().collect(Collectors.joining(",")));
			}
		});


		Predicate<User> filterByTags = (user -> user.getTags() != null && !user.getTags().isEmpty());
		Predicate<User> filterByAnswerCount = user -> user.getAnswerCount() > 0;
		Predicate<User> filterByLocation = user -> {
			String location = user.getLocation();
			return StringUtils.isNotBlank(location) && locations.contains(location.trim());
		};

		Predicate<User> filterPredicate = filterByTags.and(filterByAnswerCount).and(filterByLocation);

		users = getFilteredList(users, filterPredicate);
		
		System.out.println("User name ; Location ; Answer count ; Question count ; Link to profile ; Link to avatar");

		users.forEach(u -> System.out.println(u.getUsername() + " ; "
				+ u.getLocation() + " ; " + u.getAnswerCount() + " ; "
				+ u.getQuestionCount() + " ; " + u.getTagsCsv() + " ; "
				+ u.getLinkToProfile() + " ; " + u.getLinkToAvatar()));

		

	}

	private static List<User> getFilteredList(List<User> users, Predicate<User> predicate) {
		return users.stream().filter(predicate).collect(Collectors.toList());
	}

	private static List<Integer> getUserIDs(List<User> users) {
		return users.stream().map(user -> user.getUserId()).collect(Collectors.toList());
	}

	private static List<String> createCustomList(String... fields) {
		List<String> includeList = new ArrayList<String>();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				includeList.add(fields[i]);
			}
		}
		return includeList;
	}

	@SuppressWarnings({ "unchecked" })
	private static List<User> parseUsersJSON(String usersJSON)
			throws JsonParseException, JsonMappingException, IOException {
		List<User> users = new ArrayList<User>();
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> usersJSONMap = objectMapper.readValue(usersJSON, Map.class);
		List<Object> items = (List<Object>) usersJSONMap.get(JsonParameters.ITEMS);
		Function<Object, User> function = obj -> {
			Map<String, Object> map = (Map<String, Object>) obj;
			if (map.containsKey(JsonParameters.LOCATION)) {
				int accountId = (int) map.get(JsonParameters.ACCOUNT_ID);
				if (accountId > -1) {
					User user = new User();

					int userId = (int) map.get(JsonParameters.USER_ID);
					user.setUserId(userId);

					user.setAccountId(accountId);

					String username = (String) map.get(JsonParameters.DISPLAY_NAME);
					user.setUsername(username);

					int answerCount = (int) map.get(JsonParameters.ANSWER_COUNT);
					user.setAnswerCount(answerCount);

					int questionCount = (int) map.get(JsonParameters.QUESTION_COUNT);
					user.setQuestionCount(questionCount);

					String location = (String) map.get(JsonParameters.LOCATION);
					user.setLocation(location);

					int reputation = (int) map.get(JsonParameters.REPUTATION);
					user.setReputation(reputation);

					String linkToProfile = (String) map.get(JsonParameters.LINK);
					user.setLinkToProfile(linkToProfile);

					String linkToAvatar = (String) map.get(JsonParameters.PROFILE_IMAGE);
					user.setLinkToAvatar(linkToAvatar);

					return user;
				}
			}
			return null;
		};
		users = items.stream().map(function).filter(u -> u != null).collect(Collectors.toList());
		return users;
	}

	private static Map<Integer, Set<String>> parseUsersTagsJSON(String userTagsJSON, List<String> tagsAccepted)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> usersJSONMap = objectMapper.readValue(userTagsJSON, Map.class);
		List<Object> items = (List<Object>) usersJSONMap.get(JsonParameters.ITEMS);
		Function<Object, Map.Entry<Integer, String>> function = obj -> {
			Map<String, Object> map = (Map<String, Object>) obj;

			int userId = (int) map.get(JsonParameters.USER_ID);
			String tagName = (String) map.get(JsonParameters.NAME);
			if (!tagsAccepted.contains(tagName.toLowerCase()) && !tagsAccepted.contains(tagName.toUpperCase())) {
				tagName = null;
			}
			AbstractMap.SimpleImmutableEntry<Integer, String> entry = new AbstractMap.SimpleImmutableEntry<>(userId,
					tagName);

			return entry;
		};

		List<Entry<Integer, String>> list = items.stream().map(function).collect(Collectors.toList());
		Map<Integer, Set<String>> map = list.stream().collect(
				Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
		return map;
	}

	private static String getFilterNumber(String filterJson) throws JsonParseException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		Map<String, Object> map = objMapper.readValue(filterJson, Map.class);
		Object items = map.get(JsonParameters.ITEMS);
		String filterNumber = null;
		if (items != null) {
			for (Object item : ((List) items)) {
				Map m = (Map) item;
				if (m.containsKey(JsonParameters.FILTER)) {
					filterNumber = (String) m.get(JsonParameters.FILTER);
					break;
				}
			}
		}

		return filterNumber;
	}
}
