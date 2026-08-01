package com.example.oauth_client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@Controller
@ResponseBody
class GreetingsController {

	@GetMapping("/")
	Map<String, String> me(Principal principal) {
		return Map.of("name", principal.getName());
	}

}
