package com.propokertv.api.storage;

public interface StorageService {
    String createUploadUrl(String objectKey, String contentType);
    String publicUrl(String objectKey);
}
