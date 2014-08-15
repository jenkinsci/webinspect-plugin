package me.automationdomination.plugins.webinspect.service;

/**
 * Created with IntelliJ IDEA.
 * User: bspruth
 * Date: 8/14/14
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class FortifyClientService {

    private final String sscUrl;
    private final String token;


    //private final FortifyWsClient fortifyWsClient = new FortifyWsClientImpl();

    public FortifyClientService(
            final String sscUrl,
            final String token) {
        super();
        this.sscUrl = sscUrl;
        this.token = token;
    }

    // TODO: system command to fortifyclient
    //
}
