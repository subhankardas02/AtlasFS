package com.atlasfs.storagenode.model;

import java.time.Instant;

public class ObjectMetadata {
    // @Id
    // @GeneratedValue
    private String objectId;
    private String fileName;
    private String contentType;
    private long size;
    private String checksum;
    private int totalChunks;
    private Instant createdAt;

    public ObjectMetadata() {
    }

    public ObjectMetadata(String objectId, String fileName,
            String contentType, long size,
            String checksum, int totalChunks,
            Instant createdAt) {

        this.objectId = objectId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.checksum = checksum;
        this.totalChunks = totalChunks;
        this.createdAt = createdAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
