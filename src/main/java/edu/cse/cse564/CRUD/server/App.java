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
        //gradebook = null;

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

        if (sid.trim().equals("") || sname.trim().equals("")) {
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
                if(existingstudents.size()!=0)
                {
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
              else
                {
                 List<Student> studentlist = new ArrayList<Student>();
                studentlist.add(newstudent);
                gradebook.setStudents(studentlist);
                    
                }
            LOG.debug("CreateStudent Complete");
            
        }
              ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            String output = mapper.writeValueAsString(newstudent);
            return Response.status(Response.Status.CREATED).entity(output).build();
        }catch (JsonParseException e) {
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
        
        if(gid.trim().equals("")||per.trim().equals(""))
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("GradeItems are empty").build();
        }
        try
        {
            if(gradebook==null || gradebook.getStudents().size()==0)
            {
                 LOG.debug("CreateGrade inComplete");
                return Response.status(Response.Status.BAD_REQUEST).entity("gradebook is null or no students in the gradebook").build();
            }
            else
            {
                List<Student> existingstudents=gradebook.getStudents();
                for(Student s:existingstudents)
                {
                    List<GradeItem> gradeitems=s.getStudentGradeItems();
                    if (gradeitems != null) {
                        for (GradeItem g : gradeitems) {
                           // System.out.println(g.getGradeID());
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
    
    /**
     * method to retrieve data of the student
     * @param args 
     */
    @GET
    @Path("/gradebook/student/{StudentID}")
    public static Response getStudentDetails(@PathParam("StudentID") String sid)
    {
        LOG.info("getting an instance of student {}",sid);
        LOG.debug("GET request");
        LOG.debug("Request Content = {}",sid);
        if(sid.equals(""))
        {
             LOG.debug("sid is ''");
            return Response.status(Response.Status.BAD_REQUEST).entity("Student id given is empty").build();
        }
        try
        {
            LOG.debug("Came into try {}",sid);
            if(gradebook==null)
            {
                LOG.debug("Gradebook is null");
                return Response.status(Response.Status.BAD_REQUEST).entity("No students assigned to this gradebook").build();
            }
            LOG.debug("Gradebook is not null");
            List<Student> studentslist=gradebook.getStudents();
            Student retrived=null;
            boolean status=false;
            for(Student s:studentslist)
            {
                if(s.getStudentID().equals(sid))
                        {
                            status=true;
                            retrived=s;
                            break;
                        }
            }
            
            if(status)
            {
                 LOG.debug("there exists a student");
                 ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
              String output = mapper.writeValueAsString(retrived);
                return Response.status(Response.Status.OK).entity(output).build();
            }
            else
            {
                 LOG.debug("no student with the given sid {}",sid);
                String message="The student ID :"+sid+" doesnot exists";
                return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
            }
            
            
        }
        catch(JsonParseException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(JsonMappingException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(IOException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    
    
     /**
     * method to retrieve data of the student
     * @param args 
     */
    @GET
    @Path("/gradebook/gradeitem/{GradeID}")
    public static Response getGradeDetails(@PathParam("GradeID") String gid)
    {
        LOG.info("getting an instance of grade {}",gid);
        LOG.debug("GET request");
        LOG.debug("Request Content = {}",gid);
        if(gid.equals(""))
        {
             LOG.debug("gid is ''");
            return Response.status(Response.Status.BAD_REQUEST).entity("Grade id given is empty").build();
        }
        try
        {
            LOG.debug("Came into try {}",gid);
            if(gradebook==null)
            {
                LOG.debug("Gradebook is null");
                return Response.status(Response.Status.BAD_REQUEST).entity("No students assigned to this gradebook").build();
            }
            LOG.debug("Gradebook is not null");
            List<GradeItem> itemlist=gradebook.getStudents().get(0).getStudentGradeItems();
            
            GradeItem retrived=null;
            boolean status=false;
            
            for(GradeItem s:itemlist)
            {
                if(s.getGradeID().equals(gid))
                        {
                            status=true;
                            retrived=s;
                            break;
                        }
            }
            
            if(status)
            {
                 LOG.debug("there exists a grade");
                 ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
              String output = mapper.writeValueAsString(retrived);
                return Response.status(Response.Status.OK).entity(output).build();
            }
            else
            {
                 LOG.debug("no grade with the given sid {}",gid);
                String message="The student ID :"+gid+" doesnot exists";
                return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
            }
            
            
        }
        catch(JsonParseException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(JsonMappingException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(IOException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    
    /**
     * retreving  entire gradebook of the class 
     * no args
     * @param args 
     */
    
    @GET
    @Path("/gradebook")
    
    public static Response getGradebookdetails()
    {
        LOG.info("Getting the Gradebook details");
        LOG.debug("GET request");
      
        if(gradebook==null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Gradebook is empty").build();
        }
        try
        {
             LOG.debug("Gradebook gathering started ");
             ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            String output=mapper.writeValueAsString(gradebook);
            LOG.info("Gradebook retrivel completed");
        
            return Response.status(Response.Status.OK).entity(output).build();
            
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
    
    
    /**
     * This method deletes particular student
     * @param sid
     * return Response
     */
    
    @DELETE
    @Path("/gradebook/student/{StudentID}")
    
    public static Response deleteStudent(@PathParam("StudentID") String sid)
    {
        LOG.info("Deleting the student with {}",sid);
        LOG.debug("DELETE request");
        
        if(sid.trim().equals(""))
        {
               LOG.debug("sid is equals to '' ");
            return Response.status(Response.Status.BAD_REQUEST).entity("The student id given is empty").build();
        }
        
        if(gradebook==null)
        {
               LOG.debug("Nothing in the gradebook");
            return Response.status(Response.Status.BAD_REQUEST).entity("Gradebook is empty cannot delete student").build();
        }
        try
        {
            List<Student> existingstudents=gradebook.getStudents();
            boolean status=false;
            Student removed=null;
            for(Student s:existingstudents)
            {
                if(s.getStudentID().equals(sid))
                {
                       LOG.debug("Found the student {}",sid);
                    status=true;
                    removed=s;
                    existingstudents.remove(s);
                     LOG.debug("breaking from the loop");
                    break;
                    
                }
            }
            if(status)
            {
                 LOG.debug("entered into the status field");
                 if(existingstudents.size()==0)
                 {
                     gradebook.setStudents(null);
                 }
                gradebook.setStudents(existingstudents);
                 ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            String output=mapper.writeValueAsString(removed);
            LOG.debug("Student delete returned reposne ");
                  
            return Response.status(Response.Status.FOUND).entity(output).build();
            }
            else
            {
                   LOG.debug("Student doesnot exists");
                String message="The student with ID: "+sid+" doesnot exists";
                return Response.status(Response.Status.NOT_FOUND).entity(message).build();
            }
        }
        catch(JsonParseException e)
        {
               LOG.debug("Exception encountered");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(JsonMappingException e)
        {
               LOG.debug("exception encountered");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(IOException e)
        {
               LOG.debug("Exception encountered");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    /**
     * This method deletes particular gradeID for all the students
     * gradeID is the argument given 
     * return response
     * @param args 
     */
   @DELETE
   @Path("/gradebook/gradeitem/{GradeID}")
   public static Response deleteGradeItemforallStudents(@PathParam("GradeID") String gid)
   {
       LOG.info("Deleting the grade with {}",gid);
        LOG.debug("DELETE request");
        
       if(gid.trim().equals(""))
       {
            LOG.debug("gid is equals to '' ");
           return Response.status(Response.Status.BAD_REQUEST).entity("The grade id given is empty").build();
       }
       if(gradebook==null)
       {
            LOG.debug("Gradebook is null ");
          return  Response.status(Response.Status.BAD_REQUEST).entity("Gradebook is null").build();
       }
       try{
            LOG.debug("gradeitem deleting started ");
           List<Student> existingstudents=gradebook.getStudents();
           
           GradeItem removed=null;
           boolean status=false;
           for(Student s:existingstudents)
           {
               List<GradeItem> gradeitems=s.getStudentGradeItems();
               for(GradeItem g:gradeitems)
               {
                   if(g.getGradeID().equals(gid))
                   {
                       status=true;
                       gradeitems.remove(g);
                       removed=g;
                       break;
                   }
               }
               if(status)
               {
                   s.setStudentGradeItems(gradeitems);
               }
               else
               {
                    LOG.debug("gid not present {} ",gid);
                   String message="the Grade with ID: "+gid+" doesnot exists";
                   return Response.status(Response.Status.NOT_FOUND).entity(message).build();
               }
           }
           
            LOG.debug("gradeid for all the students deleted {}",gid);
           gradebook.setStudents(existingstudents);
           ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            String output=mapper.writeValueAsString(removed);
           return Response.status(Response.Status.FOUND).entity(output).build();
            
           
           
       }
       catch(JsonParseException e)
       {
           return Response.status(Response.Status.BAD_REQUEST).build();
       }
       catch(JsonMappingException e)
       {
           return Response.status(Response.Status.BAD_REQUEST).build();
       }
       catch(IOException e)
       {
           return Response.status(Response.Status.BAD_REQUEST).build();
       }
   }
   
   /**
    * this method deletes particular grade for a particular student
    *
    * @param gradeid and studentid
    * return response
    */
   @DELETE
   @Path("/gradebook/{StudentID}/{GradeID}")
   
   public static Response deleteGradeforParticularStudent(@PathParam("StudentID") String sid, @PathParam("GradeID") String gid)
   {
       LOG.info("Deleting the grade for student with {} {} ",gid,sid);
        LOG.debug("DELETE request");
       if(sid.trim().equals("")||gid.trim().equals(""))
       {
           
            LOG.debug("either sid or gid is null");
          return Response.status(Response.Status.BAD_REQUEST).entity("Either StudentID or GradeID is empty").build();
       }
       
       if(gradebook==null)
       {
           LOG.debug("gradebook is null");
          return Response.status(Response.Status.BAD_REQUEST).entity("gradebook is empty").build();
       }
       else if (!sid.equals("")&& !gid.equals(""))
       {
       try{
           LOG.debug("Entered into place where gradebook is not null");
           List<Student> existingstudents=gradebook.getStudents();
           boolean gradestatus=false;
           boolean studentstatus=false;
           Student removedst=null;
           GradeItem removedgrade=null;
           
           for(Student s:existingstudents)
           {
               if(s.getStudentID().equals(sid))
               {
                   removedst=s;
                   studentstatus=true;
                   List<GradeItem> gradeitems=s.getStudentGradeItems();
                   for(GradeItem g :gradeitems)
                   {
                       if(g.getGradeID().equals(gid))
                       {
                           removedgrade=g;
                          // g.setGradeID(null);
                           //g.setPercentage(null);
                           gradestatus=true;
                           //gradeitems.remove(g);
                       }
                   }
                   if(!gradestatus && studentstatus)
                   {
                       LOG.debug(" gid is not found");
                       String message="The gradeID ID: "+gid+" doesnot exists for Student ID:"+sid+" ";
                        return Response.status(Response.Status.NOT_FOUND).entity(message).build();

                   }
                     gradeitems.remove(removedgrade); 
                   s.setStudentGradeItems(gradeitems);
               }
           }
           if(!studentstatus)
           {
               LOG.debug("sid not found ");
                String message="Student with StudentID: "+sid+ " doesnot exists ";
                 return Response.status(Response.Status.NOT_FOUND).entity(message).build();
           }
           gradebook.setStudents(existingstudents);
           ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            String output1=mapper.writeValueAsString(removedgrade);
            String output2=mapper.writeValueAsString(removedst);
            String output=output1+output2;
            LOG.debug("sid gid found");
           return Response.status(Response.Status.FOUND).entity(output).build();
           
           
       }
       
       catch(JsonParseException e)
       {
           LOG.debug("In exception case");
          return  Response.status(Response.Status.BAD_REQUEST).build();
       }
       catch(JsonMappingException e)
       {
            LOG.debug("In exception case");
           return Response.status(Response.Status.BAD_REQUEST).build();
       }
       catch(IOException e)
       {
            LOG.debug("In exception case");
          return  Response.status(Response.Status.BAD_REQUEST).build();
       }
       }
       return  Response.status(Response.Status.BAD_REQUEST).build();
       
   }

  @PUT
  @Path("/gradebook/student")
   
   public static Response updateGradeforParticularStudent(@FormParam("StudentID") String sid,@FormParam("GradeID") String gid,@FormParam("Grade") String grade,
           @FormParam("Feedback") String feedback)
   {
       LOG.info("Updating gradeitem for student with feedback and grade {} {} {} {} ",gid,sid,feedback,grade);
        LOG.debug("PUT request");
        
       if(sid.equals("")|| gid.equals("")|| grade.equals("")|| feedback.equals(""))
       {
            LOG.debug("grade or gradeitem or sid or feedback is not given");
           return Response.status(Response.Status.BAD_REQUEST).entity("Enter the grade details").build();
       }
       if(gradebook==null)
       {
            LOG.debug("gradebook is null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Gradebook is null").build();
           
       }
       try
       {
            LOG.debug("Entered into place where gradebook is not null");
           List<Student> existingstudents=gradebook.getStudents();
           boolean studentstatus=false;
           boolean gradestatus=false;
           Student modified=null;
           GradeItem modifiedgrade=null;
           
           for(Student s:existingstudents)
           {
               if(s.getStudentID().equals(sid))
                   
               {
                    LOG.debug("found student {}",sid);
                   modified=s;
                   studentstatus=true;
                   List<GradeItem> gradeitems=s.getStudentGradeItems();
                   for(GradeItem g:gradeitems)
                   {
                       if(g.getGradeID().equals(gid))
                       {
                            LOG.debug("found gradeitem {}",gid);
                           g.setGrade(grade);
                           g.setFeedback(feedback);
                           gradestatus=true;
                           modifiedgrade=g;
                       }
                   }
                   if(!gradestatus)
                   {
                        LOG.debug("gradeitem not found");
                       String message="The gradeId ID : "+gid+" doesnot exists for student studentID: "+sid+" ";
                        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
                   }
                   s.setStudentGradeItems(gradeitems);
                   
               }
           }
           if(!studentstatus)
           {
                LOG.debug("student item not found");
                String message="Student with studentID: "+sid+" doesnot exist";
                return Response.status(Response.Status.NOT_FOUND).entity(message).build();
               
           }
            LOG.debug("both found");
           gradebook.setStudents(existingstudents);
            gradebook.setStudents(existingstudents);
           ObjectMapper mapper=new ObjectMapper();
             mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            String output1=mapper.writeValueAsString(modified);
            String output2=mapper.writeValueAsString(modifiedgrade);
            String output=output1+output2;
            LOG.debug("sid gid found");
           return Response.status(Response.Status.OK).entity(output).build();
           
           
           
       }
       catch(JsonParseException e)
       {
            LOG.debug("exception caused ");
           return Response.status(Response.Status.BAD_REQUEST).build();
       }
       catch(JsonMappingException e)
       {
           LOG.debug("exception caused ");
            return Response.status(Response.Status.BAD_REQUEST).build();
       }
       catch(IOException e)
       {
           LOG.debug("exception caused ");
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
        
         r=obj.createGradeItem("123","5");
        System.out.println(r.getEntity().toString());
        
        r=obj.getStudentDetails("1");
         System.out.println(r.getEntity().toString());
         
        r=obj.getStudentDetails("4");
        System.out.println(r.getEntity().toString());
        
           r=obj.getGradebookdetails();
        System.out.println(r.getEntity().toString());
        
        r=obj.deleteStudent("1");
        System.out.println(r.getEntity().toString());
        
        r=obj.deleteStudent("8");
        System.out.println(r.getEntity().toString());
        
        r=obj.deleteGradeItemforallStudents("123");
         System.out.println(r.getEntity().toString());
         
          r=obj.createStudent("67","bbcccc");
        System.out.println(r.getEntity().toString());
          r=obj.createGradeItem("890","15");
        System.out.println(r.getEntity().toString());
         
         r=obj.deleteGradeforParticularStudent("67","890");
          System.out.println(r.getEntity().toString());
        
            r=obj.createStudent("1209209296","alekhya");
        System.out.println(r.getEntity().toString());
          r=obj.createGradeItem("midterm","15");
        System.out.println(r.getEntity().toString());
       // r=obj.updateGradeforParticularStudent("1209209296","midterm","A","goodjob");
          System.out.println(r.getEntity().toString());
          
        
                
        
    }
            

}
