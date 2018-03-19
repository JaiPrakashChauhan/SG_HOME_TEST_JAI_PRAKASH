package com.management.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jai prakash Chauhan
 * and content type text/plain
 */
@WebServlet("/helloFriends")
public class HelloWorldServlet extends HttpServlet {

   private static final long serialVersionUID = 1L;

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
         throws ServletException, IOException {
      resp.setContentType("text/plain");
      resp.getWriter().write("Hello Friends! This controller is only for test purpose.");
   }
}