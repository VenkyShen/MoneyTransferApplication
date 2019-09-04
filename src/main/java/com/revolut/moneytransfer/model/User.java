package com.revolut.moneytransfer.model;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author venky
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	private Long id;

	private String name;

	private String mail;

	private Date createdAt;

	private Date updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public User() {
	}

	public User(Long id, String name, String mail, Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.mail = mail;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", mail=" + mail + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}

}
