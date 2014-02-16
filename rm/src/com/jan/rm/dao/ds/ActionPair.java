package com.jan.rm.dao.ds;

import java.util.ArrayList;
import java.util.List;

import com.jan.rm.utils.Action;


public class ActionPair implements Action{
	
	private String title;
	private int level;
	private int count;
	
	private List<ActionPair> children;
	
	private ActionPair parent;
	
	public ActionPair(String title, int level){
		this.title = title;
		this.level = level;
		count = -1;
		
		children = new ArrayList<ActionPair>();
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void addChild(ActionPair child){
		child.setParent(this);
		children.add(child);
	}
	
	public List<ActionPair> getChildren(){
		return children;
	}
	
	public void setChildren(List<ActionPair> children){
		this.children = children;
	}
	
	public ActionPair getChild(int position){
		return children.get(position);
	}
	
	public void setParent(ActionPair parent){
		this.parent = parent;
	}
	
	public ActionPair getParent(){
		return parent;
	}
	
	public boolean hasChild(){
		return children != null && children.size() > 0;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setCount(int count){
		this.count = count;
	}
	
	public int getCount(){
		return count;
	}

	@Override
	public void act(String title, String content) {
		
	}

}
