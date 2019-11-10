package md.challenge.stack.exchange.api.model;

import java.util.Set;

public class User {
	private int userId;
	private int accountId;
	private String username;
	private String location;
	private int answerCount;
	private int questionCount;
	private int reputation;
	private Set<String> tags;
	private String tagsCsv;
	private String linkToProfile;
	private String linkToAvatar;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public long getAnswerCount() {
		return answerCount;
	}
	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}
	public long getQuestionCount() {
		return questionCount;
	}
	public void setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
	}
	public long getReputation() {
		return reputation;
	}
	public void setReputation(int reputation) {
		this.reputation = reputation;
	}
	public Set<String> getTags() {
		return tags;
	}
	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	public String getLinkToProfile() {
		return linkToProfile;
	}
	public void setLinkToProfile(String linkToProfile) {
		this.linkToProfile = linkToProfile;
	}
	public String getLinkToAvatar() {
		return linkToAvatar;
	}
	public void setLinkToAvatar(String linkToAvatar) {
		this.linkToAvatar = linkToAvatar;
	}
	public String getTagsCsv() {
		return tagsCsv;
	}
	public void setTagsCsv(String tagsCsv) {
		this.tagsCsv = tagsCsv;
	}
	
}
