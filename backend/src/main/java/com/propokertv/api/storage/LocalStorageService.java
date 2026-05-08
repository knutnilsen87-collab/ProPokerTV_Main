package com.propokertv.api.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"local","default"})
public class LocalStorageService implements StorageService {
    @Override
    public String createUploadUrl(String objectKey, String contentType) {
        return "/local-upload/" + objectKey;
    }

    @Override
    public String publicUrl(String objectKey) {
        return "/local-files/" + objectKey;
    }
}
