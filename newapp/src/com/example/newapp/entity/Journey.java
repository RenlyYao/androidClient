package com.example.newapp.entity;

import java.io.Serializable;
import java.util.Date;

public class Journey implements Serializable{
	
	private static final long serialVersionUID = -5728495402364851298L;

	private String journeyId;

	private String journeyDes;

	private Date startTime;

	private Date endTime;

	public String getJourneyId() {
		return journeyId;
	}

	public void setJourneyId(String journeyId) {
		this.journeyId = journeyId;
	}

	public String getJourneyDes() {
		return journeyDes;
	}

	public void setJourneyDes(String journeyDes) {
		this.journeyDes = journeyDes;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isShareable() {
		return shareable;
	}

	public void setShareable(boolean shareable) {
		this.shareable = shareable;
	}

	private String userId;
	
	private boolean shareable;
	
}
