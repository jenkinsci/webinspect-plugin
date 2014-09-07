package me.automationdomination.plugins.webinspect;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import me.automationdomination.plugins.webinspect.service.ssc.CmdLineFortifyClientImpl;
import me.automationdomination.plugins.webinspect.service.ssc.SscServer;
import me.automationdomination.plugins.webinspect.service.ssc.SscService;
import me.automationdomination.plugins.webinspect.service.ssc.SscServiceImpl;
import me.automationdomination.plugins.webinspect.service.webinspect.WebInspectServer;
import me.automationdomination.plugins.webinspect.service.webinspect.WebInspectServerImpl;
import me.automationdomination.plugins.webinspect.service.webinspect.WebInspectService;
import me.automationdomination.plugins.webinspect.service.webinspect.WebInspectServiceImpl;
import me.automationdomination.plugins.webinspect.validation.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



public class WebInspectPublisher extends Recorder {
	
	private static final Logger logger = Logger.getLogger(WebInspectPublisher.class.getName());

	private final String fprScanFile;
	private final String settingsFile;
	private final String projectVersionId;

	@DataBoundConstructor
	public WebInspectPublisher(
			final String fprScanFile, 
			final String settingsFile,
			final String projectVersionId) {
		this.fprScanFile = fprScanFile;
		this.settingsFile = settingsFile;
		this.projectVersionId = projectVersionId;
	}

    @Override
    public boolean perform(
    		final AbstractBuild<?, ?> build, 
    		final Launcher launcher, 
    		final BuildListener listener) {
        logger.info("using fpr scan file <" + fprScanFile + ">");
        logger.info("using settings file <" + settingsFile + ">");
        logger.info("using project version id <" + projectVersionId + ">");
        
        final String webInspectUrl = this.getDescriptor().getWebInspectUrl();
        final String fortifyClient = this.getDescriptor().getFortifyClient();
        final String sscUrl = this.getDescriptor().getSscUrl();
        final String sscToken = this.getDescriptor().getSscToken();
        
        final SscServer sscServer = new CmdLineFortifyClientImpl(fortifyClient, sscUrl, sscToken);
        final SscService sscService = new SscServiceImpl(sscServer);
        
        final WebInspectServer webInspectServer = new WebInspectServerImpl(webInspectUrl);
        final WebInspectService webInspectService = new WebInspectServiceImpl(webInspectServer);
         /*
      if (sscService.uploadScanFile != null) {
            logger.info("FPR WebInspect scan file uploaded successfully to SSC!");

        } else {
            logger.info("FPR WebInspect scan file upload failed!");
        }

        logger.info("threadfix publisher execution complete");
        }
        */

        //webInspectService.retrieveAndWriteScanFile(settingsFile, "TESTETESTE69", "/tmp/teste69.fpr");
        webInspectService.retrieveAndWriteScanFile(settingsFile, fprScanFile, fprScanFile);
        
        //sscService.uploadScanFile("/tmp/teste69.fpr");
        sscService.uploadScanFile(fprScanFile);
        
        return true;
    }

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Override
    public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE; // NONE since this is not dependent on the last step
    }

    public String getFprScanFile() {
		return fprScanFile;
	}

	public String getSettingsFile() {
		return settingsFile;
	}

	public String getProjectVersionId() {
		return projectVersionId;
	}



	
	/**
     * Descriptor for {@link WebInspectPublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/WebInspectPublisher/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		private static final String DISPLAY_NAME = "Publish WebInspect Scan";

		private static final String FORTIFY_CLIENT_PARAMETER = "fortifyClient";
		private static final String SSC_URL_PARAMETER = "sscUrl";
		private static final String SSC_TOKEN_PARAMETER = "sscToken";
		private static final String WEB_INSPECT_URL_PARAMETER = "webInspectUrl";

		private final ConfigurationValueValidator fileValidator = new FileValidator();
		private final ConfigurationValueValidator simpleStringValidator = new SimpleStringValidator();
		private final ConfigurationValueValidator apiKeyStringValidator = new ApiKeyStringValidator();
		private final ConfigurationValueValidator urlValidator = new ApacheCommonsUrlValidator();

		private String fortifyClient;
		private String sscUrl;
		private String sscToken;
		private String webInspectUrl;

		public DescriptorImpl() {
			load();
		}

		public FormValidation doCheckFortifyClient(@QueryParameter final String fortifyClient) {
			logger.finest("validating fortifyclient");

			if (!fortifyClientIsValid(fortifyClient)) {
				return FormValidation.error("Location of the fortifyclient path \"" + fortifyClient + "\" is invalid");
			}

			return FormValidation.ok();
		}

		public FormValidation doCheckSscUrl(@QueryParameter final String sscUrl) {
			logger.finest("validating ssc url");

			if (!sscUrlIsValid(sscUrl)) {
				return FormValidation.error("SSC Fortify URL \"" + sscUrl + "\" is invalid");
			}

			return FormValidation.ok();
		}
		
		public FormValidation doCheckSscToken(@QueryParameter final String sscToken) {
			logger.finest("validating ssc token");
			
			if (!sscTokenIsValid(sscToken)) {
				return FormValidation.error("SSC Fortify token \"" + sscToken + "\" is invalid");
			}

			return FormValidation.ok();
		}
		
		public FormValidation doCheckWebInspectUrl(@QueryParameter final String webInspectUrl) {
			logger.finest("validating webinspect url");
			
			if (!webInspectUrlIsValid(webInspectUrl)) {
				return FormValidation.error("WebInspect REST API URL \"" + webInspectUrl + "\" is invalid");
			}

			return FormValidation.ok();
		}

        public FormValidation doTestConnection(@QueryParameter final String webInspectUrl) throws IOException, ServletException {
            final WebInspectServer webInspectServer = new WebInspectServerImpl(webInspectUrl) {
            };
            final WebInspectService webInspectService = new WebInspectServiceImpl(webInspectServer);
            final List<String> settings = webInspectService.retrieveSettingsFiles();

            if (!settings.isEmpty()) {
                return FormValidation.ok("WebInspect connection success!");
            } else {
                return FormValidation.error("Unable to connect to WebInspect");
            }
        }

        public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project types
			return true;
		}
		
		@Override
		public boolean configure(final StaplerRequest staplerRequest, final JSONObject formData) throws FormException {
			logger.finest("saving fortifyclient");
			
			final String fortifyClientPathNameParameter = formData.getString(FORTIFY_CLIENT_PARAMETER);
 
			if (!fortifyClientIsValid(fortifyClientPathNameParameter)) {
				throw new FormException("Location of the fortifyclient path \"" + fortifyClientPathNameParameter + "\" is invalid", FORTIFY_CLIENT_PARAMETER);
			} else {
				fortifyClient = fortifyClientPathNameParameter;
				
				logger.finest("saved fortifyclient value <" + fortifyClient + ">");
			}

			
			
			logger.finest("saving ssc url");
			
			final String sscUrlParameter = formData.getString(SSC_URL_PARAMETER);

			if (!sscUrlIsValid(sscUrlParameter)) {
				throw new FormException("SSC URL \"" + sscUrlParameter + "\" is invalid", FORTIFY_CLIENT_PARAMETER);
			} else {
				sscUrl = sscUrlParameter;
				
				logger.finest("saved ssc url value <" + sscUrl + ">");
			}

			
			
			logger.finest("saving ssc token");
			
			final String sscTokenParameter = formData.getString(SSC_TOKEN_PARAMETER);

			if (!sscTokenIsValid(sscTokenParameter)) {
				throw new FormException("SSC Fortify Token \"" + sscTokenParameter + "\" is invalid", SSC_TOKEN_PARAMETER);
			} else {
				sscToken = sscTokenParameter;
				
				logger.finest("saved ssc token value <" + sscToken + ">");
			}

			
			
			logger.finest("saving webinspect url");
			
			final String webInspectUrlParameter = formData.getString(WEB_INSPECT_URL_PARAMETER);

			if (!webInspectUrlIsValid(webInspectUrlParameter)) {
				throw new FormException("WebInspect REST API URL \"" + webInspectUrlParameter + "\" is invalid", WEB_INSPECT_URL_PARAMETER);
			} else {
				webInspectUrl = webInspectUrlParameter;
				
				logger.finest("saved webinspect url value <" + webInspectUrl + ">");
			}

			
			
			save();
			
			logger.finest("configuration complete");
			
			return super.configure(staplerRequest, formData);
		}
		
		private boolean fortifyClientIsValid(final String fortifyClientPathName) {
			final boolean valid = fileValidator.isValid(fortifyClientPathName);
			
			logger.finest("fortifyclient parameter <" + fortifyClientPathName + "> is <" + (valid ? "VALID" : "INVALID") + ">");
			
			return valid;
		}
        
        private boolean sscUrlIsValid(final String sscUrl) {
        	final boolean valid = urlValidator.isValid(sscUrl);
        	
        	logger.finest("ssc url parameter <" + sscUrl + "> is <" + (valid ? "VALID" : "INVALID") + ">");
        	
        	return valid;
        }
        
        private boolean sscTokenIsValid(final String sscToken) {
        	final boolean valid = simpleStringValidator.isValid(sscToken) && apiKeyStringValidator.isValid(sscToken);
        	
        	logger.finest("ssc token parameter <" + sscToken + "> is <" + (valid ? "VALID" : "INVALID") + ">");
        	
        	return valid;
        }
        
        private boolean webInspectUrlIsValid(final String webInspectUrl) {
        	final boolean valid = urlValidator.isValid(webInspectUrl);
        	
        	logger.finest("webinspect url parameter <" + webInspectUrl + "> is <" + (valid ? "VALID" : "INVALID") + ">");
        	
        	return valid;
        }

        public ListBoxModel doFillSettingsFileItems() {
        	logger.finest("populating settings files");
        	
        	final ListBoxModel settingsFileItems = new ListBoxModel();
        	
        	final WebInspectServer webInspectServer = new WebInspectServerImpl(webInspectUrl);
        	final WebInspectService webInspectService = new WebInspectServiceImpl(webInspectServer);
        	
        	
        	
        	final List<String> settingsFiles;
        	
        	try {
        		settingsFiles = webInspectService.retrieveSettingsFiles();
        	} catch (final Exception e) {
        		logger.warning("exception retrieving settings from webinspect server");
        		settingsFileItems.add("ERROR RETRIEVING SETTINGS FROM WEBINSPECT", "Validating WebInspect Settings");
        		return settingsFileItems;
        	}
        	
        	for (final String settingsFile : settingsFiles) {
        		settingsFileItems.add(settingsFile, settingsFile);
        	}

        	return settingsFileItems;
        }

		public ListBoxModel doFillProjectVersionIdItems() {
			logger.finest("populating project versions");
			
			final ListBoxModel projectVersionItems = new ListBoxModel();
			
			final SscServer sscServer = new CmdLineFortifyClientImpl(fortifyClient, sscUrl, sscToken);
			final SscService sscService = new SscServiceImpl(sscServer);
			

			
			final Map<Integer, String> projects;

			try {
				projects = sscService.retrieveProjects();
			} catch (final Exception e) {
				logger.warning("exception retrieving projects form ssc server");
				projectVersionItems.add("ERROR RETRIEVING PROJECT VERSIONS FROM SSC SERVER", "Validating SSC Project Versions");
				return projectVersionItems;
			}

			for (final Integer projectId : projects.keySet()) {
				projectVersionItems.add(projects.get(projectId), projectId.toString());
			}

			return projectVersionItems;
		}

		public String getDisplayName() {
			return DISPLAY_NAME;
		}

		public String getFortifyClient() {
			return fortifyClient;
		}

		public String getSscUrl() {
			return sscUrl;
		}

		public String getSscToken() {
			return sscToken;
		}

		public String getWebInspectUrl() {
			return webInspectUrl;
		}

	}

}

