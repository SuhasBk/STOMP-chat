package com.websocks.websocks.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ReadOnlyFileSystemException;

@Service
public class FileService {

    private final Path root = Paths.get("uploads");
    private Logger logger = LoggerFactory.getLogger(FileService.class);

    public void init() {
        try {
            logger.info("CREATING UPLOAD DIRECTORY...");
            Files.createDirectory(root);
        } catch (Exception e) {
            logger.error("FAILED TO CREATE UPLOAD DIRECTORY!");
            throw new ReadOnlyFileSystemException();
        }
    }

    public void save(MultipartFile file) {
        String filename = file.getOriginalFilename();
        try {
            logger.info("SAVING UPLOADED FILE... {}", filename);
            Files.copy(file.getInputStream(), root.resolve(filename));
        } catch (Exception e) {
            logger.error("FAILED TO SAVE UPLOADED FILE! {}", filename);
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Resource load(String filename) {
        try {
            logger.info("FETCHING FILE : {}", filename);
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                logger.error("UNREADABLE FILE! {}", filename);
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            logger.error("FAILED TO FETCH FILE: {}", filename);
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void deleteAll() {
        logger.info("DELETING ALL UPLOADED FILES...");
        FileSystemUtils.deleteRecursively(root.toFile());
    }

}
