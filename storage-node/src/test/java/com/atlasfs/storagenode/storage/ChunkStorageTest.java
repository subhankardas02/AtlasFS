package com.atlasfs.storagenode.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

class ChunkStorageTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldStoreNewChunkSuccessfully() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        String chunkId = "chunk-001";
        byte[] data = "Hello AtlasFS".getBytes();

        String checksum = storage.putChunk(chunkId, data);

        assertNotNull(checksum);
        assertTrue(Files.exists(tempDir.resolve(chunkId + ".chunk")));
    }

    @Test
    void shouldAcceptSameChunkWithIdenticalContent() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        String chunkId = "chunk-002";
        byte[] data = "Hello AtlasFS".getBytes();

        String firstChecksum = storage.putChunk(chunkId, data);
        String secondChecksum = storage.putChunk(chunkId, data);

        assertEquals(firstChecksum, secondChecksum);
    }

    @Test
    void shouldRejectSameChunkIdWithDifferentContent() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        String chunkId = "chunk-003";

        byte[] firstData = "Hello AtlasFS".getBytes();
        byte[] secondData = "Different content".getBytes();

        storage.putChunk(chunkId, firstData);

        assertThrows(IOException.class, () -> storage.putChunk(chunkId, secondData));
    }

    @Test
    void shouldStoreExactChunkData() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);
        String chunkId = "chunk-004";
        byte[] originalData = "Hello AtlasFS".getBytes();
        storage.putChunk(chunkId, originalData);
        Path chunkPath = tempDir.resolve(chunkId + ".chunk");
        byte[] storedData = Files.readAllBytes(chunkPath);
        assertArrayEquals(originalData, storedData);
    }

    @Test
    void shouldRetrieveStoredChunkSuccessfully() throws IOException{
        ChunkStorage storage=new ChunkStorage(tempDir);
        String chunkId="chunk-005";
        byte[] originalData="Hello AtlasFS".getBytes();
        storage.putChunk(chunkId, originalData);
        byte[] retrievedData=storage.getChunk(chunkId);
        assertArrayEquals(originalData, retrievedData);

    }


}