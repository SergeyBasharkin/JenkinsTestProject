//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.24 at 10:54:10 AM MSK 
//


package ru.alta.svd.client.core.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "pay",
        "days",
        "payRub",
        "operationResult",
        "file"
})
@XmlRootElement(name = "XMLFile")
public class XMLFile {

    @XmlElement(name = "Pay")
    protected String pay;
    protected String days;
    protected String payRub;

    @XmlElement(name = "OperationResult", required = true)
    protected OperationResult operationResult;

    @JacksonXmlElementWrapper(useWrapping=false)
    @XmlElement(name = "File")
    protected List<File> file;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "state",
            "message"
    })
    public static class OperationResult {
        @XmlElement(required = true)
        protected Integer state;
        protected String message;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "id",
            "login",
            "statId",
            "content"
    })
    public static class File {
        @XmlElement(required = true)
        protected String id;
        protected String login;
        @XmlElement(required = true)
        protected String statId;
        @XmlElement(required = true)
        protected byte[] content;

    }
}