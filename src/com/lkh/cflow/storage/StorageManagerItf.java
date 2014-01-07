package com.lkh.cflow.storage;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.velocity.Template;

public interface StorageManagerItf {

	public String copyModel(String devId, String modelid, String lang, String toWftid) throws Exception;

	public void copy(String pathFrom, String pathTo) throws Exception;

	public boolean doesFileExist(String path) throws Exception;

	public void delete(String path);

	public void download(String path, OutputStream outstream) throws Exception;

	public void upload(String path, String content, String contentType) throws Exception;

	public void upload(String path, File file, String contentType) throws Exception;

	public void upload(String path, long contentLength, String contentType, InputStream inputStream) throws Exception;

	public String getPrcPath(String devId, String prcId);

	public String getWftPath(String devId, String wftId);

	public String getIstWftPath(String devId, String prcId);

	public String getVtPath(String devId, String vtname);

	public String getModelPath(String modelId, String lang);

	public void guard(String path);

	public Template getTemplate(String devId, String vtname);

}
