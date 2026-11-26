package com.atlasfs.storagenode.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.UUID;

public class ChunkStorage {
    private static final int MAX_LOCK_COUNT = 1000;
    private final Object[] locks;
    private final Path storageDirectory;

    private Object getLock(String chunkId) {
        return locks[Math.floorMod(chunkId.hashCode(), MAX_LOCK_COUNT)];
    }

    public ChunkStorage(Path storageDirectory) throws IOException {
        this.storageDirectory = storageDirectory;
        this.locks = new Object[MAX_LOCK_COUNT];
        for (int i = 0; i < MAX_LOCK_COUNT; i++) {
            locks[i] = new Object();
        }
        Files.createDirectories(storageDirectory);

        if (!Files.isDirectory(storageDirectory)) {
            throw new IOException("Storage path is not a directory: " + storageDirectory);
        }
    }

    private String calculateChecksum(byte[] data) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return java.util.HexFormat.of().formatHex(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 algorithm not available", e);

        }

    }

    private void validateChunkId(String chunkId) {
        if (chunkId == null || !chunkId.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Invalid chunk ID: " + chunkId);
        }
    }

    public byte[] getChunk(String chunkId) throws IOException {
        validateChunkId(chunkId);
        Object lock = getLock(chunkId);
        synchronized (lock) {
            Path chunkPath = storageDirectory.resolve(chunkId + ".chunk");
            Path checksumPath = storageDirectory.resolve(chunkId + ".checksum");

            if (!Files.exists(chunkPath)) {
                throw new IOException("Chunk not found: " + chunkId);
            }
            if (!Files.exists(checksumPath)) {
                throw new IOException("Checksum file missing: " + chunkId);
            }

            byte[] data = Files.readAllBytes(chunkPath);
            String storedChecksum = Files.readString(checksumPath);
            String actualChecksum = calculateChecksum(data);

            if (!storedChecksum.equals(actualChecksum)) {
                throw new IOException("Chunk checksum verification failed: " + chunkId);
            }

            return data;
        }

    }

    public String putChunk(String chunkId, byte[] data) throws IOException {
        validateChunkId(chunkId);
        if (data == null) {
            throw new IllegalArgumentException("Chunk data cannot be null");
        }
        String checkSum = calculateChecksum(data);
        Object lock = getLock(chunkId);
        Path chunkPath = storageDirectory.resolve(chunkId + ".chunk");

        synchronized (lock) {

            if (Files.exists(chunkPath)) {
                byte[] existingData = Files.readAllBytes(chunkPath);
                String existingChecksum = calculateChecksum(existingData);
                if (checkSum.equals(existingChecksum))
                    return checkSum;
                else {
                    throw new IOException("Chunk ID already exists with different content.");
                }
            }
            Path tempPath = storageDirectory.resolve(chunkId + "-" + UUID.randomUUID() + ".tmp");
            try {
                Files.write(tempPath, data);
                Files.move(tempPath, chunkPath, StandardCopyOption.ATOMIC_MOVE);
                Path checksumPath = storageDirectory.resolve(chunkId + ".checksum");
                Files.writeString(checksumPath, checkSum);

            } finally {
                Files.deleteIfExists(tempPath);
            }

        }

        return checkSum;
    }

    public boolean deleteChunk(String chunkId) throws IOException {
        validateChunkId(chunkId);
        Object lock = getLock(chunkId);
        synchronized (lock) {
            Path chunkPath = storageDirectory.resolve(chunkId + ".chunk");
            return Files.deleteIfExists(chunkPath);
        }
    }

    public boolean exists(String chunkId) throws IOException {
        validateChunkId(chunkId);
        Object lock = getLock(chunkId);
        synchronized (lock) {
            Path chunkPath = storageDirectory.resolve(chunkId + ".chunk");
            return Files.exists(chunkPath);
        }

    }

}
