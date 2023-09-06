package org.jax.mgi.fewi.searchUtil;

import java.io.PrintStream;

import org.slf4j.Logger;

public class PrinterUtil {

	private int indentAmount = 3;
	private int indents = 0;
	private String output = "";
	
	public void printi(String in) {
		indents++;
		print(in);
	}
	
	public void printu(String in) {
		print(in);
		indents--;
	}
	
	public void printiu(String in) {
		indents++;
		print(in);
		indents--;
	}
	
	public void inDent() {
		indents++;
	}
	
	public void unDent() {
		indents--;
	}
	
	public void print(String in) {
		if(output.length() == 0) output = "\n";
		String out = "";
		for(int i = 0; i < (indents * indentAmount); i++) {
			out += " ";
		}
		output += out + in + "\n";
	}
	
	public void printni(String in) {
		output += in;
	}

	public void generateOutput(Logger logger) {
		logger.debug(output);
		output = "";
	}

	public void generateOutput(PrintStream out) {
		out.println(output);
		output = "";
	}
	
	public String generateHTMLOutput() {
		return "<pre>" + generateOutput() + "</pre>";
	}

	public String generateOutput() {
		String ret = output;
		output = "";
		return ret;
	}
}