package com.org.tlapsecamra;

public class PicData
{
	private byte[] data;
	private String name;
	private double lat;
	private double lon;
		
	public PicData(byte[] data, String name, double lat, double lon)
	{
		this.data = data;
		this.name = name;
		this.lat  = lat;
		this.lon  = lon;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getlat()
	{
		return lat;
	}
	
	public double getlon()
	{
		return lon;
	}
	
	public String getlats()
	{   
    StringBuilder b = new StringBuilder();
    b.append((int) lat);
    b.append("/1,");
    lat = (lat - (int) lat) * 60;
    b.append((int) lat);
    b.append("/1,");
    lat = (lat - (int) lat) * 60000;
    b.append((int) lat);
    b.append("/1000");
    return b.toString();
	}
	
	public String getlons()
	{
	StringBuilder b = new StringBuilder();
    b.append((int) lon);
    b.append("/1,");
    lon = (lon - (int) lon) * 60;
    b.append((int) lon);
    b.append("/1,");
    lon = (lon - (int) lon) * 60000;
    b.append((int) lon);
    b.append("/1000");
    return b.toString();}
	
}