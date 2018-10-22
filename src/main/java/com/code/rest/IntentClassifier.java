package com.code.rest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/intent")
public class IntentClassifier {

	@PostMapping(value="/this", consumes="application/json", produces="text/plain")
	public String s(@RequestBody Soln reques) throws Exception{
		String sentences[] = reques.getEmail().split("[.\n] *");
		HttpClient client = new DefaultHttpClient();
		boolean help=false;
		boolean issue=false;
		Ret ret = new Ret();
		for(String str:sentences) {
			if(!str.equals("")) {
				URL url = new URL("https://westus.api.cognitive.microsoft.com/luis/v2.0/apps/69e7d9c1-eb8b-4f01-988f-ce610880fff3?subscription-key=ee37480989984715b1bc22d3df6756ad&timezoneOffset=-360&q="+URLEncoder.encode(str,"UTF-8"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				br.readLine();
				br.readLine();
				br.readLine();
				String[] st = br.readLine().split("\"");
				if(st[3].equals("Help")) {
					return "Raise a ticket";
				}
				if(st[3].equals("Issue"))
					issue = true;
			}
		}
		if(issue) {
			return "Manual intervention required";
		}
		return "None";
	}
}

class Soln{
	String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

class Ret{
	String sentences;

	public String getSentences() {
		return sentences;
	}

	public void setSentences(String sentences) {
		this.sentences = sentences;
	}
}
