package com.management.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
/*
 * Friend management Rest Controller to address below scenario or cases.
 *  1) API to create a friend connection between two email addresses.
 *  2) API to retrieve the friends list for an email address.
 *  3) API to retrieve the common friends list between two email addresses.
 *  4) API to subscribe to updates from an email address
 *  5) API to block updates from an email address.
 *  6) API to retrieve all email addresses that can receive updates from an email address.
 */

@RestController
@RequestMapping(value = "api")
public class FriendManagementController {
   //Creating the object of Concurrent HashMap
	ConcurrentHashMap<String, Friend> friendsMapping = new ConcurrentHashMap<String, Friend>();
	
	/*@method addFriend
	 *@param ResponseBody Map<String, Object> response 
	 * 1)API to create a friend connection between two email addresses.
	 * a)create a friend connection between 2 email ID
	 * b)check to user(friend) is already connected with other user(friend)
	 * c)response if user is blocked
	 //REQUEST FORMAT
	 {
  		"friends":
    	[
      	"andy@example.com",
      	"john@example.com"
    	]
		} 
	 //RESPONSE FORMAT
	    {
  		"success": true
		}
	 */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "addFriend", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> addFriend(@RequestBody Map<String, Object> inputMap,
			HttpServletRequest request) {
		System.out.println("addFriend called");
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("success", false);
		try{
		  List<String> friends = (List<String>) inputMap.get("friends");
		  if(friends==null || friends.isEmpty() || friends.size() != 2){
		    response.put("errorMsg", "Invalid input.");
            return response;
		  }
		  String user1 = friends.get(0);
		  String user2 = friends.get(1);
		  
		  if(user1.trim().equals("") || user2.trim().equals("")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
		  
		  if(!friendsMapping.containsKey(user1)){
		    friendsMapping.put(user1, new Friend(user1)); 
		  }
		  
		  if(!friendsMapping.containsKey(user2)){
            friendsMapping.put(user2, new Friend(user2)); 
          }
		  
		  Friend friend1 = friendsMapping.get(user1);
		  if(friend1.getFriendList().contains(user2)){
		    response.put("errorMsg", "Already connected.");
            return response; 
          }
		  
		  if(friend1.getBlockedMeList().contains(user2) || friend1.getMyBlockedList().contains(user2)){
            response.put("errorMsg", "user blocked");
            return response; 
          }
		  
		  if(!friend1.getFriendList().contains(user2)){
		    friendsMapping.get(user1).getFriendList().add(user2);
		    friendsMapping.get(user2).getFriendList().add(user1);
		  }
		  
		  response.put("success", true);
		}catch (Exception e) {
		  response.put("errorMsg", "Server error.");
        }
		return response;
	}
	
	/*
	 * API to retrieve the friends list for an email address.
	 * @method getFriendList
	 *@param ResponseBody Map<String, Object> response 
	 * 1) Getting the friend List and count 
	  //REQUEST FORMAT
	  	{
      	"email": "andy@example.com"
      	}
        //RESPONSE FORMAT
        {
  		"success": true,
  		"friends" :
    	[
      	'common@example.com'
    	],
  		"count" : 1   
		}
	 */
	@RequestMapping(value = "getFriendList", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> getFriendList(@RequestBody Map<String, Object> inputMap,
            HttpServletRequest request) {
		System.out.println("getFriendList called");
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try{
          String email = (String) inputMap.get("email");
          if(email==null || email.isEmpty() || email.trim().equals("")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          Friend friend = friendsMapping.get(email);
          response.put("count", 0);
          if(friend!=null && !friend.getFriendList().isEmpty()){
            response.put("friends", friend.getFriendList());
            response.put("count", friend.getFriendList().size());
          }
          response.put("success", true);
        }catch (Exception e) {
          response.put("errorMsg", "Server error.");
        }
        return response;
    }
   /*API to retrieve the common friends list between two email addresses.
    * @method getCommonFriendList
    // REQUEST FORMAT
    {
      friends:
    [
      'andy@example.com',
      'john@example.com'
    ]
      }
     // RESPONSE FORMAT
      {
  		"success": true,
  		"friends" :
    	[
      	'common@example.com'
    	],
  		"count" : 1   
		}
    */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "getCommonFriendList", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> getCommonFriendList(@RequestBody Map<String, Object> inputMap,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try{
          List<String> friends = (List<String>) inputMap.get("friends");
          if(friends==null || friends.isEmpty() || friends.size() != 2){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          String user1 = friends.get(0);
          String user2 = friends.get(1);
          
          if(user1.trim().equals("") || user2.trim().equals("")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          
          response.put("count", 0);
          if(!friendsMapping.containsKey(user1) || !friendsMapping.containsKey(user2)){
            response.put("errorMsg", "No common friends.");
          }else{
            Friend friend1 = friendsMapping.get(user1);
            Friend friend2 = friendsMapping.get(user2);
            if(!friend1.getFriendList().isEmpty() && !friend2.getFriendList().isEmpty()){
              Set<String> friends1 = new HashSet<String>(friend1.getFriendList());
              friends1.retainAll(friend2.getFriendList());
              
              response.put("friends", friends1);
              response.put("count", friends1.size());
            }
          }
          
          response.put("success", true);
        }catch (Exception e) {
          response.put("errorMsg", "Server error.");
        }
        return response;
    }
	
	/*
	 * API to subscribe to updates from an email address.
	 * REQUEST FORMAT
	 * {
      "requestor": "lisa@example.com",
      "target": "john@example.com"
       }
	 */
    @RequestMapping(value = "subscribeUpdates", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> subscribeUpdates(@RequestBody Map<String, Object> inputMap,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try{
          if(!inputMap.containsKey("requestor") || !inputMap.containsKey("target")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          String requestor = (String) inputMap.get("requestor");
          String target = (String) inputMap.get("target");
          
          if(requestor.trim().equals("") || target.trim().equals("")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          
          if(!friendsMapping.containsKey(requestor)){
            friendsMapping.put(requestor, new Friend(requestor));
          }
          
          if(!friendsMapping.containsKey(target)){
            friendsMapping.put(target, new Friend(target));
          }
          
          Friend friend = friendsMapping.get(requestor);
          if(friend.getMySubscribedList().contains(target)){
            response.put("errorMsg", "Already subscribed");
          }
          
          friendsMapping.get(requestor).getMySubscribedList().add(target);
          friendsMapping.get(target).getSubscribedMeList().add(requestor);
          
          response.put("success", true);
        }catch (Exception e) {
          response.put("errorMsg", "Server error.");
        }
        return response;
    }
	/*
	 * API to block updates from an email address.
     Suppose "andy@example.com" blocks "john@example.com":if they are connected as friends, then "andy" will no longer receive notifications from "john"
     if they are not connected as friends, then no new friends connection can be added
	 */
			
    @RequestMapping(value = "blockUpdates", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> blockUpdates(@RequestBody Map<String, Object> inputMap,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try{
          if(!inputMap.containsKey("requestor") || !inputMap.containsKey("target")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          String requestor = (String) inputMap.get("requestor");
          String target = (String) inputMap.get("target");
          
          if(requestor.trim().equals("") || target.trim().equals("")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          
          if(!friendsMapping.containsKey(requestor)){
            friendsMapping.put(requestor, new Friend(requestor));
          }
          
          if(!friendsMapping.containsKey(target)){
            friendsMapping.put(target, new Friend(target));
          }
          
          Friend friend = friendsMapping.get(requestor);
          if(friend.getMyBlockedList().contains(target)){
            response.put("errorMsg", "Already blocked");
          }
          
          friendsMapping.get(requestor).getMyBlockedList().add(target);
          friendsMapping.get(target).getBlockedMeList().add(requestor);
          
          response.put("success", true);
        }catch (Exception e) {
          response.put("errorMsg", "Server error.");
        }
        return response;
    }
    
    /*
     * API to retrieve all email addresses that can receive updates from an email address.
     * Eligibility for receiving updates from i.e. "john@example.com":
     */
    @RequestMapping(value = "getReceiveUpdates", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> receiveUpdates(@RequestBody Map<String, Object> inputMap,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try{
          if(!inputMap.containsKey("sender") || !inputMap.containsKey("text")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          String sender = (String) inputMap.get("sender");
          String text = (String) inputMap.get("text");
          
          if(sender.trim().equals("") || text.trim().equals("")){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          
          String[] arrText = text.trim().split(" ");
          if(arrText.length < 2){
            response.put("errorMsg", "Invalid input.");
            return response;
          }
          
          List<String> recipients = new ArrayList<String>();
          recipients.add(arrText[arrText.length-1]);
          if(!friendsMapping.containsKey(sender)){
            friendsMapping.put(sender, new Friend(sender));
            response.put("recipients", recipients);
          }else{
        	  
            Friend friend = friendsMapping.get(sender);
            recipients.addAll(friend.getFriendList());
            recipients.addAll(friend.getSubscribedMeList());
            if(!friend.getBlockedMeList().isEmpty())
              recipients.removeAll(friend.getBlockedMeList());
            response.put("recipients", recipients);
          }
          
          response.put("success", true);
        }catch (Exception e) {
          response.put("errorMsg", "Server error.");
        }
        return response;
    }
	
    /*
     * Friend getter setter(POJO Class) class to maintain the set
     *
      String email;
	  Set<String> friendList;
	  Set<String> blockedMeList;
	  Set<String> myBlockedList;
	  Set<String> subscribedMeList;
	  Set<String> mySubscribedList;
     * 
     */
	class Friend{
	  String email;
	  Set<String> friendList;
	  Set<String> blockedMeList;
	  Set<String> myBlockedList;
	  Set<String> subscribedMeList;
	  Set<String> mySubscribedList;
	  
      public Set<String> getFriendList() {
        return friendList;
      }
     
      public void setFriendList(Set<String> friendList) {
        this.friendList = friendList;
      }
      
      public String getEmail() {
        return email;
      }
      public void setEmail(String email) {
        this.email = email;
      }
  	  
      Friend(String email){
        this.email = email;
        this.friendList = new HashSet<String>();
        this.blockedMeList = new HashSet<String>();
        this.subscribedMeList = new HashSet<String>();
        this.myBlockedList = new HashSet<String>();
        this.mySubscribedList = new HashSet<String>();
      }
      public Set<String> getBlockedMeList() {
        return blockedMeList;
      }
      public Set<String> getMyBlockedList() {
        return myBlockedList;
      }
      public void setBlockedMeList(Set<String> blockedMeList) {
        this.blockedMeList = blockedMeList;
      }
      public void setMyBlockedList(Set<String> myBlockedList) {
        this.myBlockedList = myBlockedList;
      }

      public Set<String> getSubscribedMeList() {
        return subscribedMeList;
      }

      public Set<String> getMySubscribedList() {
        return mySubscribedList;
      }

      public void setSubscribedMeList(Set<String> subscribedMeList) {
        this.subscribedMeList = subscribedMeList;
      }

      public void setMySubscribedList(Set<String> mySubscribedList) {
        this.mySubscribedList = mySubscribedList;
      }
	}
}
