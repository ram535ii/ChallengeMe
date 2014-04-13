package com.example.PA2;

import java.io.Serializable;

public class Person implements Serializable {
		private String name;
		private int age;
		private String chatMsg;
		private String AveSpeed;
		private static final long serialVersionUID = 1L; 
		public String getName(){
			return name;
		}
		public void setName(String name){
			this.name = name;
		}
		public int getAge(){
			return age;
			
		}
		public void setAge(int age){
			this.age = age;
		}
		public String getChatMsg(){
			return chatMsg;
		}
		public void setChatMsg(String chat){
			this.chatMsg = chat;
		}
		public String getSpeed(){
			return AveSpeed;
		}
		public void setSpeed(String AveSpd){
			this.AveSpeed = AveSpd;
		}
		public String infotoString(){
			return "\nName = " + name + "\nAge = " + age + "\nSays: " + chatMsg;
		}
		public String showResults(){
			return "\nUser: " + name + "\nAverage Speed: " + AveSpeed;
		}
}
