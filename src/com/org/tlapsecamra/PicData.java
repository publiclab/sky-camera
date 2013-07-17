package com.org.tlapsecamra;

public class PicData
{
	private byte[] data;
	private String name;
		
	public PicData(byte[] data, String name)
	{
		this.data = data;
		this.name = name;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public String getName()
	{
		return name;
	}
	
}