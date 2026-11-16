package com.decoupledx.common.web;

import com.decoupledx.common.error.ErrorCode;

/**
 * Upload validation codes shared by the applications; the bundles own the
 * human copy via the standard {@code error.<code>} keys.
 */
public enum UploadErrorCode implements ErrorCode {

    INVALID_UPLOAD_IMAGE_TYPE(415, "Only JPEG, PNG or WebP images may be uploaded"),
    UPLOAD_IMAGE_TOO_LARGE(413, "Images may be at most 5 MB"),
    UPLOAD_STORE_FAILED(500, "The upload could not be stored");

    private final int httpStatus;
    private final String defaultMessage;

    UploadErrorCode(int httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }
}
