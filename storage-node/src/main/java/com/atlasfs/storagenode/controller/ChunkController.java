package com.atlasfs.storagenode.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.atlasfs.storagenode.storage.ChunkStorage;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/chunks")
public class ChunkController {

    private final ChunkStorage chunkStorage;

    public ChunkController(ChunkStorage chunkStorage) {
        this.chunkStorage = chunkStorage;
    }

    @PutMapping("/{chunkId}")
    public ResponseEntity<String> uploadChunk(@PathVariable String chunkId, @RequestBody byte[] data) {
        try {
            String checksum = chunkStorage.putChunk(chunkId, data);

            return ResponseEntity.ok(checksum);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value = "/{chunkId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadChunk(@PathVariable String chunkId) {

        try {
            byte[] data = chunkStorage.getChunk(chunkId);

            return ResponseEntity.ok(data);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{chunkId}")
    public ResponseEntity<String> deleteChunk(@PathVariable String chunkId) {

        try {
            boolean deleted = chunkStorage.deleteChunk(chunkId);

            if (deleted) {
                return ResponseEntity.ok("Chunk deleted successfully");
            }

            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{chunkId}/exists")
    public ResponseEntity<Boolean> checkChunkExists(@PathVariable String chunkId) {

        try {
            boolean exists = chunkStorage.exists(chunkId);

            return ResponseEntity.ok(exists);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
