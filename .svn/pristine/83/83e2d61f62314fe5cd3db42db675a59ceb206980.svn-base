package ru.alta.svd.client.core.dao.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.domain.entity.types.AuthType;
import ru.alta.svd.client.core.main.SvdClientCoreApplication;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {SvdClientCoreApplication.class})
public class FileConfigurationRepositoryTest {

    @Autowired
    private FileConfigurationRepository fileConfigurationRepository;

    private String oldConfig_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "    <url>https://svd.alta.ru</url>\n" +
            "    <serverUsername>serveralta</serverUsername>\n" +
            "    <connectionTimeout>10000</connectionTimeout>\n" +
            "    <socketTimeout>10000</socketTimeout>\n" +
            "    <encryptionType>RSA</encryptionType>\n" +
            "    <proxy>\n" +
            "        <active>false</active>\n" +
            "        <host>host</host>\n" +
            "        <port>0</port>\n" +
            "        <user>user</user>\n" +
            "        <password>RACUj0zwSyQ=</password>\n" +
            "        <authScheme>BASIC</authScheme>\n" +
            "    </proxy>\n" +
            "    <accounts>\n" +
            "        <account>\n" +
            "            <login>altaoffice</login>\n" +
            "            <password>0jCj4cegrvTYAWPYGcGEiAALzMr9gLbG</password>\n" +
            "            <serverId>59d80d13-eec7-4e59-bde8-2db9387a91c0</serverId>\n" +
            "            <sendDir>C:\\Alta\\ED\\OUT\\altaoffice</sendDir>\n" +
            "            <receiveDir>C:\\Alta\\ED\\IN\\altaoffice</receiveDir>\n" +
            "            <requestInteval>10000</requestInteval>\n" +
            "        </account>\n" +
            "        <account>\n" +
            "            <login>sirukov</login>\n" +
            "            <password>1nBHMfefSkImMyXcviBd8Q==</password>\n" +
            "            <serverId>59d80d13-eec7-4e59-bde8-2db9387a91c0</serverId>\n" +
            "            <sendDir>C:\\Alta\\ED\\OUT\\sirukov</sendDir>\n" +
            "            <receiveDir>C:\\Alta\\ED\\IN\\altaoffice</receiveDir>\n" +
            "            <requestInteval>10000</requestInteval>\n" +
            "            <msgType>ED</msgType>\n" +
            "        </account>\n" +
            "    </accounts>\n" +
            "</configuration>";

    private String oldConfig_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "    <encryptionEnabled>true</encryptionEnabled>\n" +
            "    <url>https://svd.alta.ru</url>\n" +
            "    <serverUsername>serveralta</serverUsername>\n" +
            "    <connectionTimeout>10000</connectionTimeout>\n" +
            "    <socketTimeout>10000</socketTimeout>\n" +
            "    <proxy>\n" +
            "        <active>false</active>\n" +
            "        <host></host>\n" +
            "        <port>0</port>\n" +
            "        <user></user>\n" +
            "        <password>RACUj0zwSyQ=</password>\n" +
            "        <authScheme>Basic</authScheme>\n" +
            "    </proxy>\n" +
            "    <accounts>\n" +
            "        <account>\n" +
            "            <login>pechenko-02</login>\n" +
            "            <password>123</password>\n" +
            "            <serverId></serverId>\n" +
            "            <sendDir>/home/pechenko/outbox</sendDir>\n" +
            "            <receiveDir>/home/pechenko/inbox</receiveDir>\n" +
            "            <receiveSleep>10000</receiveSleep>\n" +
            "        </account>\n" +
            "    </accounts>\n" +
            "</configuration>\n";

    private String config;

    @Test
    public void testOldConfig_1() throws IOException {
        FileConfigurationRepository.OldConfiguration oldConfiguration = new XmlMapper().readValue(oldConfig_1, FileConfigurationRepository.OldConfiguration.class);
        Configuration configuration = fileConfigurationRepository.convert(oldConfiguration);

        assertEquals(configuration.getProxy().getAuthType(), AuthType.Basic);
    }

    @Test
    public void testOldConfig_2() throws IOException {
        FileConfigurationRepository.OldConfiguration_2 oldConfiguration = new XmlMapper().readValue(oldConfig_2, FileConfigurationRepository.OldConfiguration_2.class);
        Configuration configuration = fileConfigurationRepository.convert(oldConfiguration);

        assertEquals(configuration.getProxy().getAuthType(), AuthType.Basic);
    }

}