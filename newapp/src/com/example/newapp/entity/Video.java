package com.example.newapp.entity;

import java.io.Serializable;
import java.util.Date;

public class Video implements Serializable{
	private static final long serialVersionUID = 6585270406365348759L;

	private String videoId;

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public int getVideoOrder() {
		return videoOrder;
	}

	public void setVideoOrder(int videoOrder) {
		this.videoOrder = videoOrder;
	}

	public String getJourneyId() {
		return journeyId;
	}

	public void setJourneyId(String journeyId) {
		this.journeyId = journeyId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getVideopath() {
		return videopath;
	}

	public void setVideopath(String videopath) {
		this.videopath = videopath;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	private int videoOrder;
	
	private String journeyId;

	private Date startTime;

	private Date endTime;

	private String videopath;

	private String thumbnailPath;
}
