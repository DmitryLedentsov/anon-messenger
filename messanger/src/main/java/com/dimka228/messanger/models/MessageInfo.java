package com.dimka228.messanger.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

public interface MessageInfo {
    public Integer getSenderId();
    public String getSender();
    public String getMessage();

}
