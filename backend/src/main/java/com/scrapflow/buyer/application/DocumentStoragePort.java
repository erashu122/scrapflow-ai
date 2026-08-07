package com.scrapflow.buyer.application;
import java.io.IOException;
import java.io.InputStream;
public interface DocumentStoragePort { void store(String key, InputStream content) throws IOException; }
