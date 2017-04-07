/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cse.cse564.CRUD.server;

import java.util.*;
import java.net.URI;
import java.util.Random;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.ws.rs.FormParam;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author acheruvu
 */
@Path("/")
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static GradeBook gradebook;

    @Context
    private UriInfo context;

    App() {
        LOG.info("Creating an App class");
        gradebook = null;

    }

    /**
     * This method creates the Student
     */
    @POST
    @Path("/gradebook/student")

    public static Response createStudent(@FormParam("StudentID") String sid, @FormParam("StudentName") String sname) {
        LOG.info("Creating the instance createStudent {}");
        LOG.debug("POST request");
        LOG.debug("Request Content = {} {} ", sid, sname);

        if (sid.equals("") || sname.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Student Details  are empty").build();
        }
        try {
            Student newstudent = new Student();
            newstudent.setStudentID(sid);
            newstudent.setStudentName(sname);

            if (gradebook == null) {
                gradebook = new GradeBook();
                List<Student> studentlist = new ArrayList<Student>();
                studentlist.add(newstudent);
                gradebook.setStudents(studentlist);
            } else {
                List<Student> existingstudents = gradebook.getStudents();
                for (Student s : existingstudents) {
                    if (s.getStudentID().equals(sid)) {
                        String message = "Student ID :" + sid + " given already exists";
                        return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                    }
                }

                List<GradeItem> existinggradeitems = existingstudents.get(0).getStudentGradeItems();
                if (existinggradeitems == null) {
                    existingstudents.add(newstudent);
                    gradebook.setStudents(existingstudents);
                } else {
                    List<GradeItem> newgradeitem = new ArrayList<GradeItem>();
                    for (GradeItem g : existinggradeitems) {
                        g.setFeedback(null);
                        g.setGrade(null);
                        newgradeitem.add(g);
                    }
                    newstudent.setStudentGradeItems(newgradeitem);
                    existingstudents.add(newstudent);
                    gradebook.setStudents(existingstudents);
                }

            }
            LOG.debug("CreateStudent Complete");
              ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            String output = mapper.writeValueAsString(newstudent);
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (JsonParseException e) {
            LOG.debug("CreateStudent Complete");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JsonMappingException e) {
            LOG.debug("CreateStudent Complete");
            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch (IOException e) {
            LOG.debug("CreateStudent Complete");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }


    /**
     * method to create GradeItem
     * @param gid,per
     * return Response
     * 
     */
    @POST
    @Path("/gradebook/gradeitem")
    
    
    
    public static Response createGradeItem(@FormParam("GradeID") String gid,@FormParam("Percentage") String per)
    {
        LOG.info("Creating the instance createGrade {}");
        LOG.debug("POST request");
        LOG.debug("Request Content = {} {} ", gid, per);

        GradeItem newgradeitem=null;
        
        if(gid.equals("")||per.equals(""))
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("GradeItems are empty").build();
        }
        try
        {
            if(gradebook==null)
            {
                 LOG.debug("CreateGrade inComplete");
                return Response.status(Response.Status.BAD_REQUEST).entity("gradebook is null").build();
            }
            else
            {
                List<Student> existingstudents=gradebook.getStudents();
                for(Student s:existingstudents)
                {
                    List<GradeItem> gradeitems=s.getStudentGradeItems();
                    if (gradeitems != null) {
                        for (GradeItem g : gradeitems) {
                            if (g.getGradeID().equals(gid)) {
                                String message = "Grade with ID: " + gid + " already exists";
                                 LOG.debug("CreateGrade inComplete");
                                return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                            }
                        }
                         LOG.debug("Grade added to existing list");
                        newgradeitem=new GradeItem();
                        newgradeitem.setGradeID(gid);
                        newgradeitem.setPercentage(per);
                        gradeitems.add(newgradeitem);
                        s.setStudentGradeItems(gradeitems);
                    }
                    else
                    {
                         LOG.debug("Grade added to new list");
                        List<GradeItem> newgradeitemlist= new ArrayList<GradeItem>();
                         newgradeitem=new GradeItem();
                        newgradeitem.setGradeID(gid);
                        newgradeitem.setPercentage(per);
                        newgradeitemlist.add(newgradeitem);
                        s.setStudentGradeItems(newgradeitemlist);
                        
                    }
                }
                
            }
             LOG.debug("CreateGrade Complete");
             ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            String output=mapper.writeValueAsString(newgradeitem);
            return Response.status(Response.Status.CREATED).entity(output).build();
            
        }
        catch(JsonParseException e)
        {
             LOG.debug("exception raised");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(JsonMappingException e)
        {
             LOG.debug("exception raised");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(IOException e)
        {
             LOG.debug("exception raised");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    


    public static void main(String[] args)
    {
        App obj=new App();
        Response r=obj.createStudent("1","aa");
        System.out.println(r.getEntity().toString());
        r=obj.createStudent("2","bb");
        System.out.println(r.getEntity().toString());
        r=obj.createStudent("","hdj");
        System.out.println(r.getEntity().toString());
        r=obj.createStudent("dh","");
        System.out.println(r.getEntity().toString());
        
        r=obj.createGradeItem("123","5");
        System.out.println(r.getEntity().toString());
        
        r=obj.createGradeItem("1234","6");
        System.out.println(r.getEntity().toString());
        
        r=obj.createGradeItem("","gyjgj");
        System.out.println(r.getEntity().toString());
        
        r=obj.createGradeItem("nvh","");
        System.out.println(r.getEntity().toString());
        
                
        
    }
            

}
