/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

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
import util.MyLogin;

/**
 *
 * @author upcnet
 */
@Stateless
@Path("entity.user")
public class UserFacadeREST extends AbstractFacade<User> {
  @PersistenceContext(unitName = "PubSubWebServerPU")
  private EntityManager em;

  public UserFacadeREST() {
    super(User.class);
  }

  @POST
  @Override
  @Consumes({"application/xml", "application/json"})
  public void create(User entity) {
    super.create(entity);
  }
  
  @POST
  @Path("create")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public User create_and_return(User entity) {
    Query query = em.createQuery("select u from User u where u.login=:login");
    query.setParameter("login", entity.getLogin());
    List list = query.getResultList();
    if(list.isEmpty()){
      em.persist(entity);
      em.flush();
      return entity;
    }
    else{
      return (User)list.get(0);
    }
  }
  
  @POST
  @Path("login")
  @Produces({"application/xml", "application/json"})
  @Consumes({"application/xml", "application/json"})
  public User login(MyLogin login) {
    System.out.println("login: "+login.login+", password: "+login.password);
    Query query = em.createQuery("select u from User u where u.login=:login AND u.password=:password");
    query.setParameter("login", login.login);
    query.setParameter("password", login.password);
    try{
      return (User)query.getSingleResult();
    }
    catch(Exception e){
      return null;
    }
  }

  @PUT
  @Path("{id}")
  @Consumes({"application/xml", "application/json"})
  public void edit(@PathParam("id") Integer id, User entity) {
    super.edit(entity);
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Integer id) {
    super.remove(super.find(id));
  }

  @GET
  @Path("{id}")
  @Produces({"application/xml", "application/json"})
  public User find(@PathParam("id") Integer id) {
    return super.find(id);
  }

  @GET
  @Override
  @Produces({"application/xml", "application/json"})
  public List<User> findAll() {
    return super.findAll();
  }

  @GET
  @Path("{from}/{to}")
  @Produces({"application/xml", "application/json"})
  public List<User> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
