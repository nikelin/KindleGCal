package com.redshape.kindle.gcal.data;

import java.io.Serializable;
import java.util.Date;

public class Entry implements Serializable {
	private String summary;
	private String id;
	private String kind;
	private String etag;
	private Date created;
	private Date updated;
	private String title;
	private String eventFeedLink;
	private String accessControlListLinl;
	private boolean canEdit;
	private Author author;
	private AccessLevel level;
	private String color;
	private boolean hidden;
	private boolean selected;
	private String timezone;
	private String location;
	private int timesCleaned;
	private Date startDate;
	private Date endDate;

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEventFeedLink() {
		return eventFeedLink;
	}

	public void setEventFeedLink(String eventFeedLink) {
		this.eventFeedLink = eventFeedLink;
	}

	public String getAccessControlListLinl() {
		return accessControlListLinl;
	}

	public void setAccessControlListLinl(String accessControlListLinl) {
		this.accessControlListLinl = accessControlListLinl;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public AccessLevel getLevel() {
		return level;
	}

	public void setLevel(AccessLevel level) {
		this.level = level;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getTimesCleaned() {
		return timesCleaned;
	}

	public void setTimesCleaned(int timesCleaned) {
		this.timesCleaned = timesCleaned;
	}
}
