package md.challenge.stack.exchange.api.constants;

public interface ApiObjectConstants {
	interface UserObject {
		String OBJECT_NAME = "user";

		String ABOUT_ME = "about_me";
		String ACCEPT_RATE = "accept_rate";
		String ACCOUNT_ID = "account_id";
		String AGE = "age";
		String ANSWER_COUNT = "answer_count";
		String BADGE_COUNTS = "badge_counts";
		String CREATION_DATE = "creation_date";
		String DISPLAY_NAME = "display_name";
		String DOWN_VOTE_COUNT = "down_vote_count";
		String IS_EMPLOYEE = "is_employee";
		String LAST_ACCESS_DATE = "last_access_date";
		String LAST_MODIFIED_DATE = "last_modified_date";
		String LINK = "link";
		String LOCATION = "location";
		String PROFILE_IMAGE = "profile_image";
		String QUESTION_COUNT = "question_count";
		String REPUTATION = "reputation";
		String REPUTATION_CHANGE_DAY = "reputation_change_day";
		String REPUTATION_CHANGE_MONTH = "reputation_change_month";
		String REPUTATION_CHANGE_QUARTER = "reputation_change_quarter";
		String REPUTATION_CHANGE_WEEK = "reputation_change_week";
		String REPUTATION_CHANGE_YEAR = "reputation_change_year";
		String TIMED_PENALTY_DATE = "timed_penalty_date";
		String UP_VOTE_COUNT = "up_vote_count";
		String USER_ID = "user_id";
		String USER_TYPE = "user_type";
		String VIEW_COUNT = "view_count";
		String WEBSITE_URL = "website_url";
	}
	
	interface Tags{
		String OBJECT_NAME="tag";
		
		String USER_ID = "user_id";
		String HAS_SYNONYMS = "has_synonyms";
		String NAME = "name";
		String IS_MODERATOR_ONLY = "is_moderator_only";
		String IS_REQUIRED = "is_required";
		String COUNT = "count";
		String LAST_ACTIVITY_DATE = "last_activity_date";
		String SYNONYMS = "synonyms";
		
	}
}
