package com.andrewrs.sps.utils;

import java.util.ArrayList;
import java.util.Collections;

public class JsonObject {
	private int dataType = -1;
	private String name="";
	private String data;
	private String originalData;
	private String sisterData;
	protected JsonObject parent;
	public ArrayList<JsonObject> children;
	public static final int STRING = 0,INT = 1,OBJECT = 2,ARRAY = 3,BOOLEAN = 4;
	public JsonObject(String json)
	{
		this.setData(json);
		this.originalData=json;
		children=new ArrayList<JsonObject>();
	}	
	public JsonObject(int level,JsonObject parentField,String fieldName, String data)
	{
		this.setData(data);
		this.originalData=this.getData();
		children=new ArrayList<JsonObject>();
		this.dataType=level;
		this.parent=parentField;
		this.name=fieldName;
		this.setData(data);
		if((parent!=null?parent.getDataType():JsonObject.OBJECT)!=JsonObject.ARRAY)
			buildObject();
		else
			buildArray();
	}
	public String getChildName(int index)
	{
		try {
		return children.get(index).getName();
		}catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	public JsonObject getChild(String pathToElement)
	{
		
		String dataPath=pathToElement;
		String nextPath="";
		if(pathToElement.indexOf('.')>0)
		{
			dataPath=pathToElement.substring(0,pathToElement.indexOf('.'));
			nextPath=pathToElement.substring(pathToElement.indexOf('.')+1, pathToElement.length());
		}
			
		for(JsonObject child : children)
		{
			//System.out.println(dataPath+" | "+child.name); 
			//This is cool because it shows the path the lookup is 
			//taking, good for traversing new datasets
			if(child.name.toLowerCase().equals(dataPath.toLowerCase()))
				if(nextPath.length()>0)
					return child.getChild(nextPath);
				else
					return child;
		}
		return null;
	}
	public JsonObject getChild(int index)
	{
		try {
		return children.get(index);
		}catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	public void appendToBeginning(String s)
	{
		setData(s+getData());
	}
	public String getName()
	{
		return name;
	}
	private void buildArray() 
	{
		int begin=0;
		//begin=getJson().indexOf("\"", begin);
		//int end=getJson().indexOf("\"", begin+1)+1;
		int endOfField=-1;
		name=parent.children.size()+"";
		//System.out.println(getJson().charAt(end+1));

		if (getData().charAt(begin)=='{')
		{
			dataType=JsonObject.OBJECT;
			String workingJson=originalData.substring(begin, originalData.length());
			endOfField=getEnclosingCharIndex("{",workingJson,"{","}")+begin;
			children.add(new JsonObject(JsonObject.OBJECT,this,name,getData().substring(begin+1, endOfField+1)));
			if(originalData.length()>endOfField)
				if(originalData.charAt(endOfField)==',' ||
						originalData.charAt(originalData.length()>endOfField+1?endOfField+1:endOfField)==',')
				{
					int sisterBegin=returnMin(originalData.indexOf("\"",endOfField),
							originalData.indexOf("\\{",endOfField));
					sisterBegin=returnMin(sisterBegin,originalData.indexOf("[",endOfField))-1;
					sisterData=originalData.substring(sisterBegin);
					
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(sisterBegin, 1+originalData.indexOf("\"",endOfField+2)),
							sisterData));
				}
		}
		else if (getData().charAt(begin)=='[')
		{

			dataType=JsonObject.ARRAY;
			endOfField= getEnclosingCharIndex("[",getData(),"[","]");
			children.add(new JsonObject(-1,this,name+" "+parent.children.size(),data.substring(
					data.indexOf('['), endOfField)));
			if(originalData.length()>endOfField)
				if(originalData.charAt(endOfField)==',' ||
						originalData.charAt(originalData.length()>endOfField+1?endOfField+1:endOfField)==',')
				{
					String sisterData=originalData.substring(originalData.indexOf("\"",endOfField+2),
							originalData.length());
					
					parent.children.add(new JsonObject(JsonObject.ARRAY,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField), 
							1+originalData.indexOf("\"",endOfField+2)),
							sisterData));
				}
			for(JsonObject child:children)
			{
				child.setName(Integer.toString(children.indexOf(child)));
			}
		}
		else if(getData().charAt(begin)=='\"')
		{
			dataType=JsonObject.STRING;
			endOfField=getData().indexOf("\"", begin+2);
			setData(getData().substring(begin+2, endOfField));
			if(parent.getDataType()==2)
				endOfField++;
			if(originalData.length()>=endOfField+1)
				if(originalData.charAt(endOfField+1)==',')
				{
					sisterData=originalData.substring(originalData.indexOf("\"",endOfField+2),originalData.length());
					if(!sisterData.endsWith("}"))
					{
						sisterData+='}';
					}
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField+2), originalData.indexOf("\"",endOfField+3)),
							sisterData));
				}
		}
		else if(Character.isDigit(getData().charAt(0)))
		{
			dataType=JsonObject.INT;
			int fieldEndIndex=returnMin(getData().indexOf("}", begin+1),getData().indexOf("]", begin+1));
			fieldEndIndex=returnMin(getData().indexOf(",", begin+1),fieldEndIndex);
			endOfField=fieldEndIndex;
			
			setData(getData().substring(begin+1, endOfField+1).replaceAll("[^0-9]", ""));		
			if(originalData.length()>=endOfField+1)
				if(originalData.charAt(endOfField)==',')
				{
					sisterData=originalData.substring(originalData.indexOf("\"",endOfField),originalData.length());
					if(!sisterData.endsWith("}"))
					{
						sisterData+='}';
					}
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField), originalData.indexOf("\"",endOfField+2)),
							sisterData));
				}
		}
		else if(getData().charAt(0)=='t' || getData().charAt(0)=='f')
		{
			dataType=JsonObject.BOOLEAN;
			int fieldEndIndex=returnMin(getData().indexOf("}", begin+1),getData().indexOf("]", begin+1));
			fieldEndIndex=returnMin(getData().indexOf(",", begin+1),fieldEndIndex);
			endOfField=fieldEndIndex;

				setData(getData().substring(begin +1, endOfField));
			
			if(originalData.length()>=endOfField+1)
				if(originalData.charAt(endOfField)==',')
				{
					sisterData=originalData.substring(originalData.indexOf("\"",endOfField),originalData.length());
					if(!sisterData.endsWith("}"))
					{
						sisterData+='}';
					}
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField), originalData.indexOf("\"",endOfField+2)),
							sisterData));
				}
		}
		Collections.reverse(children);
		
	}
	public void setName(String name)
	{
		this.name=name;
	}
	private int getDataType() 
	{
		return dataType;
	}
	private void buildObject()
	{
		int begin=0;
		begin=getData().indexOf("\"", begin);
		int end=getData().indexOf("\"", begin+1)+1;
		int endOfField=-1;
		name=getData().substring(begin+1, end-1);
		if (getData().charAt(end+1)=='{')
		{
			dataType=JsonObject.OBJECT;
			String workingJson=originalData.substring(begin, originalData.length());
			endOfField=getEnclosingCharIndex(name,workingJson,"{","}")+begin;
			children.add(new JsonObject(JsonObject.OBJECT,this,name,getData().substring(end+1, endOfField+1)));
			if(originalData.length()>endOfField)
				if(originalData.charAt(endOfField)==',' ||
						originalData.charAt(originalData.length()>endOfField+1?endOfField+1:endOfField)==',')
				{
					sisterData=originalData.substring(originalData.indexOf("\"",endOfField+2),originalData.length());
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField), originalData.indexOf("\"",endOfField+2)),
							sisterData));
				}
		}
		else if (getData().charAt(end+1)=='[')
		{

			dataType=JsonObject.ARRAY;
			endOfField= getEnclosingCharIndex(name,getData(),"[","]");
			if(getData().substring(end+2, endOfField).replaceAll("\\[", "").replaceAll("\\]", "").length()>0)
				children.add(new JsonObject(-1,this,name+" "+(parent.children.size()+1),getData().substring(end+2, endOfField)));
			if(originalData.length()>=endOfField+1)
				if(originalData.charAt(endOfField)==',' ||
						originalData.charAt(originalData.length()>endOfField+1?endOfField+1:endOfField)==',')
				{
					sisterData=originalData.substring(originalData.indexOf("\"",endOfField+2),originalData.length());
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField), originalData.indexOf("\"",endOfField+2)),
							sisterData));
				}
		}
		else if(getData().charAt(end+1)=='\"')
		{
			dataType=JsonObject.STRING;
			endOfField=getData().indexOf("\"", end+2);
			setData(getData().substring(end+2, endOfField));
			if(originalData.length()>=endOfField+1)
				if(originalData.charAt(endOfField+1)==',')
				{
					sisterData=originalData.substring(originalData.indexOf("\"",endOfField+2),originalData.length());
					if(!sisterData.endsWith("}"))
					{
						sisterData+='}';
					}
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField+2), originalData.indexOf("\"",endOfField+3)),
							sisterData));
				}
		}
		else if(Character.isDigit(getData().charAt(end+1)))
		{
			dataType=JsonObject.INT;
			int fieldEndIndex=returnMin(getData().indexOf("}", end+1),getData().indexOf("]", end+1));
			fieldEndIndex=returnMin(getData().indexOf(",", end+1),fieldEndIndex);
			endOfField=fieldEndIndex;
			
			setData(getData().substring(end+1, endOfField+1).replaceAll("[^0-9]", ""));		
			if(originalData.length()>=endOfField+1)
				if(originalData.charAt(endOfField)==',')
				{
					sisterData=originalData.substring(originalData.indexOf("\"",endOfField),originalData.length());
					if(!sisterData.endsWith("}"))
					{
						sisterData+='}';
					}
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField), originalData.indexOf("\"",endOfField+2)),
							sisterData));
				}
		}
		else if(getData().charAt(end+1)=='t' || getData().charAt(end+1)=='f')
		{
			dataType=JsonObject.BOOLEAN;
			int fieldEndIndex=returnMin(getData().indexOf("}", end+1),getData().indexOf("]", end+1));
			fieldEndIndex=returnMin(getData().indexOf(",", end+1),fieldEndIndex);
			endOfField=fieldEndIndex;

				setData(getData().substring(end +1, endOfField));	
				
			if(originalData.length()>=endOfField+1)
				if(originalData.charAt(endOfField)==',')
				{
					sisterData=originalData.substring(originalData.indexOf("\"",endOfField),originalData.length());
					if(!sisterData.endsWith("}"))
					{
						sisterData+='}';
					}
					parent.children.add(new JsonObject(-1,
							parent,
							originalData.substring(originalData.indexOf("\"",endOfField), originalData.indexOf("\"",endOfField+2)),
							sisterData));
				}
		}
		
		Collections.reverse(children);
		if(this.getDataType()==JsonObject.ARRAY)
			for(JsonObject child:children)
			{
				child.setName(Integer.toString(children.indexOf(child)));
			}
		
		
	}
	private int returnMin(int a,int b)
	{
		if(a > -1 && b > -1)
			return a<b ? a:b;
		else if(b>-1)
			return b;
		else return a;
	}
	private int getEnclosingCharIndex(String fieldName,String data,String start,String end)
	{
		int startCounter=1,endCounter=0;
		final int startIndex=data.indexOf(fieldName)+fieldName.length()+3;
		int cStart=startIndex+1,cEnd=0;
		int lastStartIndex=0,lastEndIndex=0;
		while(startCounter>endCounter && data.indexOf(end, cEnd+end.length())>lastEndIndex)
		{
			cStart=data.indexOf(start, cStart+start.length());
			cEnd=data.indexOf(end,cEnd+1);
			
				if(lastStartIndex<cStart && cStart<cEnd)
					startCounter++;
				if(lastEndIndex<cEnd)
					endCounter++;
			lastStartIndex=cStart;
			lastEndIndex=cEnd;
		}
		return cEnd;
		
	}
	public JsonObject nextSister(JsonObject previousSister)
	{
		int nextIndex=1+parent.children.indexOf(previousSister);
		JsonObject nextSister;
		try{
			nextSister=parent.children.get(nextIndex);
		}catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace();
			nextSister=null;
		}
		return nextSister;
	}
	public void printData()
	{
		if(this.dataType==STRING || this.dataType==INT || this.dataType==BOOLEAN)
			System.out.println(name+": \""+getData()+"\"");
		else
		{
			if(this.getDataType()!=JsonObject.ARRAY)
			{
				System.out.println(name+":\n{");
				for(JsonObject data : children)
				{
					data.printData();
				}
				System.out.println("\n}");
			}
			else
			{
					System.out.println(name+":\n[");
					for(JsonObject data : children)
					{
						data.printData();
					}
					System.out.println("\n]");
			}
		}
	}
	public String getData() 
	{
		return data;
	}
    public ArrayList<JsonObject> getChildren()
    {
        return children;
    }
	public void setData(String json) 
	{
		this.data = json;
	}
	public JsonObject getParent() 
	{
		return parent;
	}
}
