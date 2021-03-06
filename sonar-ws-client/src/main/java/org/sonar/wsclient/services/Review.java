/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.wsclient.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @since 2.8
 */
public class Review extends Model {

  private Long id;
  private Date createdAt = null;
  private Date updatedAt = null;
  private String authorLogin = null;
  private String assigneeLogin = null;
  private String title = null;
  private String type = null;
  private String status = null;
  private String severity = null;
  private String resourceKee = null;
  private Integer line = null;
  private Boolean falsePositive = null;
  private Long violationId;
  private List<Review.Comment> comments = new ArrayList<Review.Comment>();

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public Review setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return the createdAt
   */
  public Date getCreatedAt() {
    return createdAt;
  }

  /**
   * @param createdAt
   *          the createdAt to set
   */
  public Review setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * @return the updatedAt
   */
  public Date getUpdatedAt() {
    return updatedAt;
  }

  /**
   * @param updatedAt
   *          the updatedAt to set
   */
  public Review setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * @return the authorLogin
   */
  public String getAuthorLogin() {
    return authorLogin;
  }

  /**
   * @param s
   *          the authorLogin to set
   */
  public Review setAuthorLogin(String s) {
    this.authorLogin = s;
    return this;
  }

  /**
   * @return the assigneeLogin
   */
  public String getAssigneeLogin() {
    return assigneeLogin;
  }

  /**
   * @param s
   *          the assigneeLogin to set
   */
  public Review setAssigneeLogin(String s) {
    this.assigneeLogin = s;
    return this;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param s
   *          the title to set
   */
  public Review setTitle(String s) {
    this.title = s;
    return this;
  }

  /**
   * @deprecated since 2.9. Use {@link #getFalsePositive()} instead.
   * @return the type
   */
  @Deprecated
  public String getType() {
    return type;
  }

  /**
   * @deprecated since 2.9. Use {@link #setFalsePositive(Boolean)} instead.
   * @param s
   *          the type to set
   */
  @Deprecated
  public Review setType(String s) {
    this.type = s;
    // the following code is only here to ensure backward compatibility with 2.8
    if ("FALSE_POSITIVE".equals(type)) {
      falsePositive = Boolean.TRUE;
    } else if ("VIOLATION".equals(type)) {
      falsePositive = Boolean.FALSE;
    }
    return this;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status
   *          the status to set
   */
  public Review setStatus(String status) {
    this.status = status;
    return this;
  }

  /**
   * @return the severity
   */
  public String getSeverity() {
    return severity;
  }

  /**
   * @param severity
   *          the severity to set
   */
  public Review setSeverity(String severity) {
    this.severity = severity;
    return this;
  }

  /**
   * @return the resourceKee
   */
  public String getResourceKee() {
    return resourceKee;
  }

  /**
   * @param resourceKee
   *          the resourceKee to set
   */
  public Review setResourceKee(String resourceKee) {
    this.resourceKee = resourceKee;
    return this;
  }

  /**
   * @return the line
   */
  public Integer getLine() {
    return line;
  }

  /**
   * @param line
   *          the line to set
   */
  public Review setLine(Integer line) {
    this.line = line;
    return this;
  }

  /**
   * @since 2.9
   * @return the falsePositive
   */
  public Boolean getFalsePositive() {
    return falsePositive;
  }

  /**
   * @since 2.9
   * @param falsePositive
   *          true if false positive
   */
  public Review setFalsePositive(Boolean falsePositive) {
    this.falsePositive = falsePositive;
    return this;
  }

  /**
   * @since 2.9
   * @return the violation id
   */
  public Long getViolationId() {
    return violationId;
  }

  /**
   * @param id
   *          the violation id to set
   */
  public Review setViolationId(Long violationId) {
    this.violationId = violationId;
    return this;
  }

  /**
   * @return the comments
   */
  public List<Review.Comment> getComments() {
    return comments;
  }

  public Review addComments(Long id, Date updatedAt, String authorLogin, String text) {
    this.comments.add(new Review.Comment(id, updatedAt, authorLogin, text));
    return this;
  }

  /**
   * @since 2.8
   */
  public static final class Comment extends Model {

    private Long id = null;
    private String authorLogin = null;
    private Date updatedAt = null;
    private String text = null;

    private Comment(Long id, Date updatedAt, String authorLogin, String text) {
      this.id = id;
      this.updatedAt = updatedAt;
      this.authorLogin = authorLogin;
      this.text = text;
    }

    /**
     * @since 2.9
     * @return the id
     */
    public Long getId() {
      return id;
    }

    /**
     * @return the authorLogin
     */
    public String getAuthorLogin() {
      return authorLogin;
    }

    /**
     * @return the updatedAt
     */
    public Date getUpdatedAt() {
      return updatedAt;
    }

    /**
     * @return the text
     */
    public String getText() {
      return text;
    }
  }

}
