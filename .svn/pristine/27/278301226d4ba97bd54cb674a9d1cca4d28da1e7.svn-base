package ru.alta.svd.client.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alta.svd.client.rest.dto.FileSystemNode;
import ru.alta.svd.client.rest.exception.FileConfigException;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/folders")
public class FoldersController {

    @GetMapping
    public List<FileSystemNode> rootFolders(String path){
        LinkedList<FileSystemNode> folders = new LinkedList<>();
        if (path == null || path.isEmpty()){
            FileSystems.getDefault().getRootDirectories().forEach(root -> {
                FileSystemNode node = new FileSystemNode();
                node.setRoot(true);
                node.setPathName(root.toString());
                node.setChild(findChildDirs(root));
                folders.add(node);
            });
            return folders;
        }else {
            return findChildDirs(Paths.get(path));
        }
    }

    @GetMapping("/home")
    public List<FileSystemNode> homeFolder(){
        return findChildDirs(Paths.get(System.getProperty("user.home")));
    }

    @GetMapping("/program")
    public List<FileSystemNode> programmFolder(){
        return findChildDirs(Paths.get("."));
    }

    private List<FileSystemNode> findChildDirs(Path root){
        List<FileSystemNode> list = new LinkedList<>();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            stream.forEach(path -> {
                if (Files.isDirectory(path)) {
                    FileSystemNode child = new FileSystemNode();
                    child.setRoot(false);
                    child.setPathName(path.toString());
                    list.add(child);
                }
            });
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return list;
    }
}
