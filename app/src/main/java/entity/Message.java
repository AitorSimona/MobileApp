/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Date;


/**
 *
 * @author upcnet
 */
public class Message {
  private Integer id;
  private String content;
  private Date date;
  private UserInfo userReceiver;
  private UserInfo userSender;

  public Message() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public UserInfo getUserReceiver() {
    return userReceiver;
  }

  public void setUserReceiver(UserInfo userReceiver) {
    this.userReceiver = userReceiver;
  }

  public UserInfo getUserSender() {
    return userSender;
  }

  public void setUserSender(UserInfo userSender) {
    this.userSender = userSender;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Message)) {
      return false;
    }
    Message other = (Message) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "entity.Message[ id=" + id + " ]";
  }
  
}
