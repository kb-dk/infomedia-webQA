package dk.kb.webQA.api.impl;

import dk.kb.webQA.api.*;
import dk.kb.webQA.model.Batch;
import dk.kb.webQA.model.CharacterizationInfo;
import java.io.File;
import dk.kb.webQA.model.NewspaperDate;
import dk.kb.webQA.model.NewspaperDay;
import dk.kb.webQA.model.NewspaperID;
import dk.kb.webQA.model.Note;
import dk.kb.webQA.model.SlimBatch;
import java.net.URI;

import dk.kb.webQA.webservice.exception.ServiceException;
import dk.kb.webQA.webservice.exception.InternalServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.model.wadl.Description;
import org.apache.cxf.jaxrs.model.wadl.DocTarget;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.*;

import io.swagger.annotations.Api;

/**
 * infomedia-webqa
 *
 * <p>${project.description} 
 *
 */
public class DefaultApiServiceImpl implements DefaultApi {
    private final Logger log = LoggerFactory.getLogger(getClass());



    /* How to access the various web contexts. See https://cxf.apache.org/docs/jax-rs-basics.html#JAX-RSBasics-Contextannotations */

    @Context
    private transient UriInfo uriInfo;

    @Context
    private transient SecurityContext securityContext;

    @Context
    private transient HttpHeaders httpHeaders;

    @Context
    private transient Providers providers;

    @Context
    private transient Request request;

    @Context
    private transient ContextResolver contextResolver;

    @Context
    private transient HttpServletRequest httpServletRequest;

    @Context
    private transient HttpServletResponse httpServletResponse;

    @Context
    private transient ServletContext servletContext;

    @Context
    private transient ServletConfig servletConfig;

    @Context
    private transient MessageContext messageContext;


    @Override
    public Batch
 getBatch(String batchID) {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<NewspaperDate>
 getBatchDatesForNewspaper(String batchID, String year) {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<SlimBatch>
 getBatchForYearAndMonth(String month, String year) {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<SlimBatch>
 getBatches() {
        // TODO: Implement...
        return null;
    }

    @Override
    public Object
 getConfig() {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<NewspaperDate>
 getDatesForNewspaperYear(String newspaperID, String year) {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<CharacterizationInfo>
 getEntityCharacterization(Long handle) {
        // TODO: Implement...
        return null;
    }

    @Override
    public URI
 getEntityURL(Long handle, String type) {
        // TODO: Implement...
        return null;
    }

    @Override
    public NewspaperDay
 getNewspaperDay(String batchID, String newspaperID, String date) {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<NewspaperID>
 getNewspaperIDs() {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<Note>
 getNewspaperNotes(String avisid) {
        // TODO: Implement...
        return null;
    }

    @Override
    public Integer
 getNoteCount(String newspaperID, String batchID, String date) {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<Note>
 getNotes(String batchID) {
        // TODO: Implement...
        return null;
    }

    @Override
    public javax.ws.rs.core.StreamingOutput
 getTiffFile(String _file, String batchid) {
        // TODO: Implement...
        return null;
    }

    @Override
    public List<String>
 getYearsForNewspaper(String newspaperID) {
        // TODO: Implement...
        return null;
    }

    @Override
    public void
 removeNote(String batchID, Integer id) {
        // TODO: Implement...

    }

    @Override
    public void
 removeNotes(String avisid, Integer id) {
        // TODO: Implement...

    }

    @Override
    public void
 setNewspaperNotes(String avisid, String date, String body, String batchID, String edition, String section, String page) {
        // TODO: Implement...

    }

    @Override
    public void
 setNotes(String batchID, String date, String body, String avis, String edition, String section, String page) {
        // TODO: Implement...

    }

    @Override
    public void
 setState(String batchID, String state) {
        // TODO: Implement...

    }


    /**
    * This method simply converts any Exception into a Service exception
    * @param e: Any kind of exception
    * @return A ServiceException
    * @see dk.kb.webservice.ServiceExceptionMapper
    */
    private ServiceException handleException(Exception e) {
        if (e instanceof ServiceException) {
            return (ServiceException) e; // Do nothing - this is a declared ServiceException from within module.
        } else {// Unforseen exception (should not happen). Wrap in internal service exception
            log.error("ServiceException(HTTP 500):", e); //You probably want to log this.
            return new InternalServiceException(e.getMessage());
        }
    }

}
