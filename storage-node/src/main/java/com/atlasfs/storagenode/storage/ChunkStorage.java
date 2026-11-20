package com.atlasfs.storagenode.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ChunkStorage {
    
    private Path storageDirectory;


    public ChunkStorage(Path storageDirectory) throws IOException{
        this.storageDirectory=storageDirectory;
        Files.createDirectories(storageDirectory);

        if(!Files.isDirectory(storageDirectory)){
            throw new IOException("Storage path is not a directory: "+storageDirectory);
        }
    }

    public String putChunk(String chunkId, byte[] data) throws IOException{
        Path chunkPath=storageDirectory.resolve(chunkId+".chunk");
        Path tempPath=storageDirectory.resolve(chunkId+".tmp");
        Files.write(tempPath, data);
        Files.move(tempPath, chunkPath, StandardCopyOption.ATOMIC_MOVE);

        return chunkPath.toString();
    }



}
