package com.andrewrs.sps.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class HTTPHandler 
{
	private String urlString;
	private HttpURLConnection con;
	public HTTPHandler(String surl) throws MalformedURLException
	{
		urlString = surl;
		
	}
	public String getData(String path)
	{
		try{
			URL url=new URL(urlString+"/"+path);
			con=(HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(false);
	        InputStream io=con.getInputStream();

			byte body[] = new byte[con.getContentLength()];
			for(int i=0;i<con.getContentLength();i++)
				body[i] = (byte) io.read();
			
	        con.disconnect();
	        System.out.println("Get from:"+path+"\n   Response Code:"+con.getResponseCode() +"\n   Response Data: "+new String(body));
		       
	        return new String(body);
		}catch(Exception e)
		{
			e.printStackTrace();
			return "Error 500";
		}
	}
	public String getDataById(String path,String id)
	{
		try{
			URL url=new URL(urlString+"/"+path+":"+id);
			con=(HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(false);
	        InputStream io=con.getInputStream();

			byte body[] = new byte[con.getContentLength()];
			for(int i=0;i<con.getContentLength();i++)
				body[i] = (byte) io.read();
			//byte body[] = io.readAllBytes();
			
	        System.out.println("Get By ID "+path+":"+id+"\n   Response Code:"+con.getResponseCode() +"\n   Response Data: "+new String(body));
	        con.disconnect();
	        return new String(body);
		}catch(Exception e)
		{
			e.printStackTrace();
			return "Error 500";
		}
	}	
	public int deleteByAttribute(String path,String json) throws Exception
	{
		System.out.println("Delete by Attribute: "+path);
		try{
			URL url=new URL(urlString+"/"+path);
		con=(HttpURLConnection) url.openConnection();
		con.setRequestMethod("DELETE");
        con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);
		OutputStream io=con.getOutputStream();
		byte arr[]=json.getBytes();
		
		for(int i=0;i<arr.length;i++)
				io.write(arr[i]);
		io.flush();
        System.out.println("   Filter Data: "+json);
        InputStream input=con.getInputStream();

		byte arrResp[] = new byte[con.getContentLength()];
		for(int i=0;i<con.getContentLength();i++)
			arrResp[i] = (byte) input.read();
		//byte arrResp[] = input.readAllBytes();
		
        System.out.println("   Response Code:"+con.getResponseCode() +"\n   Response Data: "+new String(arrResp));
        int responseCode = con.getResponseCode();
		con.disconnect();   
		return responseCode;
		}catch(Exception e)
		{
			e.printStackTrace();
			return 500;
		}
	}
	public void deleteById(String path,String id)
	{
		try{
			URL url=new URL(urlString+"/"+path+":"+id);
			con=(HttpURLConnection) url.openConnection();
			con.setRequestMethod("DELETE");
			con.setDoOutput(false);
	        InputStream io=con.getInputStream();

			byte body[] = new byte[con.getContentLength()];
			for(int i=0;i<con.getContentLength();i++)
				body[i] = (byte) io.read();
			//byte body[] = io.readAllBytes();
			
	        System.out.println("Delete by ID: "+path+":"+id+"\n   Response Code:"+con.getResponseCode() +"\n   Response Data: "+new String(body));
	        con.disconnect();
	        
		}catch(Exception e)
		{
			e.printStackTrace();
	        System.out.print(500);
		}
	}
	public String jsonBuilder(Map<String,String> data)
	{
		StringBuilder appender=new StringBuilder("{\n");
		for(Map.Entry<String, String> entry:data.entrySet())
		{
			appender.append("\"");
			appender.append(entry.getKey());
			appender.append("\":\"");
			appender.append(entry.getValue());
			appender.append("\"");
		}
		return appender.toString();
	}
	public int postJsonString(String path,String json) throws Exception
	{
		System.out.println("Post To: "+path);
		try{
			URL url=new URL(urlString+"/"+path);
		con=(HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);
		OutputStream io=con.getOutputStream();
		byte arr[]=json.getBytes();
		
		for(int i=0;i<arr.length;i++)
				io.write(arr[i]);
		io.flush();
        System.out.println("   Sent Data: "+json);
        InputStream input=con.getInputStream();

		byte arrResp[] = new byte[con.getContentLength()];
		for(int i=0;i<con.getContentLength();i++)
			arrResp[i] = (byte) input.read();
		//byte arrResp[] = input.readAllBytes();
		
        System.out.println("   Response Code:"+con.getResponseCode() +"\n   Response Data: "+new String(arrResp));
        int responseCode = con.getResponseCode();
		con.disconnect();   
		return responseCode;
		}catch(Exception e)
		{
			e.printStackTrace();
			return 500;
		}
	}
	public int putJsonString(String path,String json) throws Exception
	{
		System.out.println("Put To: "+path);
		try{
			URL url=new URL(urlString+"/"+path);
		con=(HttpURLConnection) url.openConnection();
		con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);
		OutputStream io=con.getOutputStream();
		byte arr[]=json.getBytes();
		
		for(int i=0;i<arr.length;i++)
				io.write(arr[i]);
		io.flush();
        System.out.println("   Sent Data "+json);
        InputStream input=con.getInputStream();

		byte arrResp[] = new byte[con.getContentLength()];
		for(int i=0;i<con.getContentLength();i++)
			arrResp[i] = (byte) input.read();
		//byte arrResp[] = input.readAllBytes();
		
        System.out.println("   Response Code:"+con.getResponseCode() +"\n   Response Data: "+new String(arrResp));
        int responseCode = con.getResponseCode();
		con.disconnect();   
		return responseCode;
		}catch(Exception e)
		{
			e.printStackTrace();
			return 500;
		}
	}
	public String postJsonStringReturnResp(String path,String json) 
	{
		System.out.println("Post To: "+path);
		try{
			URL url=new URL(urlString+"/"+path);
		con=(HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);
		OutputStream io=con.getOutputStream();
		byte arr[]=json.getBytes();
		
		for(int i=0;i<arr.length;i++)
				io.write(arr[i]);
		io.flush();
        System.out.println("   Sent Data "+json);
        InputStream input=con.getInputStream();

		byte arrResp[] = new byte[con.getContentLength()];
		for(int i=0;i<con.getContentLength();i++)
			arrResp[i] = (byte) input.read();
		//byte arrResp[] = input.readAllBytes();
		
        System.out.println("   Response Code:"+con.getResponseCode() +"\n   Response Data: "+new String(arrResp));
		con.disconnect();   
		return new String(arrResp);
		}catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
}
