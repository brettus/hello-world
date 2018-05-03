package com.example.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class MessageList {

	private List<ErrorMessage> messages;
	
	public MessageList(final List<ErrorMessage> messages) {
		this.messages = messages;
	}
	
	public MessageList() {
		this.messages = new ArrayList<>();
	}

	public List<ErrorMessage> getMessages() {
		return messages;
	}

	public void setMessages(final List<ErrorMessage> messages) {
		this.messages = messages;
	}
	
	public void addMessage(final ErrorMessage message) {
		this.messages.add(message);
	}

	public boolean isEmpty() {
		return messages.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageList [messages=");
		builder.append(messages);
		builder.append("]");
		return builder.toString();
	}
	
}
