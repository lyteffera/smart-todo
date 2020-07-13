package com.andrewrs.sps.utils;


public class JsonObjectification 
{
	public JsonObject jsonObject;
	public JsonObjectification() {}
	public JsonObjectification(String json)
	{
		String baseStartTag,baseEndTag;
		if(json.charAt(0)=='[')
		{
			baseStartTag="";
			baseEndTag="";
			jsonObject=new JsonObject(JsonObject.ARRAY, 
					new JsonObject(""),
					"base",
					"\"base\":"+baseStartTag+json+baseEndTag);
		}
		else
		{
			baseStartTag= "{";
			baseEndTag= "}";
			jsonObject=new JsonObject(JsonObject.OBJECT, 
					null,
					"base",
					"\"base\":"+baseStartTag+json+baseEndTag);
		}
	}
	public static JsonObject objectify(String json)
	{
		String baseStartTag,baseEndTag;
        JsonObject jsonObject = null;
		if(json.charAt(0)=='[')
		{
			baseStartTag="";
			baseEndTag="";
			jsonObject=new JsonObject(JsonObject.ARRAY, 
					new JsonObject(""),
					"base",
					"\"base\":"+baseStartTag+json+baseEndTag);
		}
		else
		{
			baseStartTag= "{";
			baseEndTag= "}";
			jsonObject=new JsonObject(JsonObject.OBJECT, 
					null,
					"base",
					"\"base\":"+baseStartTag+json+baseEndTag);
			
		}
        return jsonObject;
	}
	public void print()
	{
		for(JsonObject d: jsonObject.children)
			d.printData();
	}
}