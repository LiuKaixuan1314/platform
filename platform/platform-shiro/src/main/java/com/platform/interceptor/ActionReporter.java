package com.platform.interceptor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
/**
 * URL请求参数拦截器
 * @author 周立波
 *
 */
public class ActionReporter implements HandlerInterceptor{
	private static final Log log = LogFactory.getLog(LogInterceptor.class);
	private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		}
	};
	private static Boolean bool =true;
//	private static ThreadLocal<Map<String, Long>> paramsThread = new ThreadLocal<Map<String, Long>>();

	/**
	 * Report action before action invoking when the common request coming
	 */
	static final boolean reportCommonRequest(HttpServletRequest request) {
		String content_type = request.getContentType();
		if (content_type == null || content_type.toLowerCase().indexOf("multipart") == -1) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private static final void doReport(HttpServletRequest request, Object handler,Date start) {
		StringBuilder sb = new StringBuilder("\naction report -------- ").append(sdf.get().format(new Date())).append(" ------------------------------\n");
		Class<? extends Object> cc = handler.getClass();
		sb.append("Controller        : ").append(cc.getName()).append(".(").append(cc.getSimpleName()).append(".java:1)");
		sb.append("\nMethod            : ").append(request.getServletPath()).append("\n");
		// print all parameters
		Enumeration<String> e = request.getParameterNames();
		if (e.hasMoreElements()) {
			sb.append("Parameter         : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = request.getParameterValues(name);
				if (values.length == 1) {
					sb.append(name).append("=").append(values[0]);
				} else {
					sb.append(name).append("[]={");
					for (int i = 0; i < values.length; i++) {
						if (i > 0)
							sb.append(",");
						sb.append(values[i]);
					}
					sb.append("}");
				}
				sb.append("  ");
			}
			sb.append("\n");
		}
		Date end = new Date();
		sb.append("Time Consuming    : " + (end.getTime() - start.getTime()) + "ms");
		sb.append("\n");
		sb.append("--------------------------------------------------------------------------------\n");
		System.out.print(sb.toString());
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		 	request.setAttribute("REQUEST_START_TIME", new Date());
	        return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		if (bool) {
			boolean isMultipartRequest = ActionReporter.reportCommonRequest(request);
			if (!isMultipartRequest) {
				Date start = (Date) request.getAttribute("REQUEST_START_TIME");
				doReport(request, handler,start);
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		if (bool) {
			boolean isMultipartRequest = ActionReporter.reportCommonRequest(request);
			if (isMultipartRequest) {
				Date start = (Date) request.getAttribute("REQUEST_START_TIME");
				doReport(request, handler,start);
			}
		}
	}
}
