package ru.alta.svd.protocol.test.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pechenko
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OperationResult")
public class OperationResult {
    @XmlElement(name = "state", required = true)
    private Integer state;
    
    @XmlElement(name = "message", required = true)
    private String message;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
