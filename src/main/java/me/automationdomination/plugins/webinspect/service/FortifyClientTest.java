/**
 * Created with IntelliJ IDEA.
 * User: bspruth
 * Date: 8/29/14
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

    public class FortifyClientTest {

        private static String fortifyClient="/Applications/HP_Fortify/HP_Fortify_SCA_and_Apps_4.10/bin/fortifyclient";
        private static String sscUrl="https://packetnoodle.automationdomination.me/ssc";
        private static String sscToken="41ff726a-1b08-4a77-8338-c77b1646c271";


        public static void main (String [] args) throws IOException {
            HashMap<Long, String> projects;
            String[] command = {fortifyClient, "listProjectVersions", "-machineoutput", "-authtoken", sscToken, "-url", sscUrl};
            //execute the command
            Process ListProjects = Runtime.getRuntime().exec(command);
            //read output
            BufferedReader Resultset = new BufferedReader(
                    new InputStreamReader (
                            ListProjects.getInputStream()));
            //print output
            String line;
            while ((line = Resultset.readLine()) != null) {
                System.out.println(line);
            }
        }
        }

