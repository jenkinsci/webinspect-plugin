package me.automationdomination.plugins.webinspect.service.ssc;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;



/**
 * Created with IntelliJ IDEA. User: bspruth Date: 8/17/14 Time: 10:18 PM To
 * change this template use File | Settings | File Templates.
 */
public class SscServiceImpl implements SscService {
	
	private static final int PROJECT_ID_INDEX = 0;
	private static final int PROJECT_NAME_INDEX = 1;
	private static final int PROJECT_VERSION_INDEX = 2;
	
	private static Logger logger = Logger.getLogger(SscServiceImpl.class.getName());
	
	private final SscServer sscServer;

	public SscServiceImpl(final SscServer sscServer) {
		super();
		this.sscServer = sscServer;
	}

	@Override
	public Map<Integer, String> retrieveProjects() {
		logger.fine("retrieving projects");
		
		final HashMap<Integer, String> projects = new HashMap<Integer, String>();
		
		// retrieve the projects data from the server
		final String projectsString = sscServer.retrieveProjects();

		// tokenize the projects output
		final StringTokenizer projectsStringTokenizer = new StringTokenizer(projectsString, "\n");
		
		while (projectsStringTokenizer.hasMoreTokens()) {
			final String line = projectsStringTokenizer.nextToken();
			
			final String[] values = line.split(",");
			
			// we assume a length of three means we have a line with real
			// projects listed
			if (values.length == 3) {
				try {
					final Integer projectID = Integer.parseInt(values[PROJECT_ID_INDEX]);
					final String projectName = values[PROJECT_NAME_INDEX];
					final String projectVersion = values[PROJECT_VERSION_INDEX];
					final String fullProjectName = projectName + " (" + projectVersion + ")";
					
					projects.put(projectID, fullProjectName);
				} catch (final NumberFormatException e) {
					logger.warning("skipping line <" + line + "> with unexpected project id value");
				}
			} else {
				logger.warning("skipping line <" + line + "> with unexpected number of tokens");
			}
		}
		
		return projects;
	}

	@Override
    public String uploadScanFile(final String scanFilePathName) {
		logger.info("uploading scan file path name");
		
        /*
        logger.fine("uploading FPR!");
        final Integer projectID = Integer.parseInt(values[PROJECT_ID_INDEX]);
        final String sscUrl

        private File getFPRFile(String fortifyFPRPath) {
            File myDir = new File(taskContext.getWorkingDirectory(), fortifyFPRPath);
            if (! myDir.exists()) {
                throw new InvalidFortifyFPRPathException("No FPR directory was found in " + myDir);
            }
            FilenameFilter select = new FPRFileListFilter();
            File[] contents = myDir.listFiles(select);

            // There should be no reason for more than one FPR to exist in a build so
            // we will just assume the first (and hopefully only) FPR is the correct one
            if ( contents != null && contents.length >= 1 )
                return contents[0];
            else
                throw new FPRNotFoundException("No FPR file was found in " + myDir);
        }


    }
     return fpr;
     */
        return null;
    }
}
