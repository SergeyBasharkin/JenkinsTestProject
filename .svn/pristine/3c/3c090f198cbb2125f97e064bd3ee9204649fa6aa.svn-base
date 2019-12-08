package ru.alta.svd.client.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alta.svd.client.rest.dto.FileSystemNode;
import ru.alta.svd.client.rest.main.SvdClientRestApplication;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {SvdClientRestApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FoldersControllerTest {

    @Value("http://localhost:${local.server.port}/folders")
    private String appPath;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testFolders() {
        FileSystemNode[] fileSystemNodeList = restTemplate.getForObject(appPath, FileSystemNode[].class);
        assertTrue(fileSystemNodeList.length != 0);
    }

   @Test
    public void testFoldersExc() {
       FileSystemNode[] fileSystemNodeList = restTemplate.getForObject(appPath, FileSystemNode[].class);
       assertTrue(fileSystemNodeList.length != 0);

       ResponseEntity responseEntity = restTemplate.getForEntity(appPath + "?path=" + fileSystemNodeList[0].getPathName() + "/" + UUID.randomUUID().toString(), String.class);

       assertEquals(responseEntity.getStatusCodeValue(), 200);
    }

    @Test
    public void testFoldersChild() {
        FileSystemNode[] fileSystemNodeList = restTemplate.getForObject(appPath, FileSystemNode[].class);
        assertTrue(fileSystemNodeList.length != 0);

        FileSystemNode[] child = restTemplate.getForObject(appPath + "?path=" + fileSystemNodeList[0].getPathName(), FileSystemNode[].class);
        assertTrue(child.length != 0);
    }

    @Test
    public void testHome() {
        FileSystemNode[] fileSystemNodeList = restTemplate.getForObject(appPath + "/home", FileSystemNode[].class);
        assertTrue(fileSystemNodeList.length != 0);
    }

    @Test
    public void testProgram() {
        FileSystemNode[] fileSystemNodeList = restTemplate.getForObject(appPath + "/program", FileSystemNode[].class);
        assertTrue(fileSystemNodeList.length != 0);
    }

}