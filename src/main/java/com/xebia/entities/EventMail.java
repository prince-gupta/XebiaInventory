package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;


/**
 * The persistent class for the event_mails database table.
 * 
 */
@Entity
@Table(name="event_mails")
@NamedQuery(name="EventMail.findAll", query="SELECT e FROM EventMail e")
public class EventMail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	private String event;

	@Column(name="ref_id")
	private BigInteger refId;

	private String type;

	private BigInteger user;

    private String status;

    @Temporal(TemporalType.DATE)
    @Column(name="event_date")
    private Date eventDate;

	public EventMail() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getEvent() {
		return this.event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public BigInteger getRefId() {
		return this.refId;
	}

	public void setRefId(BigInteger refId) {
		this.refId = refId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigInteger getUser() {
		return this.user;
	}

	public void setUser(BigInteger user) {
		this.user = user;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
}