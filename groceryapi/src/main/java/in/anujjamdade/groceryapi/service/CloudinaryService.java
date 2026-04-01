package in.anujjamdade.groceryapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudinaryService {
    List<String> uploadImages(List<MultipartFile> images);
}

