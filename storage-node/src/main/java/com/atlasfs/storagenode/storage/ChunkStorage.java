package com.atlasfs.storagenode.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChunkStorage {
    
    private Path storageDirectory;


    public ChunkStorage(Path storageDirectory) throws IOException{
        this.storageDirectory=storageDirectory;
        Files.createDirectories(storageDirectory);

        if(!Files.isDirectory(storageDirectory)){
            throw new IOException("Storage path is not a directory: "+storageDirectory);
        }
    }



}
