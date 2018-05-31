/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import entity.Message;
import entity.Publisher;
import entity.Topic;
import entity.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import webSocketService.WebSocketServer;

/**
 *
 * @author upcnet
 */
@Stateless
@Path("entity.publisher")
public class PublisherFacadeREST extends AbstractFacade<Publisher> {
  @PersistenceContext(unitName = "PubSubWebServerPU")
  private EntityManager em;

  public PublisherFacadeREST() {
    super(Publisher.class);
  }

  @POST
  @Override
  @Consumes({"application/xml", "application/json"})
  public void create(Publisher entity) {
    super.create(entity);
  }
  
  @POST
  @Path("create")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public Publisher create_and_return(Publisher entity) {
    //first we create the topic (if necessary):
    Query query = em.createQuery("select t from Topic t where t.name=:name");
    query.setParameter("name", entity.getTopic().getName());
    List list = query.getResultList();
    if(list.isEmpty()){
      em.persist(entity.getTopic());
      em.flush();
    }
    //then we create the publisher (if necessary):
    query = em.createQuery("select p from Publisher p where p.user=:user");
    query.setParameter("user", entity.getUser());
    list = query.getResultList();
    if(list.isEmpty()){
      em.persist(entity);
      em.flush();
      return entity;
    }
    else{
      return (Publisher)list.get(0);
    }
  }

  @POST
  @Path("delete")
  @Consumes({"application/xml", "application/json"})
  public void remove(Publisher entity) {
    //we must check if the user is really a publisher:
    Query query = em.createQuery("select p from Publisher p where p.user=:user");
    query.setParameter("user", entity.getUser());
    List list = query.getResultList();
    if(list.isEmpty()){
      return ;
    }
    else{
      //we remove the user as publisher:
      Publisher publisher = (Publisher)list.get(0);
      super.remove(publisher);
      Topic topic = publisher.getTopic();
      //we check out if he was the last publisher of that topic:
      query = em.createQuery("select p from Publisher p where p.topic=:topic");
      query.setParameter("topic", topic);
      list = query.getResultList();
      if(list.isEmpty()){
        System.out.println("no publisher left for the topic: "+topic.getName());
        query = em.createQuery("delete from Topic t where t.name=:name");
        query.setParameter("name", topic.getName());
        query.executeUpdate();
        //to warn the clients that this topic has been closed:
        Message the_message = new Message();
        the_message.setContent(topic.getName());
        topic.setName("CLOSED");
        the_message.setTopic(topic);
        WebSocketServer.notifyAll(the_message);
      }
    }
  }
  
  @POST
  @Path("publisher")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public Publisher publisherOf(User entity) {    
    Query query = em.createQuery("select p from Publisher p where p.user=:user");
    query.setParameter("user", entity);
    List list = query.getResultList();
    if(list.isEmpty()){
      return null;
    }
    else{
      return (Publisher)list.get(0);
    }
  }
  
  @PUT
  @Path("{id}")
  @Consumes({"application/xml", "application/json"})
  public void edit(@PathParam("id") Integer id, Publisher entity) {
    super.edit(entity);
  }
  
  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Integer id) {
    Publisher publisher = super.find(id);
    Topic topic = publisher.getTopic();
    super.remove(publisher);
    //we check out if he was the last publisher of that topic:
    Query query = em.createQuery("select p from Publisher p where p.topic=:topic");
    query.setParameter("topic", topic);
    List list = query.getResultList();
    if(list.isEmpty()){
      System.out.println("no publisher left for the topic: "+topic.getName());
      query = em.createQuery("delete from Topic t where t.name=:name");
      query.setParameter("name", topic.getName());
      query.executeUpdate();
      //to warn the clients that this topic has been closed:
      Message the_message = new Message();
      the_message.setContent(topic.getName());
      topic.setName("CLOSED");
      the_message.setTopic(topic);
      WebSocketServer.notifyAll(the_message);
    }
  }

  @GET
  @Path("{id}")
  @Produces({"application/xml", "application/json"})
  public Publisher find(@PathParam("id") Integer id) {
    return super.find(id);
  }

  @GET
  @Override
  @Produces({"application/xml", "application/json"})
  public List<Publisher> findAll() {
    return super.findAll();
  }

  @GET
  @Path("{from}/{to}")
  @Produces({"application/xml", "application/json"})
  public List<Publisher> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
    return super.findRange(new int[]{from, to});
  }

  @GET
  @Path("count")
  @Produces("text/plain")
  public String countREST() {
    return String.valueOf(super.count());
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
  
}
