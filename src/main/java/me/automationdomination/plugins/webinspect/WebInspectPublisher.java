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
import me.automationdomination.plugins.webinspect.validation.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.PrintStream;

public class WebInspectPublisher extends Recorder {

	private final String fprScanFile;
	private final String settingsFile;
	private final String projectVersionId;

	// Fields in config.jelly must match the parameter names in the
	// "DataBoundConstructor"
	@DataBoundConstructor
	public WebInspectPublisher(
			final String fprFile, 
			final String settingsFile,
			final String projectVersionId) {
		this.fprScanFile = fprFile;
		this.settingsFile = settingsFile;
		this.projectVersionId = projectVersionId;
	}

    @Override
    public boolean perform(
    		final AbstractBuild<?, ?> build, 
    		final Launcher launcher, 
    		final BuildListener listener) {
        final PrintStream log = launcher.getListener().getLogger();
        
        log.println("hello, world!");
        
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
    
    public String getFprFile() {
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
        private static final String TOKEN_PARAMETER = "sscToken";
        private static final String WEB_INSPECT_URL_PARAMETER = "webInspectUrl";

        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String fortifyClient;
        private String sscUrl;
        private String sscToken;
        private String webInspectUrl;
        
		private final ConfigurationValueValidator fileValidator = new FileValidator();
		private final ConfigurationValueValidator simpleStringValidator = new SimpleStringValidator();
		private final ConfigurationValueValidator apiKeyStringValidator = new ApiKeyStringValidator();
		private final ConfigurationValueValidator urlValidator = new ApacheCommonsUrlValidator();

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }
        
		public FormValidation doCheckFortifyClient(@QueryParameter final String pathName) {
			if (!fileValidator.isValid(pathName))
				return FormValidation.error("Location of the fortifyclient path \"" + pathName + "\" is invalid");

			return FormValidation.ok();
		}
        
        public FormValidation doCheckSscUrl(@QueryParameter final String sscUrl) {
        	if (!urlValidator.isValid(sscUrl))
        		return FormValidation.error("SSC Fortify URL \"" + sscUrl + "\" is invalid");

        	return FormValidation.ok();
        }
        
        public FormValidation doCheckToken(@QueryParameter final String sscToken) {
        	if (!(simpleStringValidator.isValid(sscToken) && apiKeyStringValidator.isValid(sscToken)))
        		return FormValidation.error("SSC Fortify token \"" + sscToken + "\" is invalid");

        	return FormValidation.ok();
        }
        
		public FormValidation doCheckWebInspectUrl(@QueryParameter final String url) {
        	if (!urlValidator.isValid(url)) 
        		return FormValidation.error("WebInspect REST API URL \"" + url + "\" is invalid");

        	return FormValidation.ok();
		}

		public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project
			// types
			return true;
		}
		
        @Override
        public boolean configure(final StaplerRequest staplerRequest, final JSONObject formData) throws FormException {
        	// TODO: validate all these parameters
        	fortifyClient = formData.getString(FORTIFY_CLIENT_PARAMETER);
        	
        	if (!fileValidator.isValid(fortifyClient))
				throw new FormException("Location of the fortifyclient path \"" + fortifyClient + "\" is invalid", FORTIFY_CLIENT_PARAMETER);
        	
        	
        	sscUrl = formData.getString(SSC_URL_PARAMETER);

        	if (!urlValidator.isValid(sscUrl)) 
        		throw new FormException("SSC URL \"" + sscUrl + "\" is invalid", FORTIFY_CLIENT_PARAMETER);
        	
        	
        	sscToken = formData.getString(TOKEN_PARAMETER);
        	
        	if (!(simpleStringValidator.isValid(sscToken) && apiKeyStringValidator.isValid(sscToken)))
        		throw new FormException("SSC Fortify Token \"" + sscToken + "\" is invalid", TOKEN_PARAMETER);
        	
        	
        	webInspectUrl = formData.getString(WEB_INSPECT_URL_PARAMETER);
        	
        	if (!urlValidator.isValid(webInspectUrl)) 
        		throw new FormException("WebInspect REST API URL \"" + webInspectUrl + "\" is invalid", WEB_INSPECT_URL_PARAMETER);
            
        	
            save();
            return super.configure(staplerRequest,formData);
        }
        
        public ListBoxModel doFillSettingsFileItems() {
        	final ListBoxModel settingsFileItems = new ListBoxModel();
        	
        	settingsFileItems.add("default", "1");
        	settingsFileItems.add("AutomationDomination", "2");
        	settingsFileItems.add("WebGoat", "3");
        	settingsFileItems.add("Commerce-4j", "4");
        	
        	return settingsFileItems;
        }

        public ListBoxModel doFillProjectVersionIdItems() {
        	final ListBoxModel projectVersionIdItems = new ListBoxModel();
            /**

            //TODO: Need to add FortifyClientService to build list of projectVersionIdItems

            return projectVersionIdItems;
             */
        	projectVersionIdItems.add("WebGoat-snapshot", "1");
        	projectVersionIdItems.add("WebGoat-release", "2");


        	return projectVersionIdItems;
    }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return DISPLAY_NAME;
        }

		public String getFortifyClient() {
			return fortifyClient;
		}

		public String getSscUrl() {
			return sscUrl;
		}

		public String getToken() {
			return sscToken;
		}

        public String getWebInspectUrl() {
			return webInspectUrl;
		}


    }
}

