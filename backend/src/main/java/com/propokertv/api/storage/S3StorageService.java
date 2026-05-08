package com.propokertv.api.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("s3")
public class S3StorageService implements StorageService {
    @Override
    public String createUploadUrl(String objectKey, String contentType) {
        return "TODO_SIGNED_S3_UPLOAD_URL/" + objectKey;
    }

    @Override
    public String publicUrl(String objectKey) {
        return "TODO_S3_PUBLIC_URL/" + objectKey;
    }
}
