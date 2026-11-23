package com.atlasfs.storagenode.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

class ChunkStorageTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldStoreNewChunkSuccessfully() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        String chunkId = "chunk-001";
        byte[] data = "Hello AtlasFS".getBytes(StandardCharsets.UTF_8);

        String checksum = storage.putChunk(chunkId, data);

        assertNotNull(checksum);
        assertTrue(Files.exists(tempDir.resolve(chunkId + ".chunk")));
    }

    @Test
    void shouldAcceptSameChunkWithIdenticalContent() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        String chunkId = "chunk-002";
        byte[] data = "Hello AtlasFS".getBytes(StandardCharsets.UTF_8);

        String firstChecksum = storage.putChunk(chunkId, data);
        String secondChecksum = storage.putChunk(chunkId, data);

        assertEquals(firstChecksum, secondChecksum);
    }

    @Test
    void shouldRejectSameChunkIdWithDifferentContent() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        String chunkId = "chunk-003";

        byte[] firstData = "Hello AtlasFS".getBytes(StandardCharsets.UTF_8);
        byte[] secondData = "Different content".getBytes(StandardCharsets.UTF_8);

        storage.putChunk(chunkId, firstData);

        assertThrows(IOException.class, () -> storage.putChunk(chunkId, secondData));
    }

    @Test
    void shouldStoreExactChunkData() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);
        String chunkId = "chunk-004";
        byte[] originalData = "Hello AtlasFS".getBytes(StandardCharsets.UTF_8);
        storage.putChunk(chunkId, originalData);
        Path chunkPath = tempDir.resolve(chunkId + ".chunk");
        byte[] storedData = Files.readAllBytes(chunkPath);
        assertArrayEquals(originalData, storedData);
    }

    @Test
    void shouldRetrieveStoredChunkSuccessfully() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);
        String chunkId = "chunk-005";
        byte[] originalData = "Hello AtlasFS".getBytes(StandardCharsets.UTF_8);
        storage.putChunk(chunkId, originalData);
        byte[] retrievedData = storage.getChunk(chunkId);
        assertArrayEquals(originalData, retrievedData);

    }

    @Test
    void shouldDeleteChunkSuccessfully() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);
        String chunkId = "chunk-006";
        Path chunkPath = tempDir.resolve(chunkId + ".chunk");
        byte[] originalData = "Hello AtlasFS".getBytes(StandardCharsets.UTF_8);
        storage.putChunk(chunkId, originalData);
        boolean deleted = storage.deleteChunk(chunkId);

        assertTrue(deleted);
        assertFalse(Files.exists(chunkPath));

    }

    @Test
    void shouldReturnFalseWhenChunkDoesNotExist() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        boolean deleted = storage.deleteChunk("nonexistent-chunk");

        assertFalse(deleted);
    }

    @Test
    void shouldRejectChunkIdWithPathSeparators() {
        ChunkStorage storage = assertDoesNotThrow(() -> new ChunkStorage(tempDir));

        assertThrows(IllegalArgumentException.class, () -> storage.putChunk("../secret", "data".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void shouldRejectNullChunkId() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        assertThrows(IllegalArgumentException.class, () -> storage.putChunk(null, "data".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void shouldRejectEmptyChunkId() throws IOException {
        ChunkStorage storage = new ChunkStorage(tempDir);

        assertThrows(IllegalArgumentException.class, () -> storage.putChunk("", "data".getBytes(StandardCharsets.UTF_8)));
    }
    @Test 
    void shouldReturnTrueWhenChunkExists() throws IOException{
        ChunkStorage storage=new ChunkStorage(tempDir);
        String chunkId="chunk-008";
        byte[] chunkData="Hello World".getBytes(StandardCharsets.UTF_8);
        storage.putChunk(chunkId, chunkData);

        assertTrue(storage.exists(chunkId));
    }



}