package com.example.lifeassistant_project.activity_update.packages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class DataPackage {
    protected int connectionTimeout = 5000;
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {return null;};
}
