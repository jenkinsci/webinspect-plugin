package me.automationdomination.plugins.webinspect.service;


import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bspruth
 * Date: 8/14/14
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class FortifyClientService {

    private String fortifyClient;
    private String sscUrl;
    private String sscToken;

    //private final FortifyWsClient fortifyWsClient = new FortifyWsClientImpl();
    // TODO: system command to fortifyclient

        public FortifyClientService (String sscUrl, String sscToken, String fortifyClient) {
            this.sscUrl = sscUrl;
            this.sscToken = sscToken;
            this.fortifyClient = fortifyClient;
        }
        public static void validateFortifyConnection(String sscUrl, String sscToken, String fortifyClient) {
            String[] command = {fortifyClient, "listProjectVersions", "-machineoutput", "-authtoken", sscToken, "-url", sscUrl};
            ListProjectsProcessService listProjectsProcessService = new ListProjectsProcessService ();
            //ExternalProcess process = new ExternalProcess(command , listProjectsProcessService );
            //process.execute();
            //listProjectsProcessService.throwSpecificExceptions();
            return;
        }

    public Map<Long, String> getProjects() {
        HashMap<Long, String> projects;
        String[] command = {fortifyClient, "listProjectVersions", "-machineoutput", "-authtoken", sscToken, "-url", sscUrl};
        ListProjectsProcessService listProjectsProcessService = new ListProjectsProcessService();
        //ExternalProcess process = new ExternalProcess(command , listProjectsProcessService );
        //process.execute();
        projects = listProjectsProcessService.getProjects();
        return projects;
    }

    // added for fpr
    public static void validateFortifyFPRPath(String fortifyFPRPath) throws IOException {
        File fortifyFPR = new File(fortifyFPRPath);
        if ( ! fortifyFPR.exists() ) {
            throw new IOException("That is not a valid Fortify FPR path");
        }

    }

}
