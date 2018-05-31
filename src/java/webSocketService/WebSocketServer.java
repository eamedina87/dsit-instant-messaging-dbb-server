package webSocketService;

import com.google.gson.Gson;
import entity.Message;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import util.MySubscription;

@ServerEndpoint("/ws")
public class WebSocketServer {

  //Open connections from clients:
  private static Set<Session> sessions = new HashSet<Session>();
  //Subscriptions from each client (session):
  private static Map<Session, List<String>> subscriptions = new HashMap<Session, List<String>>();

  @OnMessage
  public void onMessage(String message, Session session)
    throws IOException, SQLException {
    System.out.println("onMessage: " + message);

    Gson gson = new Gson();
    MySubscription mySubscription = gson.fromJson(message, MySubscription.class);
    if(mySubscription.type){
      subscriptions.get(session).add(mySubscription.topic);
    }
    else{
      subscriptions.get(session).remove(mySubscription.topic);
    }
  }

  @OnOpen
  public void onOpen(Session session) {
    sessions.add(session);
    subscriptions.put(session, new ArrayList<String>());
    System.out.println("new session: " + session.getId());
  }

  @OnClose
  public void onClose(Session session) {
    System.out.println("closed session: " + session.getId());
    sessions.remove(session);
    subscriptions.remove(session);
  }

  public static void notifyAll(Message message) {
    Gson gson = new Gson();
    String json_message = gson.toJson(message);
    String topic_name = message.getTopic().getName();
    try {
      /* Send the notification to all open WebSocket sessions */
      ArrayList<Session> closedSessions = new ArrayList<>();
      for (Session session : sessions) {
        if (!session.isOpen()) {
          System.out.println("Closed session: " + session.getId());
          closedSessions.add(session);
        } else {
          if (subscriptions.containsKey(session) && 
              subscriptions.get(session).contains(topic_name)) {
            session.getBasicRemote().sendText(json_message);
            System.out.println("Sending: " + json_message + "\nto session_Id:" + session.getId());
          } 
          else {
            if (subscriptions.containsKey(session) && 
                topic_name.equals("CLOSED") && 
                subscriptions.get(session).contains(message.getContent())) {
              session.getBasicRemote().sendText(json_message);
              System.out.println("Closing: " + json_message + "\nto session_Id:" + session.getId());
            }
          }
        }
      }
      sessions.removeAll(closedSessions);
      subscriptions.remove(closedSessions);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

}
