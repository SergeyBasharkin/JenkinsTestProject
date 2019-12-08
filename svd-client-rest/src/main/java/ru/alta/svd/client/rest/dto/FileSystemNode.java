package ru.alta.svd.client.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class FileSystemNode {
    private String pathName;
    private boolean isRoot;

    @ApiModelProperty(example = "{\n" +
            "    \"dir\": true,\n" +
            "    \"child\": []," +
            "    \"pathName\": \"string\",\n" +
            "    \"root\": false\n" +
            "  }")
    private List<FileSystemNode> child = new LinkedList<>();
}
