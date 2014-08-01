package me.automationdomination.plugins.webinspect;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.PrintStream;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link WebInspectPublisher} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class WebInspectPublisher extends Recorder {

	private final String fprFile;
	private final String settingsFile;
	private final String projectVersionId;

	// Fields in config.jelly must match the parameter names in the
	// "DataBoundConstructor"
	@DataBoundConstructor
	public WebInspectPublisher(
			final String fprFile, 
			final String settingsFile,
			final String projectVersionId) {
		this.fprFile = fprFile;
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
        return fprFile;
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

        private static final String FORTIFY_CLIENT_PATH_NAME_PARAMETER = "fortifyClientPathName";
        private static final String SSC_URL_PARAMETER = "sscUrl";
        private static final String TOKEN_PARAMETER = "token";
        private static final String WEB_INSPECT_URL_PARAMETER = "webInspectUrl";

        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String fortifyClientPathName;
        private String sscUrl;
        private String token;
        private String webInspectUrl;

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }
        
        public FormValidation doCheckFortifyClientPathName(@QueryParameter String value) {
        	// TODO: implement me
        	return FormValidation.ok();
        }
        
        public FormValidation doCheckSscUrl(@QueryParameter String value) {
        	// TODO: implement me
        	return FormValidation.ok();
        }
        
        public FormValidation doCheckToken(@QueryParameter String value) {
        	// TODO: implement me
        	return FormValidation.ok();
        }
        
		public FormValidation doCheckWebInspectUrl(@QueryParameter String value) {
			// TODO: implement me
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
        	fortifyClientPathName = formData.getString(FORTIFY_CLIENT_PATH_NAME_PARAMETER);
        	sscUrl = formData.getString(SSC_URL_PARAMETER);
        	token = formData.getString(TOKEN_PARAMETER);
        	webInspectUrl = formData.getString(WEB_INSPECT_URL_PARAMETER);
            
            save();
            return super.configure(staplerRequest,formData);
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return DISPLAY_NAME;
        }

		public String getFortifyClientPathName() {
			return fortifyClientPathName;
		}

		public String getSscUrl() {
			return sscUrl;
		}

		public String getToken() {
			return token;
		}

		public String getWebInspectUrl() {
			return webInspectUrl;
		}

    }
}

