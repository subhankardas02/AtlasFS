package com.atlasfs.storagenode.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkStorage {

    private Path storageDirectory;
    private final ConcurrentHashMap<String, Object> chunkLocks = new ConcurrentHashMap<>();

    public ChunkStorage(Path storageDirectory) throws IOException {
        this.storageDirectory = storageDirectory;
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


    public byte[] getChunk(String chunkId) throws IOException{
        Path chunkPath=storageDirectory.resolve(chunkId + ".chunk");
        return Files.readAllBytes(chunkPath);

    }

    public String putChunk(String chunkId, byte[] data) throws IOException {
        String checkSum = calculateChecksum(data);
        Object lock = chunkLocks.computeIfAbsent(chunkId, key -> new Object());
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
            Path tempPath = storageDirectory.resolve(chunkId  + "-" + UUID.randomUUID() + ".tmp");
            try{
                Files.write(tempPath, data);        
                Files.move(tempPath, chunkPath, StandardCopyOption.ATOMIC_MOVE);
            }
            finally{
                Files.deleteIfExists(tempPath);
            }

        }

        return checkSum;
    }

    public boolean deleteChunk(String chunkId) throws IOException{
        Path chunkPath=storageDirectory.resolve(chunkId + ".chunk");
        return Files.deleteIfExists(chunkPath);
    }

}
