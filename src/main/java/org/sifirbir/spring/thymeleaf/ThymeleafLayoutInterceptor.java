package org.sifirbir.spring.thymeleaf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sifirbir.spring.thymeleaf.annotation.DynamicLayout;
import org.sifirbir.spring.thymeleaf.annotation.Layout;
import org.sifirbir.spring.thymeleaf.annotation.NoLayout;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ThymeleafLayoutInterceptor extends HandlerInterceptorAdapter {

	private String defaultLayout = NoLayout.NAME;
	private String viewName = "view";
	
	public void setDefaultLayout(String defaultLayout) {
		this.defaultLayout = defaultLayout;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView == null || !modelAndView.hasView() || modelAndView.getView() != null) {
			return;
		}

		String originalViewName = modelAndView.getViewName();
		if (originalViewName == null || isRedirectOrForward(originalViewName)) {
			return;
		}
		
		String layoutName = getLayoutName(handler);
		if(layoutName.equals(DynamicLayout.NAME)) {
			String layoutKey = getDynamicLayoutKey(handler);
			Object layout = modelAndView.getModel().get(layoutKey);
			if(!(layout instanceof String)) {
				throw new RuntimeException("layout must be string");
			}
			layoutName = (String) layout;
		}
		
		if(layoutName.equals(NoLayout.NAME)) {
			return;
		}
		
		modelAndView.setViewName(layoutName);
		modelAndView.addObject(viewName, originalViewName);
	}

	private String getDynamicLayoutKey(Object handler) {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		DynamicLayout layout = getMethodOrTypeAnnotationDynamicLayout(handlerMethod);
		return layout.key();
	}

	private DynamicLayout getMethodOrTypeAnnotationDynamicLayout(HandlerMethod handlerMethod) {
		DynamicLayout layout = handlerMethod.getMethodAnnotation(DynamicLayout.class);
		if(layout == null) {
			layout = handlerMethod.getBeanType().getAnnotation(DynamicLayout.class);
		}
		return layout;
	}

	private String getLayoutName(Object handler) {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Layout layout = getMethodOrTypeAnnotationLayout(handlerMethod);
		if(layout == null) {
			return defaultLayout;
		} else {
			return layout.value();
		}
	}

	private Layout getMethodOrTypeAnnotationLayout(HandlerMethod handlerMethod) {
		Layout layout = handlerMethod.getMethodAnnotation(Layout.class);
		if(layout == null) {
			layout = handlerMethod.getBeanType().getAnnotation(Layout.class);
		}
		return layout;
	}

	private boolean isRedirectOrForward(String viewName) {
		return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
	}


}
