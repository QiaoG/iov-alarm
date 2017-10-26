/** 
 * @author gq
 * @date 2015年11月3日 下午2:14:37 
 */
package com.hxht.iov.alarm.web;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping(value = "/", method = GET)
	public String home() {
		return "home";
	}
}
