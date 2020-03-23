package person.winteryun.fileapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileService {

    @Value(value = "${file.upload.dir}")
    private String defaultPath;


    public String storeFile(String path, MultipartFile file) throws Exception {
        if (StringUtils.isEmpty(path)) {
            path = defaultPath;
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            Path fileLoction = Paths.get(path).toAbsolutePath().normalize().resolve(fileName);

            Files.copy(file.getInputStream(), fileLoction, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            log.error("存储文件异常," + fileName, e);
            throw e;
        }

        return fileName;
    }

    public Resource loadFile(String path, String fileName) {
        if (StringUtils.isEmpty(path)) {
            path = defaultPath;
        }

        try {
            Path filePath = Paths.get(path).toAbsolutePath().normalize().resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            log.error("读取文件异常," + fileName, e);
        }
        return null;

    }

    public List<String> listFiles(String path, int offset, int limit) {
        if (StringUtils.isEmpty(path)) {
            path = defaultPath;
        }

        try {
            File file = new File(path);
            // get the folder list
            File[] array = file.listFiles();

            if (offset > array.length) {
                return null;
            }

            List<String> fileList = new ArrayList<>();
            for (int i = offset; i <= array.length || i < limit; i++) {
                fileList.add(array[i].getName());
            }

            return fileList;
        } catch (Exception e) {
            log.error("读取文件列表异常", e);
        }
        return null;
    }
}
