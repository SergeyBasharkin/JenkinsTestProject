package ru.alta.svd.client.rest.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.DirectoryScanner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.domain.entity.ProxyServerConfig;
import ru.alta.svd.client.core.service.api.ConfigurationService;
import ru.alta.svd.client.rest.dto.ConfigObject;
import ru.alta.svd.client.rest.exception.MultipartNotFoundException;
import ru.alta.svd.client.rest.util.FileHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pechenko
 */
@RestController
@RequestMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ConfigController {

    private final ConfigurationService configurationService;

    @Value("#{'${svd.config.oldConfigFileMasks:}'.split(',')}")
    private List<String> oldConfigMasks;

    public ConfigController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @ApiOperation(value = "Получение общих настроек")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ConfigObject get() {
        ConfigObject configObject = new ConfigObject();
        Configuration configuration = configurationService.loadConfig();
        if (configuration == null) return new ConfigObject();
        BeanUtils.copyProperties(configuration, configObject);

        if (configuration.getSocketTimeout() != null)
            configObject.setSocketTimeout(configuration.getSocketTimeout() / 1000);

        if (configuration.getConnectionTimeout() != null)
            configObject.setConnectionTimeout(configuration.getConnectionTimeout() / 1000);

        if (configuration.getProxy() != null)
            BeanUtils.copyProperties(configuration.getProxy(), configObject.getProxy());
        configObject.setValidationError(Configuration.validate(configuration));
        return configObject;
    }

    @ApiOperation(value = "Сохранение общих настроек")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(@RequestBody ConfigObject configObject) {
        Configuration configuration = new Configuration();
        configuration.setProxy(new ProxyServerConfig());
        BeanUtils.copyProperties(configObject, configuration);

        if (configObject.getSocketTimeout() != null)
            configuration.setSocketTimeout(configObject.getSocketTimeout() * 1000);

        if (configObject.getConnectionTimeout() != null)
            configuration.setConnectionTimeout(configObject.getConnectionTimeout() * 1000);

        BeanUtils.copyProperties(configObject.getProxy(), configuration.getProxy());
        configurationService.saveConfiguration(configuration);
    }

    @ApiOperation(value = "Сохраненик общих настроек в файл")
    @RequestMapping(value = "/file/save", method = RequestMethod.GET)
    public ResponseEntity<Resource> saveToFile(HttpServletRequest request) throws IOException {
        File file = configurationService.loadFile();
        Resource resource = new UrlResource(file.toURI());
        file.deleteOnExit();

         String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @ApiOperation(value = "Сохраниние общих настроек из файла")
    @RequestMapping(value = "/file/load", method = RequestMethod.POST)
    public void saveFromFile(@RequestParam(name = "file", required = false) MultipartFile multipart) throws IOException {
        if (multipart == null) throw new MultipartNotFoundException();
        configurationService.saveFromFile(FileHelper.multipartToFile(multipart));
    }

    @ApiOperation(value = "Поиск старых настроек")
    @RequestMapping(value = "/old/list", method = RequestMethod.GET)
    public List<String> findOldConfig(){
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(oldConfigMasks.toArray(new String[0]));
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        return Arrays.stream(files).map(File::new).filter(this::canParse).map(File::getAbsolutePath).collect(Collectors.toList());
    }

    @ApiOperation(value = "Поиск старых настроек")
    @RequestMapping(value = "/old", method = RequestMethod.GET)
    public Configuration findOldConfig(@RequestParam String path){
        return configurationService.parseConfig(new File(path));
    }

    @ApiOperation(value = "Загрузка настроек по пути")
    @RequestMapping(value = "/old/load", method = RequestMethod.GET)
    public void loadOldConfig(@RequestParam String path) throws IOException {
        configurationService.saveFromFile(new File(path));
    }

    private boolean canParse(File file){
        return configurationService.parseConfig(file) != null;
    }
}
