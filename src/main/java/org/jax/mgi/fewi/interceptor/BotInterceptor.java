package org.jax.mgi.fewi.interceptor;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class BotInterceptor implements HandlerInterceptor {
	
	public static ConcurrentHashMap<String, ConcurrentLinkedDeque<Date>> watchIps = new ConcurrentHashMap<String, ConcurrentLinkedDeque<Date>>();
	private static int limit = 20;

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		
		String ip = arg0.getRemoteAddr();
		String path = arg0.getPathInfo();
		
		if(path.startsWith("/assets")) {
			return true;
		}

		synchronized (ip) {
			

			ConcurrentLinkedDeque<Date> list = BotInterceptor.watchIps.get(ip);
				
			if(list == null) {
				list = new ConcurrentLinkedDeque<Date>();
				list.add(new Date());
				BotInterceptor.watchIps.put(ip, list);
			} else {
				list.add(new Date());
			}
	
			// Remove old entries from the list
			while(list.size() > limit) list.removeFirst();
			
			if(list.size() >= limit) {
				double rate = (list.getLast().getTime() - list.getFirst().getTime()) / limit;
				// Faster then one request per 1000ms 
				if(rate < 700) {
					System.out.println("Blocking Site for: " + ip + " rate: " + rate);
					return false;
				} else {
					System.out.println("IP: " + ip + " Rate: " + rate);
					System.out.println("Request: " + path);
				}
			}
		}

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception { }

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception { }

}
