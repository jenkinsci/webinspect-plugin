package me.automationdomination.plugins.webinspect.service;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: bspruth
 * Date: 8/17/14
 * Time: 10:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListProjectsProcessService {

        public HashMap<Long, String> getProjects() {
            HashMap<Long, String> projects = new HashMap<Long, String>();

            // TODO: we will need to put the gettr on the projects here for the StringTokenizer to add new line
            StringTokenizer lineTokenizer = new StringTokenizer("\n");
            while ( lineTokenizer.hasMoreTokens() ) {
                String line = lineTokenizer.nextToken();
                String[] values = line.split(",");
                //we assume a length of three means we have a line with real projects listed
                if ( values.length == 3 ) {
                    try {
                        Long projectID = Long.parseLong(values[0]);
                        projects.put(projectID, values[1] + " (" + values[2] + ")");
                    }
                    catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
            return projects;
        }



}
