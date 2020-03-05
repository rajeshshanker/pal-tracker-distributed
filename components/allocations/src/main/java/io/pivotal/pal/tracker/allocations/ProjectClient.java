package io.pivotal.pal.tracker.allocations;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestOperations restOperations;
    private final String endpoint;
    private final Map<Long, ProjectInfo> projectsCache = new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.endpoint = registrationServerEndpoint;
    }
    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo project = null;
        logger.info("Getting project with id {} from DB", projectId) ;
        try {
             project = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
            logger.info("After retrival");
        }catch(Exception ex){
            ex.printStackTrace();
        }
        projectsCache.put(projectId, project);
        logger.info("Displaying List of projects available in DB") ;
        projectsCache.forEach((projectIdKey, projectname) -> logger.info((projectIdKey + ":" + projectname)));
        return project;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);
        logger.info("Displaying List of projects available in DB") ;
        return projectsCache.get(projectId);
    }
}
